package io.github.logcontext;

import static java.util.Objects.requireNonNull;

import java.io.Closeable;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

/**
 * The LogContext class provides an {@link AutoCloseable} instance that will remove the associated
 * logging context values from the current logging context.
 */
public interface LogContext extends AutoCloseable {

  /**
   * Returns a wrapper around an {@link AutoCloseable} to treat it as a LogContext.
   *
   * @param closeable the {@link AutoCloseable} instance to wrap as a LogContext (must not be <code>
   *     null</code>).
   * @return a LogContext representation of the {@link AutoCloseable}.
   * @throws NullPointerException if <code>closeable</code> is <code>null</code>.
   */
  static LogContext closeableLogContext(final AutoCloseable closeable) {
    requireNonNull(closeable, "closeable must not be null");
    return () -> {
      try {
        closeable.close();
      } catch (Exception e) {
        // Suppress autoclosed exceptions
      }
    };
  }

  /**
   * Closes the log context. Log context closure should not result in an {@link Exception}.
   */
  @Override
  void close();

  /**
   * The {@link Builder} class provides a utility for constructing additional logging context
   * information and creating a {@link LogContext} that can be used to remove the added context
   * information.
   *
   * <p>This implementation assumes that values should not be duplicated in the Nested Diagnostic
   * Context. If the nested context already contains a context value, it will not be added again and
   * not marked for removal by the returned {@link LogContext}.
   *
   * <p>For cases where a Mapped Diagnostic Context value already exists, the existing value will be
   * overwritten, but it will <em>not</em> be marked for removal by the {@link LogContext} and the
   * original value will not be restored when the {@link LogContext} is closed. It will be up to the
   * original source of the MDC key to remove it.
   */
  interface Builder extends Supplier<LogContext> {

    /**
     * Retrieves an instance of the builder to use when creating the logging context.
     *
     * @return an instance of the builder to use when creating the logging context.
     */
    static Builder builder() {
      ServiceLoader<LoggingContextServiceProvider> serviceLoader =
          ServiceLoader.load(LoggingContextServiceProvider.class);
      return StreamSupport.stream(serviceLoader.spliterator(), false)
          .findFirst()
          .map(LoggingContextServiceProvider::logContextBuilder)
          .orElse(NO_OP_BUILDER);
    }

    /**
     * Creates a new {@link Builder} that will add the supplied contexts to the nested context.
     *
     * @param context the context values to add to the logging context.
     * @return a reference to the {@link Builder} for chaining.
     */
    static Builder nestedContext(final String... context) {
      return builder().andNested(context);
    }

    /**
     * Creates a new {@link Builder} that will include the provided named context and its value.
     *
     * @param context the name of the MDC key associated with the value.
     * @param value the value of the MDC attribute.
     * @return a reference to the {@link Builder} for chaining.
     */
    static Builder mappedContext(final String context, final String value) {
      return builder().andMapped(context, value);
    }

    /**
     * Adds additional Nested Diagnostic Context (NDC) information to the logging context.
     *
     * @param context the NDC values to add to the logging context.
     * @return a reference to the {Builder} for chaining.
     */
    Builder andNested(final String... context);

    /**
     * Adds an additional Mapped Diagnostic Context (MDC) value to the logging context.
     *
     * @param context the name of the MDC key associated with the value to be added.
     * @param value the value of the MDC attribute.
     * @return a reference to the {@link Builder} for chaining.
     */
    Builder andMapped(final String context, final String value);

    /**
     * Updates the Nested Diagnostic Context and Mapped Diagnostic Contexts with the configured
     * values and returns a {@link LogContext} that can be used to remove the logging context
     * updates.
     *
     * @return a {@link LogContext} that can be used to remove the updates to the logging contexts.
     */
    @Override
    LogContext get();

    /**
     * A default {@link Builder} implementation that does nothing with the provided contexts and
     * returns an empty {@link Closeable} for the {@link LogContext}. This implementation is only
     * meant to be used if the service loader cannot find an applicable instance of the {@link
     * Builder}.
     */
    Builder NO_OP_BUILDER =
        new Builder() {

          @Override
          public Builder andNested(String... context) {
            return this;
          }

          @Override
          public Builder andMapped(String context, String value) {
            return this;
          }

          @Override
          public LogContext get() {
            return () -> {};
          }
        };
  }
}
