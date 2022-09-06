package io.github.logcontext.log4j12.spi;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import io.github.logcontext.LogContext.Builder;
import io.github.logcontext.log4j12.Log4j12LogContextBuilder;
import org.junit.jupiter.api.Test;

/**
 * The Log4j12LoggingContextServiceProviderTest class provides a set of JUnit test cases for the
 * {@link Log4j12LoggingContextServiceProvider} class.
 */
class Log4j12LoggingContextServiceProviderTest {

  /** The Log4j12 logging context service provider instance being tested. */
  private final Log4j12LoggingContextServiceProvider loggingContextServiceProvider =
      new Log4j12LoggingContextServiceProvider();

  /** Test method for {@link Log4j12LoggingContextServiceProvider#logContextBuilder()}. */
  @Test
  void testLogContextBuilder() {
    final Builder builder = loggingContextServiceProvider.logContextBuilder();

    assertThat(builder, notNullValue());
    assertThat(builder, instanceOf(Log4j12LogContextBuilder.class));
  }
}
