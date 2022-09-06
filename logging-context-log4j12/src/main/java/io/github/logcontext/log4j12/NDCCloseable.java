package io.github.logcontext.log4j12;

import org.apache.log4j.NDC;

/**
 * The {@link NDCCloseable} class provides an {@link AutoCloseable} that will automatically
 * remove the nested diagnostic context value when closed.
 */
public class NDCCloseable implements AutoCloseable {

  /** The nested diagnostic context value to remove. */
  private final String nestedContext;

  /**
   * Constructs a new instance of NDCCloseable.
   *
   * @param nestedContext the nested diagnostic context value to remove.
   */
  public NDCCloseable(final String nestedContext) {
    this.nestedContext = nestedContext;
  }

  @Override
  public void close() throws Exception {
    String context = NDC.pop();
    assert nestedContext.equals(context)
        : "unexpected context " + context + " removed instead of " + nestedContext;
  }
}
