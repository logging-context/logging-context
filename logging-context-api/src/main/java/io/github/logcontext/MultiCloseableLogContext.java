package io.github.logcontext;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import java.util.List;

/**
 * The MultiCloseableLogContext class provides a {@link LogContext} that will close a group of
 * {@link AutoCloseable} instances as a single close operation. The {@link AutoCloseable#close()}
 * method willed be called on every non-null instance provided. Any exceptions thrown will be
 * suppressed.
 */
public class MultiCloseableLogContext implements LogContext {

  /** The list of auto-closeable contexts that should be closed as a group. */
  private final List<AutoCloseable> closeables;

  /**
   * Constructs a new instance of MultiCloseableLogContext.
   *
   * @param closeables the list of auto-closeable contexts to close as a group (must not be <code>
   *     null</code>).
   * @throws NullPointerException if <code>closeables</code> is <code>null</code>.
   */
  public MultiCloseableLogContext(final List<AutoCloseable> closeables) {
    this.closeables = unmodifiableList(requireNonNull(closeables, "closeables must not be null"));
  }

  @Override
  public void close() {
    for (AutoCloseable closeable : closeables) {
      try {
        if (!isNull(closeable)) {
          closeable.close();
        }
      } catch (final Exception e) {
        // Suppress autocloseable exceptions
      }
    }
  }
}
