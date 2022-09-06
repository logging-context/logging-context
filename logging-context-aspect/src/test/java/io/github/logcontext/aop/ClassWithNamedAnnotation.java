package io.github.logcontext.aop;

import static io.github.logcontext.aop.ClassWithNamedAnnotation.LOGGING_CONTEXT_NAME;

import io.github.logcontext.LoggingContext;

@LoggingContext(LOGGING_CONTEXT_NAME)
public class ClassWithNamedAnnotation {

  public static final String LOGGING_CONTEXT_NAME = "C1N";

  public static final String METHOD_LOGGING_CONTEXT_1 = "C1N_METHOD_CONTEXT_1";

  public static final String METHOD_LOGGING_CONTEXT_2 = "C1N_METHOD_CONTEXT_2";

  public static final String PARAMETER_1_NAMED_LOGGING_CONTEXT = "C1N_PARAMETER_1";

  public static final String PARAMETER_1_DEFAULT_LOGGING_CONTEXT = "param1";

  public static final String PARAMETER_2_NAMED_LOGGING_CONTEXT = "C1N_PARAMETER_2";

  public static final String PARAMETER_2_DEFAULT_LOGGING_CONTEXT = "param2";

  public static final Object OBJECT = new Object();

  public static final RuntimeException EXCEPTION = new RuntimeException("C1N_FAIL");

  public void methodA0_P0_RV() {}

  public Object methodA0_P0_RO() {
    return OBJECT;
  }

  public void methodA0_P1X_RV(final String param1) {}

  public Object methodA0_P1X_RO(final String param1) {
    return OBJECT;
  }

  public void methodA0_P1N_RV(
      @LoggingContext(PARAMETER_1_NAMED_LOGGING_CONTEXT) final String param1) {}

  public Object methodA0_P1N_RO(
      @LoggingContext(PARAMETER_1_NAMED_LOGGING_CONTEXT) final String param1) {
    return OBJECT;
  }

  public void methodA0_P1N_P2N_RV(
      @LoggingContext(PARAMETER_1_NAMED_LOGGING_CONTEXT) final String param1,
      @LoggingContext(PARAMETER_2_NAMED_LOGGING_CONTEXT) final int param2) {}

  public Object methodA0_P1N_P2N_RO(
      @LoggingContext(PARAMETER_1_NAMED_LOGGING_CONTEXT) final String param1,
      @LoggingContext(PARAMETER_2_NAMED_LOGGING_CONTEXT) final int param2) {
    return OBJECT;
  }

  public void methodA0_P1D_RV(@LoggingContext final String param1) {}

  public Object methodA0_P1D_P2D_RO(
      @LoggingContext final String param1, @LoggingContext final int param2) {
    return OBJECT;
  }

  public Object methodA0_P1D_P2D_EX(
      @LoggingContext final String param1, @LoggingContext final int param2) {
    throw EXCEPTION;
  }

  public void methodA0_P1D_P2N_RV(
      @LoggingContext final String param1,
      @LoggingContext(PARAMETER_2_NAMED_LOGGING_CONTEXT) final int param2) {}

  @LoggingContext
  public void methodA1D_P1X_RV(final String param1) {}

  @LoggingContext
  public void methodA1D_P1N_RV(
      @LoggingContext(PARAMETER_1_NAMED_LOGGING_CONTEXT) final String param1) {}

  @LoggingContext
  public Object methodA1D_P1N_RO(
      @LoggingContext(PARAMETER_1_NAMED_LOGGING_CONTEXT) final String param1) {
    return OBJECT;
  }

  @LoggingContext
  public void method1AD_P1N_P2N_RV(
      @LoggingContext(PARAMETER_1_NAMED_LOGGING_CONTEXT) final String param1,
      @LoggingContext(PARAMETER_2_NAMED_LOGGING_CONTEXT) final int param2) {}

  @LoggingContext
  public void methodA1D_P1_P2_RV_overloaded(
      final String param1,
      @LoggingContext(PARAMETER_2_NAMED_LOGGING_CONTEXT) final Integer param2) {}

