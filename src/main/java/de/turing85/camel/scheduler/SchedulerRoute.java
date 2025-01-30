package de.turing85.camel.scheduler;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.scheduler;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.seda;

@SuppressWarnings("unused")
public class SchedulerRoute extends RouteBuilder {
  private static final String ROUTE_ID_SCHEUDLER = "my-scheduler";
  private static final String ROUTE_ID_SEDA = "queue";

  private final AtomicInteger counter = new AtomicInteger(0);

  @Override
  public void configure() {
    // @formatter:off
    from(
        scheduler(ROUTE_ID_SCHEUDLER)
            .delay(Duration.ofSeconds(1).toMillis())
            .useFixedDelay(false))
        .routeId(ROUTE_ID_SCHEUDLER)
        .setProperty(Exchange.TIMER_COUNTER, counter::incrementAndGet)
        .log("Init ${exchangeProperty.%s}".formatted(Exchange.TIMER_COUNTER))
        .to(seda(ROUTE_ID_SEDA)
            .size(2)
            .blockWhenFull(true));

    from(
        seda(ROUTE_ID_SEDA)
            .concurrentConsumers(2))
        .routeId(ROUTE_ID_SEDA)
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
