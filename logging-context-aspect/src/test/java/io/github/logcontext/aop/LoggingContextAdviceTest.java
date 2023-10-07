package io.github.logcontext.aop;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.github.logcontext.LogContext;
import io.github.logcontext.LogContext.Builder;
import io.github.logcontext.LoggingContext;
import io.github.logcontext.fixtures.PersonContext;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MemberSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * The LoggingContextAdviceTest class provides a set of JUnit test cases for the {@link
 * LoggingContextAdvice} class.
 */
class LoggingContextAdviceTest {

  /** The logging context advice instance being tested. */
  private final LoggingContextAdvice advice = new LoggingContextAdvice();

  public static final String PARAMETER_1_VALUE = "value1";

  public static final int PARAMETER_2_VALUE_INT = 6;

  public static final String PARAMETER_2_VALUE_STRING = "value2";

  /**
   * Test method for {@link LoggingContextAdvice#includeLoggingContext(ProceedingJoinPoint)} that
   * ensures that a class with a named logging context, method without a logging context, and
   * parameter with a named context builds the appropriate logging context and closes it afterwards.
   *
   * @throws Throwable
   */
  @Test
  void testIncludeLoggingContext_namedClassNamedMethodNamedParam() throws Throwable {
    withMockJoinPoint(ClassWithNamedAnnotation.class, "methodA1N_P1N_RV", null, "value1")
        .accept(
            (orderedCalls, mockBuilder) -> {
              orderedCalls
                  .verify(mockBuilder)
                  .andNested(ClassWithNamedAnnotation.LOGGING_CONTEXT_NAME);
              orderedCalls
                  .verify(mockBuilder)
                  .andNested(ClassWithNamedAnnotation.METHOD_LOGGING_CONTEXT_1);
              orderedCalls
                  .verify(mockBuilder)
                  .andMapped(ClassWithNamedAnnotation.PARAMETER_1_NAMED_LOGGING_CONTEXT, "value1");
              orderedCalls.verify(mockBuilder).get();
            });
  }

  /**
   * Test method for {@link LoggingContextAdvice#includeLoggingContext(ProceedingJoinPoint)} that
   * ensures that a class with a named logging context, method without a logging context, and
   * parameter with a named context builds the appropriate logging context and closes it afterwards.
   *
   * @throws Throwable
   */
  @Test
  void testIncludeLoggingContext_namedClassNoMethodNamedParam() throws Throwable {
    withMockJoinPoint(ClassWithNamedAnnotation.class, "methodA0_P1N_RV", null, "value1")
        .accept(
            (orderedCalls, mockBuilder) -> {
              orderedCalls
                  .verify(mockBuilder)
                  .andNested(ClassWithNamedAnnotation.LOGGING_CONTEXT_NAME);
              orderedCalls
                  .verify(mockBuilder)
                  .andMapped(ClassWithNamedAnnotation.PARAMETER_1_NAMED_LOGGING_CONTEXT, "value1");
              orderedCalls.verify(mockBuilder).get();
            });
  }

  /**
   * Test method for {@link LoggingContextAdvice#includeLoggingContext(ProceedingJoinPoint)} that
   * ensures that a class with a named logging context, method without a logging context, and
   * parameter with an extracted context builds the appropriate logging context and closes it afterwards.
   *
   * @throws Throwable
   */
  @Test
  void testIncludeLoggingContext_namedClassNoMethodExtractedParam() throws Throwable {
    withMockJoinPoint(ClassWithNamedAnnotation.class, "methodA0_P1E_RV", null, PersonContext.PERSON)
        .accept(
            (orderedCalls, mockBuilder) -> {
              orderedCalls
                  .verify(mockBuilder)
                  .andNested(ClassWithNamedAnnotation.LOGGING_CONTEXT_NAME);
              orderedCalls
                  .verify(mockBuilder)
                  .andMapped(PersonContext.PERSON_CONTEXT);
              orderedCalls.verify(mockBuilder).get();
            });
  }

  /**
   * Test method for {@link LoggingContextAdvice#includeLoggingContext(ProceedingJoinPoint)} that
   * ensures that a class with a named logging context, method without a logging context, and
   * parameter with an extracted context with a prefix mixed with a standard named context builds
   * the appropriate logging context and closes it afterwards.
   *
   * @throws Throwable
   */
  @Test
  void testIncludeLoggingContext_namedClass_noMethod_prefixedExtractedParamAndNamed() throws Throwable {
    withMockJoinPoint(ClassWithNamedAnnotation.class, "methodA0_P1EP_P2N_RV", null, PersonContext.PERSON, PARAMETER_2_VALUE_INT)
        .accept(
            (orderedCalls, mockBuilder) -> {
              orderedCalls
                  .verify(mockBuilder)
                  .andNested(ClassWithNamedAnnotation.LOGGING_CONTEXT_NAME);
              orderedCalls
                  .verify(mockBuilder)
                  .andMapped(PersonContext.PERSON_CONTEXT_PREFIXED);
              orderedCalls
                  .verify(mockBuilder)
                  .andMapped(
                      ClassWithNamedAnnotation.PARAMETER_2_NAMED_LOGGING_CONTEXT,
                      String.valueOf(PARAMETER_2_VALUE_INT));
              orderedCalls.verify(mockBuilder).get();
            });
  }

