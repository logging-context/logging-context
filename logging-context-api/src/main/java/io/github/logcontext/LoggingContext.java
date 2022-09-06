package io.github.logcontext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The LoggingContext annotation identifies a section of code as having a specific contextual value
 * associated with logging statements. The annotation can be applied to classes, methods, and method
 * parameters. When applied to a class or method, it associates the scope of the class or method
 * with a Nested Diagnostic Context (NDC). When applied to method parameters, the parameter and its
 * value are associated with the Mapped Diagnostic Context (MDC).
 *
 * <p>If no value is supplied to the annotation, it is assumed that the intended context is the name
 * of the class, method, or parameter. Nested contexts allow for multiple contexts to be added at
 * once by providing an array of context values.
 *
 * <pre>
 *   &#64;LoggingContext("ORDERS")
 *   public class OrdersService {
 *     public static final Logger LOG = LogFactory.getLogger(OrdersService.class);
 *
 *     &#64;LoggingContext("REFUND")
 *     public void issueRefund(@LoggingContext("order_number") String orderNumber) {
 *       LOG.atInfo().log("With NDC [ORDERS, REFUND] and MDC order_number={}", orderNumber);
 *     }
 *
 *     &#64;LoggingContext()
 *     public void cancel(@LoggingContext() String orderNumber) {
 *       LOG.atInfo().log("With NDC [ORDERS, cancel] and MDC orderNumber={}", orderNumber);
 *     }
 *   }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LoggingContext {
  String[] value() default {};
}
