package io.github.logcontext.log4j2.spi;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

import io.github.logcontext.LogContext.Builder;
import io.github.logcontext.log4j2.Log4j2LogContextBuilder;
import org.junit.jupiter.api.Test;

/**
 * The Log4j2LoggingContextServiceProviderTest class provides a set of JUnit test cases for the
 * {@link Log4j2LoggingContextServiceProvider} class.
 */
class Log4j2LoggingContextServiceProviderTest {

  /** The Log4j2 logging context service provider instance being tested. */
  private final Log4j2LoggingContextServiceProvider loggingContextServiceProvider =
      new Log4j2LoggingContextServiceProvider();

  /** Test method for {@link Log4j2LoggingContextServiceProvider#logContextBuilder()}. */
  @Test
  void testLogContextBuilder() {
    final Builder builder = loggingContextServiceProvider.logContextBuilder();

    assertThat(builder, notNullValue());
    assertThat(builder, instanceOf(Log4j2LogContextBuilder.class));
  }
}
