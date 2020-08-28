package ca.bc.gov.educ.api.penrequest.schedulers;

import ca.bc.gov.educ.api.penrequest.constants.EventOutcome;
import ca.bc.gov.educ.api.penrequest.constants.EventType;
import ca.bc.gov.educ.api.penrequest.messaging.MessagePublisher;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEvent;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestEventRepository;
import ca.bc.gov.educ.api.penrequest.struct.Event;
import ca.bc.gov.educ.api.penrequest.struct.PenRequest;
import ca.bc.gov.educ.api.penrequest.utils.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static ca.bc.gov.educ.api.penrequest.constants.EventOutcome.PEN_REQUEST_FOUND;
import static ca.bc.gov.educ.api.penrequest.constants.EventStatus.DB_COMMITTED;
import static ca.bc.gov.educ.api.penrequest.constants.EventType.*;
import static ca.bc.gov.educ.api.penrequest.constants.Topics.PEN_REQUEST_API_TOPIC;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test-event")
@SpringBootTest
public class EventTaskSchedulerTest {
  public static final String STUDENT_PROFILE_SAGA_API_TOPIC = "STUDENT_PROFILE_SAGA_API_TOPIC";
  @Autowired
  PenRequestEventRepository penRequestEventRepository;
  @Autowired
  private EventTaskScheduler eventTaskScheduler;

  @Autowired
  private MessagePublisher messagePublisher;

  @After
  public void tearDown(){
    penRequestEventRepository.deleteAll();
  }
  @Test
  public void testEventTaskScheduler_givenNoRecords_shouldDoNothing() throws Exception{
    eventTaskScheduler.pollEventTableAndPublish();
    verify(messagePublisher, never()).dispatchMessage(PEN_REQUEST_API_TOPIC.toString(),"".getBytes());
  }

  @Test
  public void testEventTaskScheduler_givenOneRecordWithReplyTo_shouldInvokeMessagePublisherToPublishMessage() throws Exception{

    var sagaId = UUID.randomUUID();
    var penRequestEvent = PenRequestEvent.builder().sagaId(sagaId).replyChannel(STUDENT_PROFILE_SAGA_API_TOPIC).eventType(GET_PEN_REQUEST.toString()).eventOutcome(PEN_REQUEST_FOUND.toString()).eventStatus(DB_COMMITTED.toString()).createDate(LocalDateTime.now()).createUser("TEST").build();
    penRequestEventRepository.save(penRequestEvent);
    eventTaskScheduler.pollEventTableAndPublish();
    var eventInBytes = JsonUtil.getJsonStringFromObject(Event.builder()
        .sagaId(penRequestEvent.getSagaId())
        .eventType(EventType.valueOf(penRequestEvent.getEventType()))
        .eventOutcome(EventOutcome.valueOf(penRequestEvent.getEventOutcome()))
        .eventPayload(penRequestEvent.getEventPayload()).build()).getBytes();
    var selfEventInBytes = JsonUtil.getJsonStringFromObject(Event.builder().eventType(PEN_REQUEST_EVENT_OUTBOX_PROCESSED).eventPayload(penRequestEvent.getEventId().toString()).build()).getBytes();
    doNothing().when(messagePublisher).dispatchMessage(STUDENT_PROFILE_SAGA_API_TOPIC,eventInBytes);
    verify(messagePublisher, atMostOnce()).dispatchMessage(STUDENT_PROFILE_SAGA_API_TOPIC,eventInBytes);
    verify(messagePublisher, atMostOnce()).dispatchMessage(PEN_REQUEST_API_TOPIC.toString(),selfEventInBytes);
  }

}