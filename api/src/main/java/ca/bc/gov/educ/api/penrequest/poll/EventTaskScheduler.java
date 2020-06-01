package ca.bc.gov.educ.api.penrequest.poll;

import ca.bc.gov.educ.api.penrequest.constants.EventOutcome;
import ca.bc.gov.educ.api.penrequest.constants.EventType;
import ca.bc.gov.educ.api.penrequest.messaging.MessagePublisher;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEvent;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestEventRepository;
import ca.bc.gov.educ.api.penrequest.struct.Event;
import ca.bc.gov.educ.api.penrequest.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static ca.bc.gov.educ.api.penrequest.constants.EventStatus.DB_COMMITTED;
import static ca.bc.gov.educ.api.penrequest.constants.EventType.PEN_REQUEST_EVENT_OUTBOX_PROCESSED;
import static ca.bc.gov.educ.api.penrequest.constants.Topics.PEN_REQUEST_API_TOPIC;
import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
public class EventTaskScheduler {

  @Getter(PRIVATE)
  private final MessagePublisher messagePubSub;
  @Getter(PRIVATE)
  private final PenRequestEventRepository penRequestEventRepository;

  @Autowired
  public EventTaskScheduler(MessagePublisher messagePubSub, PenRequestEventRepository penRequestEventRepository) {
    this.messagePubSub = messagePubSub;
    this.penRequestEventRepository = penRequestEventRepository;
  }

  @Scheduled(cron = "0/1 * * * * *")
  @SchedulerLock(name = "EventTablePoller",
          lockAtLeastFor = "900ms", lockAtMostFor = "950ms")
  public void pollEventTableAndPublish() throws InterruptedException, IOException, TimeoutException {
    List<PenRequestEvent> events = getPenRequestEventRepository().findByEventStatus(DB_COMMITTED.toString());
    if (!events.isEmpty()) {
      for (PenRequestEvent event : events) {
        try {
          if (event.getReplyChannel() != null) {
            getMessagePubSub().dispatchMessage(event.getReplyChannel(), penRequestEventProcessed(event));
          }
          getMessagePubSub().dispatchMessage(PEN_REQUEST_API_TOPIC.toString(), createOutboxEvent(event));
        } catch (InterruptedException | TimeoutException | IOException e) {
          log.error("exception occurred", e);
          throw e;
        }
      }
    }
  }

  private byte[] penRequestEventProcessed(PenRequestEvent penRequestEvent) throws JsonProcessingException {
    Event event = Event.builder()
            .sagaId(penRequestEvent.getSagaId())
            .eventType(EventType.valueOf(penRequestEvent.getEventType()))
            .eventOutcome(EventOutcome.valueOf(penRequestEvent.getEventOutcome()))
            .eventPayload(penRequestEvent.getEventPayload()).build();
    return JsonUtil.getJsonStringFromObject(event).getBytes();
  }

  private byte[] createOutboxEvent(PenRequestEvent penRequestEvent) throws JsonProcessingException {
    Event event = Event.builder().eventType(PEN_REQUEST_EVENT_OUTBOX_PROCESSED).eventPayload(penRequestEvent.getEventId().toString()).build();
    return JsonUtil.getJsonStringFromObject(event).getBytes();
  }
}
