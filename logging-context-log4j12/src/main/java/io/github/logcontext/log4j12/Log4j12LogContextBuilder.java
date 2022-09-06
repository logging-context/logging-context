package io.github.logcontext.log4j12;

import static java.util.Collections.reverse;
import static java.util.stream.Collectors.toList;

import io.github.logcontext.AbstractLogContextBuilder;
import io.github.logcontext.LogContext;
import io.github.logcontext.LogContext.Builder;

import io.github.logcontext.MultiCloseableLogContext;
import java.util.List;
import java.util.stream.Stream;

import org.apache.log4j.MDC;
import org.apache.log4j.NDC;


/**
 * The Log4j12LogContextBuilder class provides a {@link LogContext.Builder} implementation that
 * updates the Log4J 1.2 logging contexts with additional nested and mapped context values.
 * The logging context will not be updated until the {@link #get()} method is called. Subsequent
 * calls to {@link #get()} will update the context with the same values.
 */
public class Log4j12LogContextBuilder extends AbstractLogContextBuilder {

  /**
   * Constructs a new instance of {@link Builder}. This method is not intended for direct use.
   *
   * @see LogContext.Builder#nestedContext(String...)
   * @see LogContext.Builder#mappedContext(String, String)
   */
  public Log4j12LogContextBuilder() {
    super();
  }

  /**
   * Updates the Nested Diagnostic Context and Mapped Diagnostic Contexts with the configured values
   * and returns a {@link LogContext} that can be used to remove the logging context updates.
   *
   * @return a {@link LogContext} that can be used to remove the updates to the logging contexts.
   */
  public LogContext get() {
    final List<AutoCloseable> closeables =
        Stream.concat(
                getNestedContexts().stream()
                    .map(
                        nestedContext -> {
                          NDC.push(nestedContext);
                          return new NDCCloseable(nestedContext);
                        }),
                getMappedContextValues().entrySet().stream()
                    .map(
                        mappedContextEntry -> {
                            MDC.put(
                                mappedContextEntry.getKey(), mappedContextEntry.getValue());
                        return (LogContext) () -> MDC.remove(mappedContextEntry.getKey());}))
            .collect(toList());
    reverse(closeables);
    return new MultiCloseableLogContext(closeables);
  }
}
