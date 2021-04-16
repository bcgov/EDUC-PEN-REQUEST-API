package ca.bc.gov.educ.api.penrequest.service;


import ca.bc.gov.educ.api.penrequest.BasePenRequestAPITest;
import ca.bc.gov.educ.api.penrequest.constants.EventType;
import ca.bc.gov.educ.api.penrequest.mappers.PenRequestEntityMapper;
import ca.bc.gov.educ.api.penrequest.messaging.MessagePublisher;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.repository.DocumentRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestCommentRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestEventRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.struct.Event;
import ca.bc.gov.educ.api.penrequest.struct.PenRequest;
import ca.bc.gov.educ.api.penrequest.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static ca.bc.gov.educ.api.penrequest.constants.EventOutcome.PEN_REQUEST_FOUND;
import static ca.bc.gov.educ.api.penrequest.constants.EventOutcome.PEN_REQUEST_UPDATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;


public class EventHandlerDelegatorServiceTest extends BasePenRequestAPITest {
  @Autowired
  PenRequestRepository penRequestRepository;

  PenRequestEntityMapper mapper = PenRequestEntityMapper.mapper;
  @Autowired
  private PenRequestEventRepository penRequestEventRepository;
  @Autowired
  private PenRequestCommentRepository penRequestCommentRepository;

  @Autowired
  private DocumentRepository documentRepository;
  @Autowired
  MessagePublisher messagePublisher;

  @Autowired
  EventHandlerDelegatorService eventHandlerDelegatorService;
  @Captor
  ArgumentCaptor<byte[]> eventCaptor;

  UUID penRequestID;
  @Before
  public void setUp() {
    openMocks(this);
    final PenRequestEntity penRequestEntity = this.mapper.toModel(this.getPenRequestEntityFromJsonString());
    this.penRequestRepository.save(penRequestEntity);
    this.penRequestID = penRequestEntity.getPenRequestID();
  }

  @After
  public void tearDown() {
    this.documentRepository.deleteAll();
    this.penRequestCommentRepository.deleteAll();
    this.penRequestEventRepository.deleteAll();
    this.penRequestRepository.deleteAll();
    Mockito.clearInvocations(this.messagePublisher);
  }

  @Test
  public void handleEventUpdatePenRequest_givenDBOperationFailed_shouldNotSendResponseMessageToNATS() throws JsonProcessingException {
    final var penReq = this.getPenRequestEntityFromJsonString();
    penReq.setPenRequestID(this.penRequestID.toString());
    penReq.setLegalLastName(null);
    final Event event = Event.builder()
      .eventType(EventType.UPDATE_PEN_REQUEST)
      .eventPayload(JsonUtil.getJsonStringFromObject(penReq))
      .replyTo("PROFILE_REQUEST_SAGA_TOPIC")
      .sagaId(UUID.randomUUID())
      .build();
    this.eventHandlerDelegatorService.handleEvent(event);
    verify(this.messagePublisher, never()).dispatchMessage(eq("PROFILE_REQUEST_SAGA_TOPIC"), this.eventCaptor.capture());
  }

  @Test
  public void handleEventUpdatePenRequest_givenDBOperationSuccess_shouldSendResponseMessageToNATS() throws JsonProcessingException {
    final var penReq = this.getPenRequestEntityFromJsonString();
    penReq.setPenRequestID(this.penRequestID.toString());
    final Event event = Event.builder()
      .eventType(EventType.UPDATE_PEN_REQUEST)
      .eventPayload(JsonUtil.getJsonStringFromObject(penReq))
      .replyTo("PROFILE_REQUEST_SAGA_TOPIC")
      .sagaId(UUID.randomUUID())
      .build();
    this.eventHandlerDelegatorService.handleEvent(event);
    verify(this.messagePublisher, atLeastOnce()).dispatchMessage(eq("PROFILE_REQUEST_SAGA_TOPIC"), this.eventCaptor.capture());
    final var natsResponse = new String(this.eventCaptor.getValue());
    assertThat(natsResponse.contains(PEN_REQUEST_UPDATED.toString())).isTrue();
  }

