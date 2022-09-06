package io.github.logcontext;

import java.util.*;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

/**
 * The AbstractLogContextBuilder class provides an abstract {@link LogContext.Builder} implementation that
 * keeps track of both nested and mapped diagnostic context values. Subclasses can retrieve references to
 * the context values via the {@link #getNestedContexts()} and {@link #getMappedContextValues()} methods.
 */
public abstract class AbstractLogContextBuilder implements LogContext.Builder {

    /**
     * The list of Nested Diagnostic Context (NDC) values to add.
     */
    private final List<String> nestedContexts;

    /**
     * The mapping of Mapped Diagnostic Context (MDC) keys to their values.
     */
    private final Map<String, String> mappedContextValues;

    /**
     * Constructs a new instance of AbstractLogContextBuilder. This is only meant to be called directly
     * by a subclass constructor.
     */
    protected AbstractLogContextBuilder() {
        this.nestedContexts = new ArrayList<>();
        this.mappedContextValues = new LinkedHashMap<>();
    }

    /**
     * Adds additional Nested Diagnostic Context (NDC) information to the logging context.
     *
     * @param context the NDC values to add to the logging context.
     * @return a reference to the {Builder} for chaining.
     */
    public LogContext.Builder andNested(final String... context) {
        final Optional<List<String>> stringContexts =
                Optional.ofNullable(context)
                        .map(Arrays::asList)
                        .map(temp -> temp.stream().filter(Objects::nonNull).collect(toList()));
        stringContexts.ifPresent(this.nestedContexts::addAll);

        return this;
    }

    /**
     * Adds a Mapped Diagnostic Context (MDC) value to the logging context.
     *
     * @param context the name of the MDC key associated with the value to be added.
     * @param value   the value of the MDC attribute.
     * @return a reference to the {@link LogContext.Builder} for chaining.
     */
    public LogContext.Builder andMapped(final String context, final String value) {
        if (nonNull(context) && !context.trim().isEmpty()) {
            this.mappedContextValues.put(context, value);
        }

        return this;
    }

    /**
     * Returns an immutable copy of the current Nested Diagnostic Context values.
     *
     * @return an immutable copy of the current Nested Diagnostic Context values.
     */
    public List<String> getNestedContexts() {
        return unmodifiableList(nestedContexts);
    }

    /**
     * Returns an immutable copy of the current Mapped Diagnostic Context values.
     *
     * @return an immutable copy of the current Mapped Diagnostic Context values.
     */
    public Map<String, String> getMappedContextValues() {
        return unmodifiableMap(mappedContextValues);
    }
}
