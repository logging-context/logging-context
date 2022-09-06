package io.github.logcontext;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import io.github.logcontext.LogContext.Builder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestLogContextBuilder implements Builder {

  private final Set<String> nestedContexts = new HashSet<>();

  private final Map<String, String> mappedContexts = new HashMap<>();

  @Override
  public Builder andNested(String... context) {
    List<String> contexts = asList(requireNonNull(context, "context must not be null"));
    if (contexts.isEmpty()) {
      throw new IllegalArgumentException("Context must contain at least one value");
    }
    if (contexts.contains(null)) {
      throw new IllegalArgumentException("Nested context values must not be null");
    }
    nestedContexts.addAll(asList(context));
    return this;
  }

  @Override
  public Builder andMapped(String context, String value) {
    requireNonNull(context, "context must not be null");
    mappedContexts.put(context, value);
    return this;
  }

  @Override
  public LogContext get() {
    return new TestableLogContext(nestedContexts, mappedContexts);
  }
}