  /**
   * Test method for {@link LoggingContextAdvice#includeLoggingContext(ProceedingJoinPoint)} that
   * ensures that a class with a named logging context, an overloaded method with a default logging
   * context a logging context, and parameters with mixed logging contexts builds the appropriate
   * logging context and closes it afterwards.
   *
   * <p>This test case covers both versions of the overloaded method.
   *
   * @throws Throwable
   */
  @Test
  void testIncludeLoggingContext_namedClassOverloadedDefaultMethodMixedParams() throws Throwable {
    withMockJoinPoint(
            ClassWithNamedAnnotation.class,
            "methodA1D_P1_P2_RV_overloaded",
            null,
            PARAMETER_1_VALUE,
            PARAMETER_2_VALUE_INT)
        .accept(
            (orderedCalls, mockBuilder) -> {
              orderedCalls
                  .verify(mockBuilder)
                  .andNested(ClassWithNamedAnnotation.LOGGING_CONTEXT_NAME);
              orderedCalls.verify(mockBuilder).andNested("methodA1D_P1_P2_RV_overloaded");
              orderedCalls
                  .verify(mockBuilder)
                  .andMapped(
                      ClassWithNamedAnnotation.PARAMETER_2_NAMED_LOGGING_CONTEXT,
                      String.valueOf(PARAMETER_2_VALUE_INT));
              orderedCalls.verify(mockBuilder).get();
            });
    withMockJoinPoint(
            ClassWithNamedAnnotation.class,
            "methodA1D_P1_P2_RV_overloaded",
            null,
            PARAMETER_1_VALUE,
            PARAMETER_2_VALUE_STRING)
        .accept(
            (orderedCalls, mockBuilder) -> {
              orderedCalls
                  .verify(mockBuilder)
                  .andNested(ClassWithNamedAnnotation.LOGGING_CONTEXT_NAME);
              orderedCalls.verify(mockBuilder).andNested("methodA1D_P1_P2_RV_overloaded");
              orderedCalls
                  .verify(mockBuilder)
                  .andMapped(
                      ClassWithNamedAnnotation.PARAMETER_1_NAMED_LOGGING_CONTEXT,
                      PARAMETER_1_VALUE);
              orderedCalls
                  .verify(mockBuilder)
                  .andMapped(
                      ClassWithNamedAnnotation.PARAMETER_2_NAMED_LOGGING_CONTEXT,
                      PARAMETER_2_VALUE_STRING);
              orderedCalls.verify(mockBuilder).get();
            });
  }

  /**
   * Test method for {@link LoggingContextAdvice#includeLoggingContext(ProceedingJoinPoint)} that
   * ensures that a class with a named logging context, method without a logging context, and
   * parameter with a named context builds the appropriate logging context and closes it afterwards.
   *
   * @throws Throwable
   */
  @Test
  void testIncludeLoggingContext_namedClassMultipleNamedMethodThrowsException() throws Throwable {
    withMockJoinPoint(
            ClassWithNamedAnnotation.class,
            "methodA2N_P1X_EX",
            ClassWithNamedAnnotation.EXCEPTION,
            "value1")
        .accept(
            (orderedCalls, mockBuilder) -> {
              orderedCalls
                  .verify(mockBuilder)
                  .andNested(ClassWithNamedAnnotation.LOGGING_CONTEXT_NAME);
              orderedCalls
                  .verify(mockBuilder)
                  .andNested(
                      ClassWithNamedAnnotation.METHOD_LOGGING_CONTEXT_1,
                      ClassWithNamedAnnotation.METHOD_LOGGING_CONTEXT_2);
              orderedCalls.verify(mockBuilder).get();
            });
  }



  /**
   * Test method for {@link LoggingContextAdvice#includeLoggingContext(ProceedingJoinPoint)} that
   * ensures that a class without a logging context, method without a logging context, and parameter
   * without a logging context does not affect the logging context.
   *
   * @throws Throwable
   */
  @Test
  void testIncludeLoggingContext_noLoggingContextAnnotations() throws Throwable {
    withMockJoinPoint(ClassWithNoAnnotation.class, "methodA0_P1X_RV", null, "value1")
        .accept(
            (orderedCalls, mockBuilder) -> {
              orderedCalls.verify(mockBuilder).get();
            });
  }

