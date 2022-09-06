package io.github.logcontext.spring.aop;

import io.github.logcontext.aop.LoggingContextAdvice;
import org.springframework.stereotype.Component;

/**
 * The SpringLoggingContextAdvice class extends {@link LoggingContextAdvice} to provide a component
 * for Spring AOP.
 *
 * @see LoggingContextAdvice
 */
@Component
public class SpringLoggingContextAdvice extends LoggingContextAdvice {}
