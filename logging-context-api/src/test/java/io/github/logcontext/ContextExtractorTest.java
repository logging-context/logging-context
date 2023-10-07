package io.github.logcontext;

import static io.github.logcontext.ContextExtractor.NO_OP_EXTRACTOR;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * The ContextExtractorTest class provides a set of JUnit test cases for the
 * {@link ContextExtractor} class.
 * </p>
 */
class ContextExtractorTest {

  /**
   * Test method for {@link ContextExtractor#NO_OP_EXTRACTOR}.
   */
  @Test
  void testNoOpExtractor() {
    assertNotNull(NO_OP_EXTRACTOR);

    final Map<String, String> extractedObjectContext = NO_OP_EXTRACTOR.apply(new Object());
    assertNotNull(extractedObjectContext, "expected generic object to result in a non-null value");
    assertTrue(extractedObjectContext.isEmpty(), "expected generic object to return an empty map");

    final Map<String, String> extractedNullContext = NO_OP_EXTRACTOR.apply(new Object());
    assertNotNull(extractedObjectContext, "expected null argument to result in a non-null value");
    assertTrue(extractedObjectContext.isEmpty(), "expected null argument to return an empty map");
  }

}