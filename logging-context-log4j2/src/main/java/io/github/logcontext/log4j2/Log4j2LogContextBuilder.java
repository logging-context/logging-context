package io.github.logcontext.log4j2;

import io.github.logcontext.AbstractLogContextBuilder;
import io.github.logcontext.LogContext;
import io.github.logcontext.LogContext.Builder;
import org.apache.logging.log4j.CloseableThreadContext;

/**
 * The Log4j2LogContextBuilder class provides a {@link LogContext.Builder} implementation that
 * updates the Log4j logging contexts with additional nested and mapped context values. The logging
 * context will not be updated until the {@link #get()} method is called. Subsequent calls to {@link
 * #get()} will update the context with the same values.
 */
public class Log4j2LogContextBuilder extends AbstractLogContextBuilder {



  /**
   * Constructs a new instance of {@link Builder}. This method is not intended for direct use.
   *
   * @see LogContext.Builder#nestedContext(String...)
   * @see LogContext.Builder#mappedContext(String, String)
   */
  public Log4j2LogContextBuilder() {
    super();
  }

  /**
   * Updates the Nested Diagnostic Context and Mapped Diagnostic Contexts with the configured values
   * and returns a {@link LogContext} that can be used to remove the logging context updates.
   *
   * @return a {@link LogContext} that can be used to remove the updates to the logging contexts.
   */
  public LogContext get() {
    CloseableThreadContext.Instance context = CloseableThreadContext.pushAll(getNestedContexts());
    context = context.putAll(getMappedContextValues());
    return LogContext.closeableLogContext(context);
  }
}
