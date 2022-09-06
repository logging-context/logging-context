package io.github.logcontext;

import static io.github.logcontext.LogContext.Builder.NO_OP_BUILDER;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.logcontext.LogContext.Builder;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * The LogContextBuilderTest class provides a set of JUnit test cases for the {@link
 * LogContext.Builder} default interface methods. The interface identifies a concrete implementation
 * using the Java service provider framework. These tests are closely tied with the {@link
 * TestLoggingContextServiceProvider} implementation which is intended to return a {@link
 * TestLogContextBuilder} instance for aid in testing.
 */
public class LogContextBuilderTest {

  /** Test method for {@link LogContext.Builder#nestedContext(String...)}. */
  @Test
  void testNestedContextSingleContext() {
    final Builder logContextBuilder = Builder.nestedContext(TestValues.NESTED_CONTEXT_1);

    assertTestLogContextBuilder(logContextBuilder);

    final TestableLogContext testableContext = assertTestableLogContext(logContextBuilder);
    assertLogContextContainsNested(testableContext, TestValues.NESTED_CONTEXT_1);
  }

  /** Test method for {@link LogContext.Builder#nestedContext(String...)}. */
  @Test
  void testNestedContextMultipleContexts() {
    final Builder logContextBuilder =
        Builder.nestedContext(TestValues.NESTED_CONTEXT_1, TestValues.NESTED_CONTEXT_2);

    assertTestLogContextBuilder(logContextBuilder);

    final TestableLogContext testableContext = assertTestableLogContext(logContextBuilder);
    assertLogContextContainsNested(
        testableContext, TestValues.NESTED_CONTEXT_1, TestValues.NESTED_CONTEXT_2);
  }

  /**
   * Test method for {@link LogContext.Builder#nestedContext(String...)} that ensures a {@link
   * NullPointerException} is thrown for a <code>null</code> nested context list.
   */
  @Test
  void testNestedContextNullNestedContextList() {
    assertThrows(NullPointerException.class, () -> Builder.nestedContext((String[]) null));
  }

  /**
   * Test method for {@link LogContext.Builder#nestedContext(String...)} that ensures an {@link
   * IllegalArgumentException} is thrown if no nested contexts are provided.
   */
  @Test
  void testNestedContextNoContexts() {
    assertThrows(IllegalArgumentException.class, () -> Builder.nestedContext(new String[] {}));
  }

  /**
   * Test method for {@link LogContext.Builder#nestedContext(String...)} that ensures an {@link
   * IllegalArgumentException} is thrown if the provided nested contexts include a <code>null</code>
   * value.
   */
  @Test
  void testNestedContextIncludingNullContext() {
    assertThrows(IllegalArgumentException.class, () -> Builder.nestedContext((String) null));
    assertThrows(
        IllegalArgumentException.class,
        () -> Builder.nestedContext(null, TestValues.NESTED_CONTEXT_2));
    assertThrows(
        IllegalArgumentException.class,
        () -> Builder.nestedContext(TestValues.NESTED_CONTEXT_1, null));
  }

  /** Test method for {@link LogContext.Builder#mappedContext(String, String)}. */
  @Test
  void testMappedContext() {
    final Builder logContextBuilder =
        Builder.mappedContext(TestValues.MAPPED_CONTEXT_1, TestValues.MAPPED_CONTEXT_VALUE_1);

    assertTestLogContextBuilder(logContextBuilder);

    final TestableLogContext testableContext = assertTestableLogContext(logContextBuilder);
    assertLogContextContainsMappings(testableContext,
        singletonMap(TestValues.MAPPED_CONTEXT_1, TestValues.MAPPED_CONTEXT_VALUE_1)
            .entrySet().iterator().next());
  }

  /**
   * Test for the {@link Builder#NO_OP_BUILDER} no-op implementation.
   *
   * @throws Exception
   */
  @Test
  void testNoOpBuilder() throws Exception {
    assertThat(NO_OP_BUILDER.andMapped(null, null), equalTo(NO_OP_BUILDER));
    assertThat(NO_OP_BUILDER.andNested(null, null), equalTo(NO_OP_BUILDER));
    final LogContext noOpLogContext = NO_OP_BUILDER.get();

    assertThat(noOpLogContext, notNullValue());
    assertDoesNotThrow(() -> noOpLogContext.close());
  }

  private void assertTestLogContextBuilder(Builder logContextBuilder) {
    assertThat(logContextBuilder, notNullValue());
    assertThat(logContextBuilder, instanceOf(TestLogContextBuilder.class));
  }

  private TestableLogContext assertTestableLogContext(Builder logContextBuilder) {
    final LogContext logContext = logContextBuilder.get();

    assertThat(logContext, notNullValue());
    assertThat(logContext, instanceOf(TestableLogContext.class));

    return (TestableLogContext) logContext;
  }

  private void assertLogContextContainsNested(
      final TestableLogContext testableContext, final String... nestedContexts) {
    asList(nestedContexts)
        .forEach(
            nestedContext ->
                assertThat(testableContext.hasNestedContext(nestedContext), equalTo(true)));
  }

  private void assertLogContextContainsMappings(
      final TestableLogContext testableContext, final Map.Entry<String, String> contexts) {
    asList(contexts)
        .forEach(
            mappedContext -> {
              assertThat(testableContext.hasMappedContext(mappedContext.getKey()), equalTo(true));
              assertThat(
                  testableContext.hasMappedContextValue(
                      mappedContext.getKey(), mappedContext.getValue()),
                  equalTo(true));
            });
  }

  /**
   * The TestValues class provides a collections of constants and utility methods related to the
   * logging context classes.
   */
  public static final class TestValues {
    public static final String NESTED_CONTEXT_1 = "NDC1";

    public static final String NESTED_CONTEXT_2 = "NDC2";

    public static final String NESTED_CONTEXT_3 = "NDC3";

    public static final String MAPPED_CONTEXT_1 = "MDC1";

    public static final String MAPPED_CONTEXT_2 = "MDC2";

    public static final String MAPPED_CONTEXT_VALUE_1 = "Value1";

    public static final String MAPPED_CONTEXT_VALUE_2 = "Value2";

    public static final String MAPPED_CONTEXT_VALUE_3 = "Value3";
  }
}
