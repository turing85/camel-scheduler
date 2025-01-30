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
            .poolSize(2)
            .advanced()
                .synchronous(true))
        .routeId(ROUTE_ID)
        .setProperty(Exchange.TIMER_COUNTER, counter::incrementAndGet)
        .log("Begin ${exchangeProperty.%s}".formatted(Exchange.TIMER_COUNTER))
        .delay(Duration.ofSeconds(5).toMillis())
        .log("End ${exchangeProperty.%s}".formatted(Exchange.TIMER_COUNTER));
    // @formatter:on
  }
}
