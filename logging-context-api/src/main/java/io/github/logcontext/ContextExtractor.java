package io.github.logcontext;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public interface ContextExtractor<T> extends Function<T, Map<String, String>> {

  ContextExtractor<Object> NO_OP_EXTRACTOR = (v) -> Collections.emptyMap();

}
