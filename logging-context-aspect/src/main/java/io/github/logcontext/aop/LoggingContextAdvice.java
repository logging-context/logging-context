package io.github.logcontext.aop;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;

import io.github.logcontext.ContextExtractor;
import io.github.logcontext.ExtractedContext;
import io.github.logcontext.LogContext;
import io.github.logcontext.LogContext.Builder;
import io.github.logcontext.LoggingContext;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * The LoggingContextAdvice class provides an {@link Aspect} implementation that is invoked on
 * classes, methods, and parameters annotated with the {@link LoggingContext} annotation.
 *
 * <p>The advice will push nested context first for classes, then methods. If any parameters have
 * the {@link LoggingContext} annotation, the last value of the annotation will be put in the
 * context with the value of the parameter.
 *
 * <p>Any {@link LoggingContext} without an associated value will use the name of the annotated
 * element as the context.
 *
 * <p>Examples: <pre>
 * &amp;LoggingContext("NamedClassContext") // Nested NamedClassContext
 * public class NamedContextClass {
 *
 *   &amp;LoggingContext("NamedMethodContext") // Nested NamedMethodContext
 *   public void namedContextMethod(
 *     &amp;LoggingContext("NamedParamContext") String namedContextParam, // Mapped NamedParamContext
 *     &amp;LoggingContext String defaultContextParam, // Mapped defaultContextParam
 *   ) {}
 * }
 *
 * &amp;LoggingContext // Nested DefaultContextClass
 * public class DefaultContextClass {
 *
 *   &amp;LoggingContext // Nested defaultContextMethod
 *   public void defaultContextMethod(
 *     String noContextParam, // Not in mapped context
 *   ) {}
 *
 *   public void noContextMethod( // Not in nested context
 *     &amp;LoggingContext String defaultContextParam, // Mapped defaultContextParam
 *   ) {}
 * }
 *   </pre>
 */
@Aspect
public class LoggingContextAdvice {

  /**
   * An AspectJ Pointcut that identifies methods within classes annotated with @{@link
   * LoggingContext}.
   */
  @Pointcut("@within(io.github.logcontext.LoggingContext) && execution(* *(*))")
  public void classLoggingContext() {}

  /** An AspectJ Pointcut that identifies methods annotated with @{@link LoggingContext}. */
  @Pointcut("@annotation(io.github.logcontext.LoggingContext)")
  public void methodLoggingContext() {}

  /**
   * An AspectJ Pointcut that identifies methods containing arguments annotated with @{@link
   * LoggingContext}.
   */
  @Pointcut("execution(* *(.., @io.github.logcontext.LoggingContext (*), ..))")
  public void argumentNamedContext() {}

  /**
   * An AspectJ Pointcut that identifies methods containing arguments annotated with
   * {@link ExtractedContext}.
   */
  @Pointcut("execution(* *(.., @io.github.logcontext.ExtractedContext (*), ..))")
  public void argumentExtractedContext() {}

  /**
   * An AspectJ Pointcut that identifies methods containing arguments annotated with @{@link
   * LoggingContext} or {@link ExtractedContext}.
   */
  @Pointcut("argumentNamedContext() || argumentExtractedContext()")
  public void argumentLoggingContext() {}

  /**
   * Examines an advice {@link ProceedingJoinPoint}'s target class, method, and method parameters
   * for elements with the {@link LoggingContext} annotation. Any matches will have their associated
   * context value added to the current logging context. The logging context values will be removed
   * after the method's invocation.
   *
   * @param joinPoint the join point around a method that includes a logging context.
   * @return the return value from the invoked join point.
   * @throws Throwable if the invoked method threw an exceptions.
   */
  @Around("classLoggingContext() || methodLoggingContext() || argumentLoggingContext()")
  public Object includeLoggingContext(final ProceedingJoinPoint joinPoint) throws Throwable {
    if (joinPoint.getSignature() instanceof MethodSignature) {
      MethodSignature ms = (MethodSignature) joinPoint.getSignature();
      final Class<?> clazz = joinPoint.getTarget().getClass();
      final Method method = clazz.getMethod(ms.getName(), ms.getParameterTypes());

      final Builder contextBuilder = Builder.builder();

      try (LogContext context =
          addLoggingContext(
                  method.getParameters(),
                  joinPoint.getArgs(),
                  addLoggingContext(method, addLoggingContext(clazz, contextBuilder)))
              .get()) {

        return joinPoint.proceed();
      }
    } else {
      return joinPoint.proceed();
    }
  }

