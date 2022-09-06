package io.github.logcontext;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The MultiCloseableLogContextTest class provides a set of JUnit test cases for the {@link
 * MultiCloseableLogContext} class.
 */
@ExtendWith(MockitoExtension.class)
class MultiCloseableLogContextTest {

  @Mock private AutoCloseable closeable1;

  @Mock private AutoCloseable closeable2;

  @Mock private AutoCloseable closeable3;

  /**
   * Test method for {@link MultiCloseableLogContext#MultiCloseableLogContext(List)} that ensures a
   * {@link NullPointerException} is thrown for a <code>null</code> list of closeables.
   */
  @Test
  void testMultiCloseableLogContext_nullListOfCloseables() {
    assertThrows(NullPointerException.class, () -> new MultiCloseableLogContext(null));
  }

  /** Test method for {@link MultiCloseableLogContext#close()}. */
  @Test
  void testClose() throws Exception {
    final List<AutoCloseable> closeables = asList(closeable1, closeable2, closeable3);
    final MultiCloseableLogContext multiCloseableLogContext =
        new MultiCloseableLogContext(closeables);

    multiCloseableLogContext.close();

    verify(closeable1).close();
    verify(closeable2).close();
    verify(closeable3).close();
  }

  /**
   * Test method for {@link MultiCloseableLogContext#close()} that ensures <code>null</code> values
   * are ignored.
   */
  @Test
  void testClose_includesNullValues() throws Exception {
    final List<AutoCloseable> closeables =
        asList(null, closeable1, null, closeable2, closeable3, null);
    final MultiCloseableLogContext multiCloseableLogContext =
        new MultiCloseableLogContext(closeables);

    multiCloseableLogContext.close();

    verify(closeable1).close();
    verify(closeable2).close();
    verify(closeable3).close();
  }

  /**
   * Test method for {@link MultiCloseableLogContext#close()} that ensures if any exceptions are
   * thrown that all of the closeables are closed and the last exception is thrown.
   */
  @Test
  void testClose_thrownExceptions() throws Exception {
    final List<AutoCloseable> closeables = asList(closeable1, closeable2, closeable3);
    final MultiCloseableLogContext multiCloseableLogContext =
        new MultiCloseableLogContext(closeables);
    final Exception exception1 = new Exception();
    final Exception exception2 = new Exception();

    Mockito.doThrow(exception1).when(closeable1).close();
    Mockito.doThrow(exception2).when(closeable3).close();

    assertThrows(Exception.class, () -> multiCloseableLogContext.close());

    verify(closeable1).close();
    verify(closeable2).close();
    verify(closeable3).close();
  }
}
