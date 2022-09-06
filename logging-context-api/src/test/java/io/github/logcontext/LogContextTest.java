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
   * Test method for {@link LogContext#closeableLogContext(AutoCloseable)} that ensures a {@link
   * NullPointerException} is thrown for a <code>null</code> closeable.
   */
  @Test
  void testCloseableLogContext_nullCloseable() {
    assertThrows(NullPointerException.class, () -> LogContext.closeableLogContext(null));
  }
}
