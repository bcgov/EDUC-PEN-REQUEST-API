package ca.bc.gov.educ.api.penrequest.service;

import ca.bc.gov.educ.api.penrequest.mappers.PenRequestCommentsMapper;
import ca.bc.gov.educ.api.penrequest.mappers.PenRequestEntityMapper;
import ca.bc.gov.educ.api.penrequest.model.PenRequestCommentsEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEvent;
import ca.bc.gov.educ.api.penrequest.repository.DocumentRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestCommentRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestEventRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.struct.Event;
import ca.bc.gov.educ.api.penrequest.struct.PenRequest;
import ca.bc.gov.educ.api.penrequest.struct.PenRequestComments;
import ca.bc.gov.educ.api.penrequest.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static ca.bc.gov.educ.api.penrequest.constants.EventOutcome.*;
import static ca.bc.gov.educ.api.penrequest.constants.EventStatus.DB_COMMITTED;
import static ca.bc.gov.educ.api.penrequest.constants.EventStatus.MESSAGE_PUBLISHED;
import static ca.bc.gov.educ.api.penrequest.constants.EventType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.MockitoAnnotations.openMocks;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class EventHandlerServiceTest {

  public static final String STUDENT_PROFILE_SAGA_API_TOPIC = "STUDENT_PROFILE_SAGA_API_TOPIC";
  @Autowired
  private PenRequestRepository penRequestRepository;
  @Autowired
  private PenRequestEventRepository penRequestEventRepository;
  @Autowired
  private PenRequestCommentRepository penRequestCommentRepository;
  @Autowired
  private DocumentRepository documentRepository;

  @Autowired
  private EventHandlerService eventHandlerServiceUnderTest;
  private static final PenRequestEntityMapper mapper = PenRequestEntityMapper.mapper;
  private static final PenRequestCommentsMapper prcMapper = PenRequestCommentsMapper.mapper;

  @Before
  public void setUp() {
    openMocks(this);
  }

  @After
  public void tearDown() {
    documentRepository.deleteAll();
    penRequestCommentRepository.deleteAll();
    penRequestRepository.deleteAll();
    penRequestEventRepository.deleteAll();
  }


  @Test
  public void testHandleEvent_givenEventTypeGET__PENREQUEST__whenNoPenRequestExist_shouldHaveEventOutcomePEN__REQUEST__NOT__FOUND() throws JsonProcessingException {
    var sagaId = UUID.randomUUID();
    final Event event = Event.builder().eventType(GET_PEN_REQUEST).sagaId(sagaId).replyTo(STUDENT_PROFILE_SAGA_API_TOPIC).eventPayload(UUID.randomUUID().toString()).build();
    eventHandlerServiceUnderTest.handleGetPenRequest(event);
    var penReqEventUpdated = penRequestEventRepository.findBySagaIdAndEventType(sagaId, GET_PEN_REQUEST.toString());
    assertThat(penReqEventUpdated).isPresent();
    assertThat(penReqEventUpdated.get().getEventStatus()).isEqualTo(MESSAGE_PUBLISHED.toString());
    assertThat(penReqEventUpdated.get().getEventOutcome()).isEqualTo(PEN_REQUEST_NOT_FOUND.toString());
  }

  @Test
  public void testHandleEvent_givenEventTypeGET__PENREQUEST__whenPenRequestExist_shouldHaveEventOutcomePEN__REQUEST__FOUND() throws JsonProcessingException {
    PenRequestEntity entity = penRequestRepository.save(mapper.toModel(getPenRequestEntityFromJsonString()));
    var sagaId = UUID.randomUUID();
    final Event event = Event.builder().eventType(GET_PEN_REQUEST).sagaId(sagaId).replyTo(STUDENT_PROFILE_SAGA_API_TOPIC).eventPayload(entity.getPenRequestID().toString()).build();
    eventHandlerServiceUnderTest.handleGetPenRequest(event);
    var penReqEventUpdated = penRequestEventRepository.findBySagaIdAndEventType(sagaId, GET_PEN_REQUEST.toString());
    assertThat(penReqEventUpdated).isPresent();
    assertThat(penReqEventUpdated.get().getEventStatus()).isEqualTo(MESSAGE_PUBLISHED.toString());
    assertThat(penReqEventUpdated.get().getEventOutcome()).isEqualTo(PEN_REQUEST_FOUND.toString());
  }
  @Test
  public void testHandleEvent_givenEventTypeGET__PENREQUEST__whenPenRequestExistAndDuplicateSagaMessage_shouldHaveEventOutcomePEN__REQUEST__FOUND() throws JsonProcessingException {
    PenRequestEntity entity = penRequestRepository.save(mapper.toModel(getPenRequestEntityFromJsonString()));

    var sagaId = UUID.randomUUID();
    var penRequestEvent = PenRequestEvent.builder().sagaId(sagaId).replyChannel(STUDENT_PROFILE_SAGA_API_TOPIC).eventType(GET_PEN_REQUEST.toString()).eventOutcome(PEN_REQUEST_FOUND.toString()).eventStatus(MESSAGE_PUBLISHED.toString()).createDate(LocalDateTime.now()).createUser("TEST").build();
    penRequestEventRepository.save(penRequestEvent);

    final Event event = Event.builder().eventType(GET_PEN_REQUEST).sagaId(sagaId).replyTo(STUDENT_PROFILE_SAGA_API_TOPIC).eventPayload(entity.getPenRequestID().toString()).build();
    eventHandlerServiceUnderTest.handleGetPenRequest(event);
    var penReqEventUpdated = penRequestEventRepository.findBySagaIdAndEventType(sagaId, GET_PEN_REQUEST.toString());
    assertThat(penReqEventUpdated).isPresent();
    assertThat(penReqEventUpdated.get().getEventStatus()).isEqualTo(MESSAGE_PUBLISHED.toString());
    assertThat(penReqEventUpdated.get().getEventOutcome()).isEqualTo(PEN_REQUEST_FOUND.toString());
  }

  @Test
  public void testHandleEvent_givenEventTypeUPDATE__PENREQUEST__whenPenRequestDoNotExist_shouldHaveEventOutcomePEN__REQUEST__NOT__FOUND() throws JsonProcessingException {
    PenRequest entity = getPenRequestEntityFromJsonString();
    entity.setPenRequestID(UUID.randomUUID().toString());
    var sagaId = UUID.randomUUID();
    final Event event = Event.builder().eventType(UPDATE_PEN_REQUEST).sagaId(sagaId).replyTo(STUDENT_PROFILE_SAGA_API_TOPIC).eventPayload(JsonUtil.getJsonStringFromObject(entity)).build();
    eventHandlerServiceUnderTest.handleUpdatePenRequest(event);
    var penReqEventUpdated = penRequestEventRepository.findBySagaIdAndEventType(sagaId, UPDATE_PEN_REQUEST.toString());
    assertThat(penReqEventUpdated).isPresent();
    assertThat(penReqEventUpdated.get().getEventStatus()).isEqualTo(MESSAGE_PUBLISHED.toString());
    assertThat(penReqEventUpdated.get().getEventOutcome()).isEqualTo(PEN_REQUEST_NOT_FOUND.toString());
  }

  @Test
  public void testHandleEvent_givenEventTypeUPDATE__PENREQUEST__whenPenRequestExist_shouldHaveEventOutcomePEN__REQUEST__FOUND() throws JsonProcessingException {
    PenRequestEntity entity = penRequestRepository.save(mapper.toModel(getPenRequestEntityFromJsonString()));
    entity.setCompleteComment("Manual");
    var sagaId = UUID.randomUUID();
    final Event event = Event.builder().eventType(UPDATE_PEN_REQUEST).sagaId(sagaId).replyTo(STUDENT_PROFILE_SAGA_API_TOPIC).eventPayload(JsonUtil.getJsonStringFromObject(mapper.toStructure(entity))).build();
    eventHandlerServiceUnderTest.handleUpdatePenRequest(event);
    var penReqEventUpdated = penRequestEventRepository.findBySagaIdAndEventType(sagaId, UPDATE_PEN_REQUEST.toString());
    assertThat(penReqEventUpdated).isPresent();
    assertThat(penReqEventUpdated.get().getEventStatus()).isEqualTo(MESSAGE_PUBLISHED.toString());
    assertThat(penReqEventUpdated.get().getEventOutcome()).isEqualTo(PEN_REQUEST_UPDATED.toString());
  }

  @Test
  public void testHandleEvent_givenEventTypeUPDATE__PENREQUEST__whenPenRequestExistAndDuplicateSagaMessage_shouldHaveEventOutcomePEN__REQUEST__FOUND() throws JsonProcessingException {
    PenRequestEntity entity = penRequestRepository.save(mapper.toModel(getPenRequestEntityFromJsonString()));

    var sagaId = UUID.randomUUID();
    var penRequestEvent = PenRequestEvent.builder().sagaId(sagaId).replyChannel(STUDENT_PROFILE_SAGA_API_TOPIC).eventType(UPDATE_PEN_REQUEST.toString()).eventOutcome(PEN_REQUEST_UPDATED.toString()).eventStatus(MESSAGE_PUBLISHED.toString()).createDate(LocalDateTime.now()).createUser("TEST").build();
    penRequestEventRepository.save(penRequestEvent);

    final Event event = Event.builder().eventType(UPDATE_PEN_REQUEST).sagaId(sagaId).replyTo(STUDENT_PROFILE_SAGA_API_TOPIC).eventPayload(entity.getPenRequestID().toString()).build();
    eventHandlerServiceUnderTest.handleUpdatePenRequest(event);
    var penReqEventUpdated = penRequestEventRepository.findBySagaIdAndEventType(sagaId, UPDATE_PEN_REQUEST.toString());
    assertThat(penReqEventUpdated).isPresent();
    assertThat(penReqEventUpdated.get().getEventStatus()).isEqualTo(MESSAGE_PUBLISHED.toString());
    assertThat(penReqEventUpdated.get().getEventOutcome()).isEqualTo(PEN_REQUEST_UPDATED.toString());
  }

  @Test
  public void testHandleEvent_givenEventTypeADD__PENREQUEST__COMMENT_whenPenRequestExist_shouldHaveEventOutcomePEN__REQUEST__COMMENT__ADDED() throws JsonProcessingException {
    PenRequestEntity entity = penRequestRepository.save(mapper.toModel(getPenRequestEntityFromJsonString()));
    PenRequestCommentsEntity penRequestCommentsEntity = new PenRequestCommentsEntity();
    penRequestCommentsEntity.setPenRetrievalRequestID(entity.getPenRequestID());
    penRequestCommentsEntity.setCommentContent("Please provide other ID..");
    penRequestCommentsEntity.setCommentTimestamp(LocalDateTime.now());
    penRequestCommentsEntity.setCreateUser("API");
    penRequestCommentsEntity.setUpdateUser("API");
    var sagaId = UUID.randomUUID();
    final Event event = Event.builder().eventType(ADD_PEN_REQUEST_COMMENT).sagaId(sagaId).replyTo(STUDENT_PROFILE_SAGA_API_TOPIC).eventPayload(JsonUtil.getJsonStringFromObject(prcMapper.toStructure(penRequestCommentsEntity))).build();
    eventHandlerServiceUnderTest.handleAddPenRequestComment(event);
    var penReqEventUpdated = penRequestEventRepository.findBySagaIdAndEventType(sagaId, ADD_PEN_REQUEST_COMMENT.toString());
    assertThat(penReqEventUpdated).isPresent();
    assertThat(penReqEventUpdated.get().getEventStatus()).isEqualTo(MESSAGE_PUBLISHED.toString());
    assertThat(penReqEventUpdated.get().getEventOutcome()).isEqualTo(PEN_REQUEST_COMMENT_ADDED.toString());
  }

  @Test
  public void testHandleEvent_givenEventTypeADD__PENREQUEST__COMMENT_whenPenRequestCommentExist_shouldHaveEventOutcomePEN__REQUEST__COMMENT__ALREADY__EXIST() throws JsonProcessingException {
    PenRequestEntity entity = penRequestRepository.save(mapper.toModel(getPenRequestEntityFromJsonString()));
    PenRequestCommentsEntity penRequestCommentsEntity = new PenRequestCommentsEntity();
    penRequestCommentsEntity.setPenRetrievalRequestID(entity.getPenRequestID());
    penRequestCommentsEntity.setCommentContent("Please provide other ID..");
    penRequestCommentsEntity.setCommentTimestamp(LocalDateTime.now());
    penRequestCommentsEntity.setCreateUser("API");
    penRequestCommentsEntity.setUpdateUser("API");
    var sagaId = UUID.randomUUID();
    final Event event = Event.builder().eventType(ADD_PEN_REQUEST_COMMENT).sagaId(sagaId).replyTo(STUDENT_PROFILE_SAGA_API_TOPIC).eventPayload(JsonUtil.getJsonStringFromObject(prcMapper.toStructure(penRequestCommentsEntity))).build();
    eventHandlerServiceUnderTest.handleAddPenRequestComment(event);
    sagaId = UUID.randomUUID();
    event.setSagaId(sagaId);
    eventHandlerServiceUnderTest.handleAddPenRequestComment(event);
    var penReqEventUpdated = penRequestEventRepository.findBySagaIdAndEventType(sagaId, ADD_PEN_REQUEST_COMMENT.toString());
    assertThat(penReqEventUpdated).isPresent();
    assertThat(penReqEventUpdated.get().getEventStatus()).isEqualTo(MESSAGE_PUBLISHED.toString());
    assertThat(penReqEventUpdated.get().getEventOutcome()).isEqualTo(PEN_REQUEST_COMMENT_ALREADY_EXIST.toString());
  }

  @Test
  public void testHandleEvent_givenEventTypeADD__PENREQUEST__COMMENT_whenDuplicateSagaMessage_shouldHaveEventOutcomePEN__REQUEST__COMMENT__ADDED() throws JsonProcessingException {
    PenRequestEntity entity = penRequestRepository.save(mapper.toModel(getPenRequestEntityFromJsonString()));
    PenRequestCommentsEntity penRequestCommentsEntity = new PenRequestCommentsEntity();
    penRequestCommentsEntity.setPenRetrievalRequestID(entity.getPenRequestID());
    penRequestCommentsEntity.setCommentContent("Please provide other ID..");
    penRequestCommentsEntity.setCommentTimestamp(LocalDateTime.now());
    penRequestCommentsEntity.setCreateUser("API");
    penRequestCommentsEntity.setUpdateUser("API");

    var sagaId = UUID.randomUUID();

    var penRequestEvent = PenRequestEvent.builder().sagaId(sagaId).replyChannel(STUDENT_PROFILE_SAGA_API_TOPIC).eventType(ADD_PEN_REQUEST_COMMENT.toString()).eventOutcome(PEN_REQUEST_COMMENT_ADDED.toString()).eventStatus(MESSAGE_PUBLISHED.toString()).createDate(LocalDateTime.now()).createUser("TEST").build();
    penRequestEventRepository.save(penRequestEvent);

    final Event event = Event.builder().eventType(ADD_PEN_REQUEST_COMMENT).sagaId(sagaId).replyTo(STUDENT_PROFILE_SAGA_API_TOPIC).eventPayload(JsonUtil.getJsonStringFromObject(prcMapper.toStructure(penRequestCommentsEntity))).build();
    eventHandlerServiceUnderTest.handleAddPenRequestComment(event);
    var penReqEventUpdated = penRequestEventRepository.findBySagaIdAndEventType(sagaId, ADD_PEN_REQUEST_COMMENT.toString());
    assertThat(penReqEventUpdated).isPresent();
    assertThat(penReqEventUpdated.get().getEventStatus()).isEqualTo(MESSAGE_PUBLISHED.toString()); // the db status is updated from  MESSAGE_PUBLISHED
    assertThat(penReqEventUpdated.get().getEventOutcome()).isEqualTo(PEN_REQUEST_COMMENT_ADDED.toString());
  }



  private PenRequest getPenRequestEntityFromJsonString() {
    try {
      return new ObjectMapper().readValue(placeHolderPenReqJSON(), PenRequest.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected String placeHolderPenReqJSON() {
    return "{\"digitalID\":\"b1e0788a-7dab-4b92-af86-c678e411f1e3\",\"legalFirstName\":\"Chester\",\"legalMiddleNames\":\"Grestie\",\"legalLastName\":\"Baulk\",\"dob\":\"1952-10-31\",\"genderCode\":\"M\",\"email\":\"cbaulk0@bluehost.com\",\"emailVerified\":\"N\",\"currentSchool\":\"Xanthoparmelia wyomingica (Gyel.) Hale\",\"pen\":\"127054021\"}";
  }
}
