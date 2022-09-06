package io.github.logcontext;

public interface LoggingContextServiceProvider {

  /**
   * Creates a new instance of a LogContext.Builder specific to the provider implementation.
   *
   * @return a new instance of a LogContext.Builder specific to the provider implementation.
   */
  LogContext.Builder logContextBuilder();
}
