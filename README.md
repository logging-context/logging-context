# Logging Context

Enhance your log messages with contextual data.

# Why Use Logging Context

The primary purpose of this library is to avoid two common
logging situations.

1. Vague log messages that don't add value
```
INFO Locking user account
```

2. Unnecessary method parameters only used for logging
```java
public void lockUserAccount(String userId, String ip,
    String reason, int retryAttempt) {
  logger.info(String.format("Locking user account %s at IP %s because %s after %s retry attempts",
    userId, ip, reason, retryAttempt));
  // Actually locking the user account
}
```

The first problem is that generic log message provides very little value. Ultimately, logging statements serve only a few purposes: debugging or after-the-fact problem analysis, auditing, or performance analysis. To meet those purposes logging statements must be complete enough to tell a story useful for that purpose. Without useful, actionable details, the logging statements do little more than fill up space on disk.

The second problem arises from attempts to overcome the first problem. To make sure those details are available when a statement is logged, sometimes developers will provide too many details to ensure that they can be captured. Most likely the method for locking a user account doesn't need details about ther user's IP address or how many retry attempts failed, but that information may be useful.

So providing a wrapping "context" to our logging statements can prevent both issues.

# Types of Logging Contexts

## Mapped Diagnostic Context (MDC)

The mapped diagnostic context, or MDC for short, focuses on mapping key/value pairs in your log messages. In most Java logging frameworks, these are persisted at the thread level. The MDC represents the state (or the "what") when the logging statement is executed.

An example of our earlier statement that utilizes MDC might look like:
```
INFO {user=user1234, ip=192.168.1.140, retry=3, reason="failed password attempt"} Locking user account
```

That message provides the same information as in the second example, but represented with key/value pairs. This can be helpful because many log analysis tools can pull these out into searchable fields.

## Nested Diagnostic Context (NDC)

In contrast, the nested diagnostic context (NDC) represents the flow through the system. The nested context is more generally represented as a stack, with each value representing a different layer in the application. If the MDC represents the "what" of a logging statement, the NDC represents the "how" that logging statement was reached. This can be extremely useful in cases where common code is logging and the context from which it is being called is important.

The following examples illustrate utilizing the nested diagnostic context:
```
INFO [USER_LOGIN, USER_LOOKUP] {user=user1234} Unable to locate user account
INFO [ORDERS, RETURNS, USER_LOOKUP] {user=user1234} Unable to locate user account
```

While both messages and mapped diagnostic contexts may indicate the same user cannot be found in the system, there may be significant implications in not being able to find a user during a login attempt and not being able to find a user in the middle of processing their return.

# Logging Context Scope

One important consideration for the context of a log statement is that context information doesn't leak out of the scope of the logged operation. When adding something to the context, care must be taken to also remove it from the context as well.

```java
public void lockUserAccount(String userId) throws UserNotFoundException {
  NDC.push("LOCK_USER");
  MDC.put("user", userId);
  logger.info("Locking user account");
  // Actually locking the user account
}
```

The above code will include the desired values in the context, but it also may include them in other operations where they are not warranted. After the values are no longer needed, they should be removed. A common approach is to just add the reverse statements at the end of a method, but there are potential pitfalls with this approach if not done correctly.

__BAD EXAMPLE: DON'T DO THIS__
```java
public void lockUserAccount(String userId) throws UserNotFoundException {
  NDC.push("LOCK_USER");
  MDC.put("user", userId);
  logger.info("Locking user account");
  // Actually locking the user account
  MDC.remove("user");
  NDC.pop();
}
```

Under good circumstances, the above code will work and clear the added context information. The drawback is that if an exception is thrown during the locking process, those statements may not be executed. This requires that all logging context statements be wrapped in a `try..finally` block.

__GOOD BUT TEDIOUS__
```java
public void lockUserAccount(String userId) throws UserNotFoundException {
  try {
    NDC.push("LOCK_USER");
    MDC.put("user", userId);
    logger.info("Locking user account");
    // Actually locking the user account
  } finally {
    MDC.remove("user");
    NDC.pop();
  }
}
```

The Logging Context API aims to address these issues by making it easier to set up and automatically clean up the logging context.

# Using Logging Contexts

## Fluent API

Luckily, most modern Java logging frameworks provide at least some support for logging context information. At a very minumum, they allow you to manually add and remove entries from a mapped or nested logging context, but that each have their own approaches. Much like SLF4J is a facade around a logger, the Logging Context API provides a facade around the logger's proprietary handling of logging contexts.

The `LogContext` class provides a representation of a logging systems current context values. The class implements `Autocloseable` as a means of scoping a logging context only for specific statements within an initialized `try..catch` block. Instances of the class can be obtained using a `LogContext.Builder` or its static utility methods `nestedContext(String...)` or `mappedContext(String, String)`. Additional contexts can be chained using the `andMapped()` and `andNested()` utilities.

The `LogContext.Builder` is itself a `Supplier<LogContext>`, and the logging context __will not__ be updated until specifically requested via `get()`.

```java
try (LogContext ctx = LogContext.Builder.nestedContext("USER_LOOKUP").andMapped("user", userId).get()) {
  logger.info("Unable to locate user account");
}
logger.info("Outside of context scope");
```

This approach can be utilized using just the Logging Context API (`logging-context-api`) and an appropriate logging framework implementation (`logging-context-log4j2` or `logging-context-slf4j`).

__MAVEN DEPENDENCIES FOR FLUENT API: LOG4J2__
```xml
<dependency>
  <groupId>io.github.logging-context</groupId>
  <artifactId>logging-context-api</artifactId>
  <version>${logging-context.version}</version>
</dependency>
<dependency>
  <groupId>io.github.logging-context</groupId>
  <artifactId>logging-context-log4j2</artifactId>
  <version>${logging-context.version}</version>
</dependency>
```

__MAVEN DEPENDENCIES FOR FLUENT API: Log4J 1.2__
```xml
<dependency>
  <groupId>io.github.logging-context</groupId>
  <artifactId>logging-context-api</artifactId>
  <version>${logging-context.version}</version>
</dependency>
<dependency>
  <groupId>io.github.logging-context</groupId>
  <artifactId>logging-context-log4j12</artifactId>
  <version>${logging-context.version}</version>
</dependency>
```

## Logging Context Annotations

The real benefit of the package comes through the use of the `@LoggingContext` annotation, which can be applied to classes, methods, and parameters to enhance the logging context.

```
@LoggingContext("ORDERS")
class OrdersService {

  private OrdersDao ordersDao;

  private UserService userService;

  @LoggingContext("RETURNS")
  public void initiateReturn(@LoggingContext("order_id") final String orderId) {
    final Order order = ordersDao.getOrderById(orderId);
    final User user = userService.getUser(order.getUserId());
    ...
  }
}
```

When `@LoggingContext` is applied to classes or methods, calls within their scope will update the nested diagnostic context. When the annotation is applied to parameters, the parameter and its value will be added to the mapped diagnostic context. Once the scope of the call is complete, the contexts will both be updated to no longer include the values.

To achieve this, the Logging Context API must be combined with an Aspect Oriented Programming (AOP) framework like AspectJ or Spring Boot AOP.

   > NOTE: The use of an empty `@LoggingContext` on parameters will not generate proper parameter names unless the Java compiler option `-parameters` is passed.
   > Failure to compile with the parameter debugging will result in context names like `arg0`. If you see context values of that form, consider either 
   > enabling `-parameters` via your build system or explicitly providing parameter names.
    
# License

[MIT License](./LICENSE.md)

Copyright &copy; 2022 Michael Wooten
