package io.github.logcontext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.anEmptyMap;

/**
 * The AbstractLogContextBuilderTest class provides a set of JUnit test cases for the {@link
 * AbstractLogContextBuilder} class.
 */
class AbstractLogContextBuilderTest {

    private static final String CONTEXT_1 = "CONTEXT1";

    private static final String CONTEXT_2 = "CONTEXT2";

    private static final String VALUE_1 = "value1";

    private static final String VALUE_2 = "2";

    /**
     * The abstract {@link LogContext.Builder} implementation being
     * tested.
     */
    private AbstractLogContextBuilder logContextBuilder = new ConcreteLogContextBuilder();

    /**
     * Test method for {@link AbstractLogContextBuilder#andNested(String...)} for contexts being added
     * one at a time.
     */
    @Test
    void testAndNested() {
        LogContext.Builder builder = logContextBuilder.andNested(CONTEXT_1);

        assertThat(builder, sameInstance(logContextBuilder));
        assertThat(logContextBuilder.getNestedContexts(), contains(CONTEXT_1));

        builder = logContextBuilder.andNested(CONTEXT_2);

        assertThat(builder, sameInstance(logContextBuilder));
        assertThat(logContextBuilder.getNestedContexts(), contains(CONTEXT_1, CONTEXT_2));
    }

    /**
     * Test method for {@link AbstractLogContextBuilder#andNested(String...)} for multi contexts being
     * added one at a time.
     */
    @Test
    void testAndNested_multipleNested() {
        LogContext.Builder builder = logContextBuilder.andNested(CONTEXT_1, CONTEXT_2);

        assertThat(builder, sameInstance(logContextBuilder));
        assertThat(logContextBuilder.getNestedContexts(), contains(CONTEXT_1, CONTEXT_2));
    }

    /**
     * Test method for {@link AbstractLogContextBuilder#andNested(String...)} to ensure that <code>null
     * </code> context values are ignored.
     */
    @Test
    void testAndNested_nullContexts() {
        LogContext.Builder builder = logContextBuilder.andNested(null);

        assertThat(builder, sameInstance(logContextBuilder));
        assertThat(logContextBuilder.getNestedContexts(), empty());

        logContextBuilder.andNested(null, CONTEXT_1, null, CONTEXT_2, null);

        assertThat(builder, sameInstance(logContextBuilder));
        assertThat(logContextBuilder.getNestedContexts(), contains(CONTEXT_1, CONTEXT_2));
    }

    /** Test method for {@link AbstractLogContextBuilder#andMapped(String, String)}. */
    @Test
    void testAndMapped() {
        LogContext.Builder builder = logContextBuilder.andMapped(CONTEXT_1, VALUE_1);

        assertThat(builder, sameInstance(logContextBuilder));
        assertThat(logContextBuilder.getMappedContextValues(), hasEntry(CONTEXT_1, VALUE_1));

        builder = logContextBuilder.andMapped(CONTEXT_2, VALUE_2);

        assertThat(builder, sameInstance(logContextBuilder));
        assertThat(logContextBuilder.getMappedContextValues(), hasEntry(CONTEXT_1, VALUE_1));
        assertThat(logContextBuilder.getMappedContextValues(), hasEntry(CONTEXT_2, VALUE_2));
    }

    /** Test method for {@link AbstractLogContextBuilder#andMapped(Map)}. */
    @Test
    void testAndMapped_providedMappings() {
        final Map<String, String> contextMap = new HashMap<String, String>() {{
            put(CONTEXT_1, VALUE_1);
            put(CONTEXT_2, VALUE_2);
        }};

        LogContext.Builder builder = logContextBuilder.andMapped(contextMap);

        assertThat(builder, sameInstance(logContextBuilder));
        assertThat(logContextBuilder.getMappedContextValues(), hasEntry(CONTEXT_1, VALUE_1));
        assertThat(logContextBuilder.getMappedContextValues(), hasEntry(CONTEXT_2, VALUE_2));
    }

    /**
     * Test method for {@link AbstractLogContextBuilder#andMapped(String, String)} that ensures if a
     * newer value is provided for the same mapped context the older value is replaced.
     */
    @Test
    void testAndMapped_overwriteExistingContextValue() {
        LogContext.Builder builder = logContextBuilder.andMapped(CONTEXT_1, VALUE_1);

        assertThat(builder, sameInstance(logContextBuilder));
        assertThat(logContextBuilder.getMappedContextValues(), hasEntry(CONTEXT_1, VALUE_1));

        builder = logContextBuilder.andMapped(CONTEXT_1, VALUE_2);

        assertThat(builder, sameInstance(logContextBuilder));
        assertThat(
                "logging context not replaced with newer value",
                logContextBuilder.getMappedContextValues(),
                hasEntry(CONTEXT_1, VALUE_2));
    }

    /**
     * Test method for {@link AbstractLogContextBuilder#andMapped(Map)} that ensures if the provided
     * mappings include a newer value for the same mapped context the older value is replaced.
     */
    @Test
    void testAndMapped_providedMappings_overwriteExistingContextValue() {
        LogContext.Builder builder = logContextBuilder.andMapped(CONTEXT_1, VALUE_1);

        assertThat(builder, sameInstance(logContextBuilder));
        assertThat(logContextBuilder.getMappedContextValues(), hasEntry(CONTEXT_1, VALUE_1));

        builder = logContextBuilder.andMapped(singletonMap(CONTEXT_1, VALUE_2));

        assertThat(builder, sameInstance(logContextBuilder));
        assertThat(
            "logging context not replaced with newer value from provided context mapping",
            logContextBuilder.getMappedContextValues(),
            hasEntry(CONTEXT_1, VALUE_2));
    }

    /**
     * Test method for {@link AbstractLogContextBuilder#andMapped(String, String)} that ensures a <code>
     * null</code> context is ignored.
     */
    @Test
    void testAndMapped_ignoreNullContext() {
        LogContext.Builder builder = logContextBuilder.andMapped(null, VALUE_1);

        assertThat(builder, sameInstance(logContextBuilder));
        assertThat(logContextBuilder.getMappedContextValues(), anEmptyMap());
    }

    /**
     * Test method for {@link AbstractLogContextBuilder#andMapped(String, String)} that ensures an empty
     * or entirely blank context is ignored.
     */
    @Test
    void testAndMapped_ignoreEmptyContext() {
        LogContext.Builder builder = logContextBuilder.andMapped("", VALUE_1);

        assertThat(builder, sameInstance(logContextBuilder));
        assertThat(logContextBuilder.getMappedContextValues(), anEmptyMap());

        builder = logContextBuilder.andMapped(" \t", VALUE_1);

        assertThat(builder, sameInstance(logContextBuilder));
        assertThat(logContextBuilder.getMappedContextValues(), anEmptyMap());
    }

    /**
     * A concrete implementation of {@link AbstractLogContextBuilder} for testing the concrete methods.
     * This implementation does not provide an implementation of the {@link #get()} method.
     */
    private class ConcreteLogContextBuilder extends AbstractLogContextBuilder {

        @Override
        public LogContext get() {
            throw new UnsupportedOperationException();
        }
    }
}