package de.turing85.camel.scheduler;

import org.apache.camel.main.Main;

public class Starter {
  public static void main(final String... args) throws Exception {
    final Main camel = new Main(Starter.class);
    camel.run(args);
  }
}
