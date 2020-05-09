package ca.bc.gov.educ.api.penrequest.messaging;
import ca.bc.gov.educ.api.penrequest.props.ApplicationProperties;
import ca.bc.gov.educ.api.penrequest.service.EventHandlerService;
import ca.bc.gov.educ.api.penrequest.struct.Event;
import ca.bc.gov.educ.api.penrequest.utils.JsonUtil;
import io.nats.streaming.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static ca.bc.gov.educ.api.penrequest.constants.Topics.PEN_REQUEST_API_TOPIC;
import static lombok.AccessLevel.PRIVATE;

/**
 * This listener uses durable queue groups of nats streaming client.
 * A durable queue group allows you to have all members leave but still maintain state. When a member re-joins, it starts at the last position in that group.
 * <b>DO NOT call unsubscribe on the subscription.</b> please see the below for details.
 * Closing the Group
 * The last member calling Unsubscribe will close (that is destroy) the group. So if you want to maintain durability of the group,
 * <b>you should not be calling Unsubscribe.</b>
 * <p>
 * So unlike for non-durable queue subscribers, it is possible to maintain a queue group with no member in the server.
 * When a new member re-joins the durable queue group, it will resume from where the group left of, actually first receiving
 * all unacknowledged messages that may have been left when the last member previously left.
 */
@Component
@Slf4j
public class MessageSubscriber {

  @Getter(PRIVATE)
  private final EventHandlerService eventHandlerService;
  private StreamingConnection connection;
  private StreamingConnectionFactory connectionFactory;

  @Autowired
  public MessageSubscriber(final ApplicationProperties applicationProperties, final EventHandlerService eventHandlerService) throws IOException, InterruptedException {
    this.eventHandlerService = eventHandlerService;
    Options options = new Options.Builder().maxPingsOut(100)
            .natsUrl(applicationProperties.getNatsUrl())
            .clusterId(applicationProperties.getNatsClusterId())
            .clientId("pen-request-api-subscriber" + UUID.randomUUID().toString())
            .connectionLostHandler(this::connectionLostHandler).build();
    connectionFactory = new StreamingConnectionFactory(options);
    connection = connectionFactory.createConnection();
  }

  @PostConstruct
  public void subscribe() throws InterruptedException, TimeoutException, IOException {
    SubscriptionOptions options = new SubscriptionOptions.Builder().durableName("pen-request-api-consumer").build();
    connection.subscribe(PEN_REQUEST_API_TOPIC.toString(), "pen-request", this::onPenRequestTopicMessage, options);
  }

  /**
   * This method will process the event message pushed into the queue.
   *
   * @param message the string representation of {@link Event} if it not type of event then it will throw exception and will be ignored.
   */
  private void onPenRequestTopicMessage(Message message) {
    if (message != null) {
      try {
        String eventString = new String(message.getData());
        Event event = JsonUtil.getJsonObjectFromString(Event.class, eventString);
        getEventHandlerService().handleEvent(event);
      } catch (final Exception ex) {
        log.error("Exception ", ex);
      }
    }
  }


  /**
   * This method will keep retrying for a connection.
   */
  @SuppressWarnings("java:S2142")
  private void connectionLostHandler(StreamingConnection streamingConnection, Exception e) {
    if (e != null) {
      int numOfRetries = 1;
      while (true) {
        try {
          log.trace("retrying connection as connection was lost :: retrying ::" + numOfRetries++);
          connection = connectionFactory.createConnection();
          this.subscribe();
          break;
        } catch (IOException | InterruptedException | TimeoutException ex) {
          log.error("exception occurred", ex);
          try {
            double sleepTime = (2 * numOfRetries);
            TimeUnit.SECONDS.sleep((long) sleepTime);
          } catch (InterruptedException exc) {
            log.error("exception occurred", exc);
          }

        }
      }
    }
  }
}