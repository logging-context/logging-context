package io.github.logcontext.fixtures;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Person {

  private final String name;

  private final LocalDate birthdate;

  public Person(String name, LocalDate birthdate) {
    this.name = name;
    this.birthdate = birthdate;
  }

  public String getName() {
    return name;
  }

  public LocalDate getBirthdate() {
    return birthdate;
  }

  public Map<String, String> toContext() {
    final Map<String, String> personContext = new LinkedHashMap<>();
    personContext.put(PersonContext.NAME_CONTEXT, getName());
    personContext.put(PersonContext.BIRTHDATE_CONTEXT,
        getBirthdate().format(DateTimeFormatter.BASIC_ISO_DATE));
    return personContext;
  }
}
