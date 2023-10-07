package io.github.logcontext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

/** The LogContextTest class provides a set of JUnit test cases for the {@link LogContext} class. */
class LogContextTest {

  /** Test method for {@link LogContext#closeableLogContext(AutoCloseable)}. */
  @Test
  void testCloseableLogContext() throws Exception {
    AtomicBoolean closed = new AtomicBoolean(false);
    final AutoCloseable closeable = () -> closed.set(true);

    final LogContext logContext = LogContext.closeableLogContext(closeable);

    assertThat(logContext, notNullValue());

    logContext.close();

    assertThat("wrapped AutoCloseable has been closed", closed.get(), equalTo(true));
  }

  /**
   * Test method for {@link LogContext#closeableLogContext(AutoCloseable)} that ensures any thrown
   * exceptions from the provided {@link AutoCloseable} are swallowed and not propagated.
   * */
  @Test
  void testCloseableLogContext_swallowsExceptions() throws Exception {
    final AutoCloseable failingCloseable = () -> {
      throw new RuntimeException("Fail");
    };

    final LogContext logContext = LogContext.closeableLogContext(failingCloseable);

    assertThat(logContext, notNullValue());

    logContext.close();

    assertDoesNotThrow(logContext::close);
  }

  /**
   * Test method for {@link LogContext#closeableLogContext(AutoCloseable)} that ensures a {@link
   * NullPointerException} is thrown for a <code>null</code> closeable.
   */
  @Test
  void testCloseableLogContext_nullCloseable() {
    assertThrows(NullPointerException.class, () -> LogContext.closeableLogContext(null));
  }
}
