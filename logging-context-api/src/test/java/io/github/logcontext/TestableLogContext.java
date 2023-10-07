package io.github.logcontext;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TestableLogContext implements LogContext {

  private final Set<String> nestedContexts;

  private final Map<String, String> mappedContexts;

  private boolean closed;

  public TestableLogContext(Set<String> nestedContexts, Map<String, String> mappedContexts) {
    this.nestedContexts = nestedContexts;
    this.mappedContexts = mappedContexts;
    this.closed = false;
  }

  public boolean hasNestedContext(final String context) {
    return nestedContexts.contains(context);
  }

  public boolean hasMappedContext(final String context) {
    return mappedContexts.containsKey(context);
  }

  public boolean hasMappedContextValue(final String context, final String value) {
    return Optional.ofNullable(mappedContexts.get(context))
        .map(contextValue -> contextValue.equals(value))
        .orElse(false);
  }

  public boolean isClosed() {
    return this.closed;
  }

  public boolean isEmpty() {
    return mappedContexts.isEmpty() && nestedContexts.isEmpty();
  }

  @Override
  public void close() {
    if (this.closed) {
      throw new IllegalStateException("Testable Log Context already closed");
    }
    this.closed = true;
  }
}
