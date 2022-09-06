package io.github.logcontext.log4j12;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.log4j.NDC;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The NDCCloseableTest class provides a set of JUnit test cases for the {@link NDCCloseable} class.
 */
@ExtendWith(MockitoExtension.class)
class NDCCloseableTest {

  /** Test method for {@link NDCCloseable#close()}. */
  @Test
  void testClose() throws Exception {
    final String context = "users";
    try (final MockedStatic<NDC> mockedNdc = Mockito.mockStatic(NDC.class)) {
      mockedNdc.when(NDC::pop).thenReturn(context);

      final NDCCloseable ndcCloseable = new NDCCloseable(context);

      mockedNdc.verifyNoInteractions();

      ndcCloseable.close();

      mockedNdc.verify(NDC::pop);
    }
  }

  /**
   * Test method for {@link NDCCloseable#close()} that ensures an {@link AssertionError} is thrown
   * if the expected NDC context value is not the one removed.
   */
  @Test
  void testClose_assertionErrorOnIncorrectContext() throws Exception {
    final String context = "users";
    final String wrongContext = "wrong";
    try (final MockedStatic<NDC> mockedNdc = Mockito.mockStatic(NDC.class)) {
      mockedNdc.when(NDC::pop).thenReturn(wrongContext);

      final NDCCloseable ndcCloseable = new NDCCloseable(context);

      mockedNdc.verifyNoInteractions();

      assertThrows(AssertionError.class, () -> ndcCloseable.close());

      mockedNdc.verify(NDC::pop);
    }
  }
}