  @LoggingContext
  public void methodA1D_P1_P2_RV_overloaded(
      @LoggingContext(PARAMETER_1_NAMED_LOGGING_CONTEXT) final String param1,
      @LoggingContext(PARAMETER_2_NAMED_LOGGING_CONTEXT) final String param2) {}

  @LoggingContext
  public void methodA1D_P1D_RV(@LoggingContext final String param1) {}

  @LoggingContext
  public Object methodA1D_P1D_P2D_RO(
      @LoggingContext final String param1, @LoggingContext final int param2) {
    return OBJECT;
  }

  @LoggingContext
  public void methodA1D_P1D_P2N_RV(
      @LoggingContext final String param1,
      @LoggingContext(PARAMETER_2_NAMED_LOGGING_CONTEXT) final int param2) {}

  @LoggingContext(METHOD_LOGGING_CONTEXT_1)
  public void methodA1N_P1X_RV(final String param1) {}

  @LoggingContext(METHOD_LOGGING_CONTEXT_1)
  public void methodA1N_P1N_RV(
      @LoggingContext(PARAMETER_1_NAMED_LOGGING_CONTEXT) final String param1) {}

  @LoggingContext(METHOD_LOGGING_CONTEXT_1)
  public void methodA1N_P1N_P2N_RV(
      @LoggingContext(PARAMETER_1_NAMED_LOGGING_CONTEXT) final String param1,
      @LoggingContext(PARAMETER_2_NAMED_LOGGING_CONTEXT) final int param2) {}

  @LoggingContext(METHOD_LOGGING_CONTEXT_1)
  public void methodA1N_P1D_RV(@LoggingContext final String param1) {}

  @LoggingContext(METHOD_LOGGING_CONTEXT_1)
  public void methodA1N_P1D_P2D_RV(
      @LoggingContext final String param1, @LoggingContext final int param2) {}

  @LoggingContext(METHOD_LOGGING_CONTEXT_1)
  public void methodA1N_P1D_P2N_RV(
      @LoggingContext final String param1,
      @LoggingContext(PARAMETER_2_NAMED_LOGGING_CONTEXT) final int param2) {}

  @LoggingContext({METHOD_LOGGING_CONTEXT_1, METHOD_LOGGING_CONTEXT_2})
  public void methodA2N_P1X_RV(final String param1) {}

  @LoggingContext({METHOD_LOGGING_CONTEXT_1, METHOD_LOGGING_CONTEXT_2})
  public void methodA2N_P1X_EX(final String param1) {
    throw EXCEPTION;
  }

  @LoggingContext({METHOD_LOGGING_CONTEXT_1, METHOD_LOGGING_CONTEXT_2})
  public void methodA2N_P1N_RV(@LoggingContext("person") final String param1) {}

  @LoggingContext({METHOD_LOGGING_CONTEXT_1, METHOD_LOGGING_CONTEXT_2})
  public void methodA2N_P1N_P2N_RV(
      @LoggingContext(PARAMETER_1_NAMED_LOGGING_CONTEXT) final String param1,
      @LoggingContext(PARAMETER_2_NAMED_LOGGING_CONTEXT) final int param2) {}

  @LoggingContext({METHOD_LOGGING_CONTEXT_1, METHOD_LOGGING_CONTEXT_2})
  public void methodA2N_P1D_RV(@LoggingContext final String param1) {}

  @LoggingContext({METHOD_LOGGING_CONTEXT_1, METHOD_LOGGING_CONTEXT_2})
  public void methodA2N_P1D_P2D_RV(
      @LoggingContext final String param1, @LoggingContext final int param2) {}

  @LoggingContext({METHOD_LOGGING_CONTEXT_1, METHOD_LOGGING_CONTEXT_2})
  public void methodA2N_P1D_P2N_RV(
      @LoggingContext final String param1,
      @LoggingContext(PARAMETER_2_NAMED_LOGGING_CONTEXT) final int param2) {}
}
