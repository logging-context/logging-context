package io.github.logcontext.log4j12;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.github.logcontext.LogContext;
import io.github.logcontext.LogContext.Builder;
import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The Log4j12LogContextBuilderTest class provides a set of JUnit test cases for the {@link
 * Log4j12LogContextBuilder} class.
 */
@ExtendWith(MockitoExtension.class)
class Log4j12LogContextBuilderTest {

  private static final String CONTEXT_1 = "CONTEXT1";

  private static final String CONTEXT_2 = "CONTEXT2";

  private static final String VALUE_1 = "value1";

  private static final String VALUE_2 = "2";

  /**
   * The SLF4J {@link LogContext.Builder} implementation being
   * tested.
   */
  private Log4j12LogContextBuilder logContextBuilder = new Log4j12LogContextBuilder();

  /**
   * Test method for {@link Log4j12LogContextBuilder#get()} to ensure that a {@link LogContext} is
   * generated and that the Log4J {@link MDC} and {@link NDC} are updated.
   *
   * @throws Throwable
   */
  @Test
  void testGet_updatesLoggingContext() throws Throwable {
    Builder builder =
        logContextBuilder
            .andNested(CONTEXT_1, CONTEXT_2)
            .andMapped(CONTEXT_1, VALUE_1)
            .andMapped(CONTEXT_2, VALUE_2);

    try (final MockedStatic<MDC> mockedMdc = mockStatic(MDC.class);
        final MockedStatic<NDC> mockedNdc = mockStatic(NDC.class);
        final LogContext context = builder.get()) {

      mockedNdc.when(NDC::pop).thenReturn(CONTEXT_2, CONTEXT_1);

      assertThat(context, notNullValue());

      mockedNdc.verify(() -> NDC.push(CONTEXT_1));
      mockedNdc.verify(() -> NDC.push(CONTEXT_2));
      mockedNdc.verifyNoMoreInteractions();

      mockedMdc.verify(() -> MDC.put(CONTEXT_1, VALUE_1));
      mockedMdc.verify(() -> MDC.put(CONTEXT_2, VALUE_2));
      mockedMdc.verifyNoMoreInteractions();
    }
  }

  /**
   * Test method for {@link Log4j12LogContextBuilder#get()} to ensure that the {@link LogContext}
   * returned clears the Log4j {@link MDC} and {@link NDC} contexts when closed.
   *
   * @throws Throwable
   */
  @Test
  void testGet_returnedLoggingContextClearsContextOnClose() throws Throwable {
    Builder builder =
        logContextBuilder
            .andNested(CONTEXT_1, CONTEXT_2)
            .andMapped(CONTEXT_1, VALUE_1)
            .andMapped(CONTEXT_2, VALUE_2);

    try (final MockedStatic<MDC> mockedMdc = mockStatic(MDC.class);
        final MockedStatic<NDC> mockedNdc = mockStatic(NDC.class)) {
      mockedNdc.when(NDC::pop).thenReturn(CONTEXT_2, CONTEXT_1);

      final LogContext context = builder.get();

      assertThat(context, notNullValue());

      context.close();

      mockedNdc.verify(NDC::pop, times(2));
      mockedMdc.verify(() -> MDC.remove(CONTEXT_1));
      mockedMdc.verify(() -> MDC.remove(CONTEXT_2));
    }
  }
}
