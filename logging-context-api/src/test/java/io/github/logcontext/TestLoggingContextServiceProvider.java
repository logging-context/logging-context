package io.github.logcontext;

import io.github.logcontext.LogContext.Builder;

public class TestLoggingContextServiceProvider implements LoggingContextServiceProvider {

  @Override
  public Builder logContextBuilder() {
    return new TestLogContextBuilder();
  }
}
