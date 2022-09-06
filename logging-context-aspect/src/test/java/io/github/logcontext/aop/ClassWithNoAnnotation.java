package io.github.logcontext.aop;

import io.github.logcontext.LoggingContext;

/**
 * Example class which does not use the @{@link LoggingContext}
 * annotation at all.
 */
public class ClassWithNoAnnotation {
  public static final Object OBJECT = new Object();

  public void methodA0_P0_RV() {}

  public Object methodA0_P0_RO() {
    return OBJECT;
  }

  public void methodA0_P1X_RV(final String param1) {}

  public Object methodA0_P1X_RO(final String param1) {
    return OBJECT;
  }
}
