package io.github.logcontext.example.log4j2;

import io.github.logcontext.spring.SpringAopLoggingContextConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(value = SpringAopLoggingContextConfiguration.class)
public class Log4j2ExampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(Log4j2ExampleApplication.class, args);
  }

  @Bean
  public CommandLineRunner commandLineRunner(final Greeter greeter) {
    return args -> {
      greeter.greet(args.length > 0 ? args[0] : "World");
    };
  }
}
