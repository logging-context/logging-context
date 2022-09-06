package io.github.logcontext.log4j2.spi;

import io.github.logcontext.LogContext;
import io.github.logcontext.LogContext.Builder;
import io.github.logcontext.LoggingContextServiceProvider;
import io.github.logcontext.log4j2.Log4j2LogContextBuilder;

/**
 * The Log4j2LoggingContextServiceProvider class provides a {@link LoggingContextServiceProvider}
 * implementation that provides a {@link LogContext.Builder}
 * implementation capable of configuring the logging context for the Log4j 2 logging framework.
 */
public class Log4j2LoggingContextServiceProvider implements LoggingContextServiceProvider {

  @Override
  public Builder logContextBuilder() {
    return new Log4j2LogContextBuilder();
  }
}
