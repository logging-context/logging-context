package io.github.logcontext.fixtures;

import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;

import io.github.logcontext.ContextExtractor;
import java.util.Map;

public final class PersonContextExtractor implements ContextExtractor<Person> {

  @Override
  public Map<String, String> apply(final Person person) {
    if (isNull(person)) {
      return emptyMap();
    }

    return person.toContext();
  }
}