  /**
   * Test method for {@link LoggingContextAdvice#includeLoggingContext(ProceedingJoinPoint)} that
   * ensures that a join point with a non-method signature does not attempt to change the logging
   * context.
   *
   * @throws Throwable
   */
  @Test
  void testIncludeLoggingContext_nonMethodJoinPoint() throws Throwable {
    final ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    final Signature nonMethodSignature = mock(MemberSignature.class);

    when(joinPoint.getSignature()).thenReturn(nonMethodSignature);
    when(joinPoint.proceed()).thenReturn(ClassWithNamedAnnotation.OBJECT);

    final Object response = advice.includeLoggingContext(joinPoint);

    assertThat(response, equalTo(ClassWithNamedAnnotation.OBJECT));

    verify(joinPoint).getSignature();
    verify(joinPoint).proceed();
    verifyNoMoreInteractions(joinPoint);
  }

  // ---- BEGIN UTILITY CODE

  /**
   * Creates a test context for verifying that a method call (via an AspectJ {@link
   * ProceedingJoinPoint}) properly updates the logging context and clears it afterwards. The
   * response is a {@link Consumer} that accepts a {@link BiConsumer} for validating logging context
   * being built. The {@link BiConsumer} is intended to take an {@link InOrder} verifier and the
   * mock logging context builder. Since the order of the calls to the builder is important, the
   * {@link InOrder} verifier should be used to verify the order of the calls to the builder.
   *
   * @param clazz the {@link Class} that may or may not have @{@link LoggingContext} annotations.
   * @param methodName the name of a method in the class that may have @{@link LoggingContext}
   *     annotations.
   * @param response the sample response value that should be returned from the method.
   * @param arguments the arguments that should be passed to the method.
   * @return a {@link Consumer} that accepts a {@link BiConsumer} used to verify that the logging
   *     context was built in the correct order.
   * @throws Throwable
   */
  private Consumer<BiConsumer<InOrder, Builder>> withMockJoinPoint(
      Class clazz, String methodName, Object response, Object... arguments) throws Throwable {
    final Class[] argumentTypes =
        Stream.of(arguments)
            .map(Object::getClass)
            .collect(Collectors.toList())
            .toArray(new Class[0]);
    final Method method = clazz.getMethod(methodName, argumentTypes);

    final ProceedingJoinPoint joinPoint =
        mockJoinPoint(method, clazz.getConstructor().newInstance(), response, arguments);
    return (BiConsumer<InOrder, Builder> builderVerification) ->
        withMockBuilderAndContext(
            (mockBuilder, mockContext) -> {
              try {
                Object result = advice.includeLoggingContext(joinPoint);

                assertThat(result, equalTo(response));
              } catch (Throwable t) {
                assertThat(t, equalTo(response));
              }

              try {
                final InOrder inOrder = Mockito.inOrder(mockBuilder, joinPoint, mockContext);

                builderVerification.accept(inOrder, mockBuilder);

                inOrder.verify(joinPoint).proceed();
                inOrder.verify(mockContext).close();
              } catch (Throwable t) {
                fail(t);
              }
            });
  }

  private ProceedingJoinPoint mockJoinPoint(
      final Method method, final Object target, final Object response, final Object... arguments)
      throws Throwable {
    final ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    final MethodSignature signature = mock(MethodSignature.class);

    when(signature.getName()).thenReturn(method.getName());
    when(signature.getParameterTypes()).thenReturn(method.getParameterTypes());
    when(signature.getMethod()).thenReturn(method);

    when(joinPoint.getTarget()).thenReturn(target);
    when(joinPoint.getSignature()).thenReturn(signature);
    when(joinPoint.getArgs()).thenReturn(arguments);

    if (response instanceof Throwable) {
      when(joinPoint.proceed()).thenThrow((Throwable) response);
    } else {
      when(joinPoint.proceed()).thenReturn(response);
    }

    return joinPoint;
  }

  private void withMockBuilderAndContext(final BiConsumer<Builder, LogContext> testContext) {
    final LogContext mockContext = mock(LogContext.class);
    final Builder mockBuilder = mock(Builder.class);
    when(mockBuilder.andMapped(anyString(), anyString())).thenReturn(mockBuilder);
    when(mockBuilder.andMapped(anyMap())).thenReturn(mockBuilder);
    when(mockBuilder.andNested(any())).thenReturn(mockBuilder);
    when(mockBuilder.get()).thenReturn(mockContext);

    try (MockedStatic<Builder> staticBuilder = Mockito.mockStatic(Builder.class)) {
      staticBuilder.when(Builder::builder).thenReturn(mockBuilder);

      testContext.accept(mockBuilder, mockContext);
    }
  }
}
