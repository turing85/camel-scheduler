package de.turing85.camel.scheduler;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.scheduler;

@SuppressWarnings("unused")
public class SchedulerRoute extends RouteBuilder {
  private static final String ROUTE_ID = "my-scheduler";

  private final AtomicInteger counter = new AtomicInteger(0);

  @Override
  public void configure() {
    // @formatter:off
    from(
        scheduler(ROUTE_ID)
            .delay(Duration.ofSeconds(1).toMillis())
            .useFixedDelay(false)
            .poolSize(1))
        .routeId(ROUTE_ID)
        .threads(2)
        .setProperty(Exchange.TIMER_COUNTER, counter::incrementAndGet)
        .log("Begin ${exchangeProperty.%s}".formatted(Exchange.TIMER_COUNTER))
        .process(exchange -> sleepFor(Duration.ofSeconds(5)))
        .log("End ${exchangeProperty.%s}".formatted(Exchange.TIMER_COUNTER));
    // @formatter:on
  }

  private static void sleepFor(final Duration duration) {
    while (true) {
      try {
        Thread.sleep(duration.toMillis());
        break;
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }
}
