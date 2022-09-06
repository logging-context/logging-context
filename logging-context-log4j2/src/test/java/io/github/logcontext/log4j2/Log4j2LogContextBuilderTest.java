package io.github.logcontext.log4j2;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.logcontext.LogContext;
import io.github.logcontext.LogContext.Builder;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.logging.log4j.CloseableThreadContext;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/**
 * The Log4j2LogContextBuilderTest class provides a set of JUnit test cases for the {@link
 * Log4j2LogContextBuilder} class.
 */
class Log4j2LogContextBuilderTest {

  private static final String CONTEXT_1 = "CONTEXT1";

  private static final String CONTEXT_2 = "CONTEXT2";

  private static final String VALUE_1 = "value1";

  private static final String VALUE_2 = "2";

  /**
   * The Log4j2 {@link LogContext.Builder} implementation being
   * tested.
   */
  private Log4j2LogContextBuilder logContextBuilder = new Log4j2LogContextBuilder();

  /**
   * Test method for {@link Log4j2LogContextBuilder#get()} to ensure that a {@link LogContext} is
   * generated and that the Log4j2 {@link org.apache.logging.log4j.ThreadContext} is updated.
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

    try (final MockedStatic<CloseableThreadContext> mockThreadContext =
        mockStatic(CloseableThreadContext.class)) {
      final CloseableThreadContext.Instance mockContext =
          mock(CloseableThreadContext.Instance.class);
      mockThreadContext
          .when(() -> CloseableThreadContext.pushAll(anyList()))
          .thenReturn(mockContext);
      when(mockContext.putAll(anyMap())).thenReturn(mockContext);

      try (final LogContext context = builder.get()) {
        assertThat(context, notNullValue());

        final Map<String, String> expectedContexts = new LinkedHashMap<>();
        expectedContexts.put(CONTEXT_1, VALUE_1);
        expectedContexts.put(CONTEXT_2, VALUE_2);

        mockThreadContext.verify(
            () -> CloseableThreadContext.pushAll(asList(CONTEXT_1, CONTEXT_2)));
        verify(mockContext).putAll(expectedContexts);
      }
    }
  }

  /**
   * Test method for {@link Log4j2LogContextBuilder#get()} to ensure that the {@link LogContext}
   * returned clears the Log4j2 {@link org.apache.logging.log4j.ThreadContext} contexts when closed.
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

    try (final MockedStatic<CloseableThreadContext> mockThreadContext =
        mockStatic(CloseableThreadContext.class)) {
      final CloseableThreadContext.Instance mockContext =
          mock(CloseableThreadContext.Instance.class);
      mockThreadContext
          .when(() -> CloseableThreadContext.pushAll(anyList()))
          .thenReturn(mockContext);
      when(mockContext.putAll(anyMap())).thenReturn(mockContext);

      final LogContext context = builder.get();
      assertThat(context, notNullValue());

      context.close();

      verify(mockContext).close();
    }
  }
}
