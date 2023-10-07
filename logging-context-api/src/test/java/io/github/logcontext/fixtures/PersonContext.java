package io.github.logcontext.fixtures;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

public final class PersonContext {

  public static final String NAME_CONTEXT = "name";

  public static final String NAME_CONTEXT_VALUE = "John";

  public static final String BIRTHDATE_CONTEXT = "dob";

  public static final LocalDate BIRTHDATE_CONTEXT_DATE = LocalDate.of(2001, Month.AUGUST, 21);

  public static final String BIRTHDATE_CONTEXT_VALUE =
      BIRTHDATE_CONTEXT_DATE.format(DateTimeFormatter.BASIC_ISO_DATE);

  public static final Person PERSON = new Person(NAME_CONTEXT_VALUE, BIRTHDATE_CONTEXT_DATE);

  public static final Entry<String, String> PERSON_CONTEXT_NAME =
      new SimpleImmutableEntry<>(NAME_CONTEXT, NAME_CONTEXT_VALUE);

  public static final Entry<String, String> PERSON_CONTEXT_BIRTHDATE =
      new SimpleImmutableEntry<>(BIRTHDATE_CONTEXT, BIRTHDATE_CONTEXT_VALUE);

  public static final Map<String, String> PERSON_CONTEXT = Stream.of(PERSON_CONTEXT_NAME,
          PERSON_CONTEXT_BIRTHDATE)
      .collect(toMap(Entry::getKey, Entry::getValue));

  public static final String PERSON_CONTEXT_PREFIX = "employee.";

  public static final Map<String, String> PERSON_CONTEXT_PREFIXED = Stream.of(
          new SimpleImmutableEntry<>(PERSON_CONTEXT_PREFIX + NAME_CONTEXT, NAME_CONTEXT_VALUE),
          new SimpleImmutableEntry<>(PERSON_CONTEXT_PREFIX + BIRTHDATE_CONTEXT, BIRTHDATE_CONTEXT_VALUE))
      .collect(toMap(Entry::getKey, Entry::getValue));

}