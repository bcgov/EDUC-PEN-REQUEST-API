package ca.bc.gov.educ.api.penrequest.messaging;

import ca.bc.gov.educ.api.penrequest.service.EventHandlerDelegatorService;
import ca.bc.gov.educ.api.penrequest.struct.Event;
import ca.bc.gov.educ.api.penrequest.utils.JsonUtil;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static ca.bc.gov.educ.api.penrequest.constants.Topics.PEN_REQUEST_API_TOPIC;


@Component
@Slf4j
public class MessageSubscriber extends MessagePubSub {

  private final EventHandlerDelegatorService eventHandlerDelegatorService;

  @Autowired
  public MessageSubscriber(final Connection con, EventHandlerDelegatorService eventHandlerDelegatorService) {
    this.eventHandlerDelegatorService = eventHandlerDelegatorService;
    super.connection = con;
  }

  /**
   * This subscription will makes sure the messages are required to acknowledge manually to STAN.
   * Subscribe.
   */
  @PostConstruct
  public void subscribe() {
    String queue = PEN_REQUEST_API_TOPIC.toString().replace("_", "-");
    var dispatcher = connection.createDispatcher(onMessage());
    dispatcher.subscribe(PEN_REQUEST_API_TOPIC.toString(), queue);
  }

  /**
   * On message message handler.
   *
   * @return the message handler
   */
  private MessageHandler onMessage() {
    return (Message message) -> {
      if (message != null) {
        try {
          var eventString = new String(message.getData());
          var event = JsonUtil.getJsonObjectFromString(Event.class, eventString);
          eventHandlerDelegatorService.handleEvent(event);
        } catch (final Exception e) {
          log.error("Exception ", e);
        }
      }
    };
  }


}