  /**
   * Adds the logging context associated with the provided class to the provided logging context
   * builder's nested context.
   *
   * @param clazz the annotated class that may have logging context annotations.
   * @param builder the {@link Builder} to which the logging contexts should be added.
   */
  private Builder addLoggingContext(final Class<?> clazz, final Builder builder) {
    if (clazz.isAnnotationPresent(LoggingContext.class)) {
      final LoggingContext loggingContext = clazz.getAnnotation(LoggingContext.class);
      final String[] contexts = getContextsOrDefault(loggingContext.value(), clazz.getSimpleName());
      return builder.andNested(contexts);
    }

    return builder;
  }

  /**
   * Adds the logging context associated with the provided method to the provided logging context
   * builder's nested context.
   *
   * @param method the method that may have logging context annotations.
   * @param builder the {@link Builder} to which the logging contexts should be added.
   */
  private Builder addLoggingContext(final Method method, final Builder builder) {
    if (method.isAnnotationPresent(LoggingContext.class)) {
      final LoggingContext loggingContext = method.getAnnotation(LoggingContext.class);
      final String[] contexts = getContextsOrDefault(loggingContext.value(), method.getName());
      return builder.andNested(contexts);
    }

    return builder;
  }

  /**
   * Adds the logging context associated with the provided parameters and their values to the
   * provided context builder's mapped context.
   *
   * @param parameters the annotated elements that may have logging context annotations.
   * @param values the values associated with the parameters.
   * @param builder the {@link Builder} to which the logging contexts should be added.
   */
  private Builder addLoggingContext(
      final Parameter[] parameters, final Object[] values, Builder builder) {
    for (int i = 0; i < parameters.length; i++) {
      if (parameters[i].isAnnotationPresent(LoggingContext.class)) {
        final LoggingContext loggingContext = parameters[i].getAnnotation(LoggingContext.class);
        final String[] loggingContextNames =
            getContextsOrDefault(loggingContext.value(), parameters[i].getName());
        builder =
            builder.andMapped(
                loggingContextNames[loggingContextNames.length - 1], String.valueOf(values[i]));
      } else if (parameters[i].isAnnotationPresent(ExtractedContext.class)) {
        final ExtractedContext extractedContext = parameters[i].getAnnotation(ExtractedContext.class);
        final ContextExtractor<Object> extractor = getExtractor(extractedContext);
        Map<String, String> extractedContexts = extractor.apply(values[i]);
        if (!isNull(extractedContext.prefix()) && !extractedContext.prefix().trim().isEmpty()) {
          extractedContexts = extractedContexts.entrySet()
              .stream()
              .map(withPrefixedContext(extractedContext.prefix()))
              .collect(toMap(Entry::getKey, Entry::getValue));
        }
        builder = builder.andMapped(extractedContexts);
      }
    }

    return builder;
  }

  private String[] getContextsOrDefault(final String[] contexts, final String defaultContext) {
    return (contexts != null && contexts.length > 0) ? contexts : new String[] {defaultContext};
  }

  private ContextExtractor<Object> getExtractor(final ExtractedContext extractedContext) {
    try {
      return extractedContext.extractor().newInstance();
    } catch (Exception e) {
      return ContextExtractor.NO_OP_EXTRACTOR;
    }
  }

  private Function<Map.Entry<String, String>, Map.Entry<String, String>> withPrefixedContext(final String prefix) {
    return (entry) -> new SimpleImmutableEntry<>(prefix + entry.getKey(), entry.getValue());
  }
}