  @Test
  public void handleEventUpdatePenRequest_givenReplayAndDBOperationSuccess_shouldSendResponseMessageToNATS() throws JsonProcessingException {
    final var penReq = this.getPenRequestEntityFromJsonString();
    penReq.setPenRequestID(this.penRequestID.toString());
    final Event event = Event.builder()
      .eventType(EventType.UPDATE_PEN_REQUEST)
      .eventPayload(JsonUtil.getJsonStringFromObject(penReq))
      .replyTo("PROFILE_REQUEST_SAGA_TOPIC")
      .sagaId(UUID.randomUUID())
      .build();
    this.eventHandlerDelegatorService.handleEvent(event);
    this.eventHandlerDelegatorService.handleEvent(event);
    verify(this.messagePublisher, atLeast(2)).dispatchMessage(eq("PROFILE_REQUEST_SAGA_TOPIC"), this.eventCaptor.capture());
    final var natsResponse = new String(this.eventCaptor.getValue());
    assertThat(natsResponse.contains(PEN_REQUEST_UPDATED.toString())).isTrue();
  }


  @Test
  public void handleEventGetPenRequest_givenDBOperationFailed_shouldNotSendResponseMessageToNATS() {
    final Event event = Event.builder()
      .eventType(EventType.GET_PEN_REQUEST)
      .eventPayload("invalid pen request id")
      .replyTo("PROFILE_REQUEST_SAGA_TOPIC")
      .sagaId(UUID.randomUUID())
      .build();
    this.eventHandlerDelegatorService.handleEvent(event);
    verify(this.messagePublisher, never()).dispatchMessage(eq("PROFILE_REQUEST_SAGA_TOPIC"), this.eventCaptor.capture());
  }

  @Test
  public void handleEventGetPenRequest_givenDBOperationSuccess_shouldSendResponseMessageToNATS() {
    final Event event = Event.builder()
      .eventType(EventType.GET_PEN_REQUEST)
      .eventPayload(this.penRequestID.toString())
      .replyTo("PROFILE_REQUEST_SAGA_TOPIC")
      .sagaId(UUID.randomUUID())
      .build();
    this.eventHandlerDelegatorService.handleEvent(event);
    verify(this.messagePublisher, atLeastOnce()).dispatchMessage(eq("PROFILE_REQUEST_SAGA_TOPIC"), this.eventCaptor.capture());
    final var natsResponse = new String(this.eventCaptor.getValue());
    assertThat(natsResponse.contains(PEN_REQUEST_FOUND.toString())).isTrue();
  }

  @Test
  public void handleEventGetPenRequest_givenReplayAndDBOperationSuccess_shouldSendResponseMessageToNATS() {
    final var penReq = this.getPenRequestEntityFromJsonString();
    penReq.setPenRequestID(this.penRequestID.toString());
    final Event event = Event.builder()
      .eventType(EventType.GET_PEN_REQUEST)
      .eventPayload(this.penRequestID.toString())
      .replyTo("PROFILE_REQUEST_SAGA_TOPIC")
      .sagaId(UUID.randomUUID())
      .build();
    this.eventHandlerDelegatorService.handleEvent(event);
    this.eventHandlerDelegatorService.handleEvent(event);
    verify(this.messagePublisher, atLeast(2)).dispatchMessage(eq("PROFILE_REQUEST_SAGA_TOPIC"), this.eventCaptor.capture());
    final var natsResponse = new String(this.eventCaptor.getValue());
    assertThat(natsResponse.contains(PEN_REQUEST_FOUND.toString())).isTrue();
  }

  private PenRequest getPenRequestEntityFromJsonString() {
    try {
      return new ObjectMapper().readValue(this.placeHolderPenReqJSON(), PenRequest.class);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected String placeHolderPenReqJSON() {
    return "{\"digitalID\":\"b1e0788a-7dab-4b92-af86-c678e411f1e3\",\"legalFirstName\":\"Chester\",\"legalMiddleNames\":\"Grestie\",\"legalLastName\":\"Baulk\",\"dob\":\"1952-10-31\",\"genderCode\":\"M\",\"email\":\"cbaulk0@bluehost.com\",\"emailVerified\":\"N\",\"currentSchool\":\"Xanthoparmelia wyomingica (Gyel.) Hale\",\"pen\":\"127054021\"}";
  }
}
