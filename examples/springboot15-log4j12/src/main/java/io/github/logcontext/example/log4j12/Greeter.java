package io.github.logcontext.example.log4j12;

import io.github.logcontext.LoggingContext;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@LoggingContext("Greeter")
public class Greeter {

  /** The logger used to log error messages and debugging statements. */
  private static final Logger LOGGER = Logger.getLogger(Greeter.class);

  @LoggingContext()
  public void greet(@LoggingContext() final String whom) {
    LOGGER.info("Just saying hi");
    System.out.println("Hello, " + whom);
  }
}
