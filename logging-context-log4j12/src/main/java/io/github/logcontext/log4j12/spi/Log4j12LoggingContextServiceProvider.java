package io.github.logcontext.log4j12.spi;

import io.github.logcontext.LogContext;
import io.github.logcontext.LogContext.Builder;
import io.github.logcontext.LoggingContextServiceProvider;
import io.github.logcontext.log4j12.Log4j12LogContextBuilder;

/**
 * The Log4j12LoggingContextServiceProvider class provides a {@link LoggingContextServiceProvider}
 * implementation that provides a {@link LogContext.Builder}
 * implementation capable of configuring the logging context for the Log4J 1.2 logging
 * implementation.
 */
public class Log4j12LoggingContextServiceProvider implements LoggingContextServiceProvider {

  @Override
  public Builder logContextBuilder() {
    return new Log4j12LogContextBuilder();
  }
}
