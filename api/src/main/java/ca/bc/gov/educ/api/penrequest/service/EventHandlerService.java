package ca.bc.gov.educ.api.penrequest.service;

import ca.bc.gov.educ.api.penrequest.constants.EventOutcome;
import ca.bc.gov.educ.api.penrequest.mappers.PenRequestCommentsMapper;
import ca.bc.gov.educ.api.penrequest.mappers.PenRequestEntityMapper;
import ca.bc.gov.educ.api.penrequest.model.PenRequestCommentsEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEvent;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestCommentRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestEventRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.struct.Event;
import ca.bc.gov.educ.api.penrequest.struct.PenRequest;
import ca.bc.gov.educ.api.penrequest.struct.PenRequestComments;
import ca.bc.gov.educ.api.penrequest.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static ca.bc.gov.educ.api.penrequest.constants.EventStatus.DB_COMMITTED;
import static ca.bc.gov.educ.api.penrequest.constants.EventStatus.MESSAGE_PUBLISHED;
import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
public class EventHandlerService {
  public static final String NO_RECORD_SAGA_ID_EVENT_TYPE = "no record found for the saga id and event type combination, processing";
  public static final String RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE = "record found for the saga id and event type combination, might be a duplicate or replay," +
          " just updating the db status so that it will be polled and sent back again.";
  public static final String PAYLOAD_LOG = "payload is :: {}";
  public static final String EVENT_PAYLOAD = "event is :: {}";
  @Getter(PRIVATE)
  private final PenRequestRepository penRequestRepository;
  private static final PenRequestEntityMapper mapper = PenRequestEntityMapper.mapper;
  private static final PenRequestCommentsMapper prcMapper = PenRequestCommentsMapper.mapper;
  @Getter(PRIVATE)
  private final PenRequestEventRepository penRequestEventRepository;
  @Getter(PRIVATE)
  private final PenRequestCommentRepository penRequestCommentRepository;

  @Autowired
  public EventHandlerService(final PenRequestRepository penRequestRepository, final PenRequestEventRepository penRequestEventRepository, PenRequestCommentRepository penRequestCommentRepository) {
    this.penRequestRepository = penRequestRepository;
    this.penRequestEventRepository = penRequestEventRepository;
    this.penRequestCommentRepository = penRequestCommentRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleEvent(Event event) {
    try {
      switch (event.getEventType()) {
        case PEN_REQUEST_EVENT_OUTBOX_PROCESSED:
          log.info("received outbox processed event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          handlePenReqEventOutboxProcessed(event.getEventPayload());
          break;
        case UPDATE_PEN_REQUEST:
          log.info("received UPDATE_PEN_REQUEST event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          handleUpdatePenRequest(event);
          break;
        case GET_PEN_REQUEST:
          log.info("received GET_PEN_REQUEST event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          handleGetPenRequest(event);
          break;
        case ADD_PEN_REQUEST_COMMENT:
          log.info("received ADD_PEN_REQUEST_COMMENT event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          handleAddPenRequestComment(event);
          break;
        default:
          log.info("silently ignoring other events.");
          break;
      }
    } catch (final Exception e) {
      log.error("Exception", e);
    }
  }

  private void handleAddPenRequestComment(Event event) throws JsonProcessingException {
    val penRequestEventOptional = getPenRequestEventRepository().findBySagaIdAndEventType(event.getSagaId(), event.getEventType().toString());
    PenRequestEvent penRequestEvent;
    if (penRequestEventOptional.isEmpty()) {
      log.info(NO_RECORD_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_PAYLOAD, event);
      PenRequestCommentsEntity entity = prcMapper.toModel(JsonUtil.getJsonObjectFromString(PenRequestComments.class, event.getEventPayload()));
      val penReqComment = getPenRequestCommentRepository().findByCommentContentAndCommentTimestamp(entity.getCommentContent(), entity.getCommentTimestamp());
      if (penReqComment.isPresent()) {
        event.setEventOutcome(EventOutcome.PEN_REQUEST_COMMENT_ALREADY_EXIST);
        event.setEventPayload(JsonUtil.getJsonStringFromObject(prcMapper.toStructure(penReqComment.get())));
      } else {
        val result = getPenRequestRepository().findById(entity.getPenRetrievalRequestID());
        if (result.isPresent()) {
          entity.setPenRequestEntity(result.get());
          entity.setCreateDate(LocalDateTime.now());
          entity.setUpdateDate(LocalDateTime.now());
          getPenRequestCommentRepository().save(entity);
          event.setEventOutcome(EventOutcome.PEN_REQUEST_COMMENT_ADDED);
          event.setEventPayload(JsonUtil.getJsonStringFromObject(prcMapper.toStructure(entity)));
        }
      }
      penRequestEvent = createPenRequestEvent(event);
    } else {
      log.info(RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_PAYLOAD, event);
      penRequestEvent = penRequestEventOptional.get();
      penRequestEvent.setEventStatus(DB_COMMITTED.toString());
    }
    getPenRequestEventRepository().save(penRequestEvent);
  }

  private void handleGetPenRequest(Event event) throws JsonProcessingException {
    val penRequestEventOptional = getPenRequestEventRepository().findBySagaIdAndEventType(event.getSagaId(), event.getEventType().toString());
    PenRequestEvent penRequestEvent;
    if (penRequestEventOptional.isEmpty()) {
      log.info(NO_RECORD_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_PAYLOAD, event);
      val optionalPenRequestEntity = getPenRequestRepository().findById(UUID.fromString(event.getEventPayload())); // expect the payload contains the pen request id.
      if (optionalPenRequestEntity.isPresent()) {
        val attachedEntity = optionalPenRequestEntity.get();
        event.setEventPayload(JsonUtil.getJsonStringFromObject(mapper.toStructure(attachedEntity)));// need to convert to structure MANDATORY otherwise jackson will break.
        event.setEventOutcome(EventOutcome.PEN_REQUEST_FOUND);
      } else {
        event.setEventOutcome(EventOutcome.PEN_REQUEST_NOT_FOUND);
      }
      penRequestEvent = createPenRequestEvent(event);
    } else {
      log.info(RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_PAYLOAD, event);
      penRequestEvent = penRequestEventOptional.get();
      penRequestEvent.setEventStatus(DB_COMMITTED.toString());
    }
    getPenRequestEventRepository().save(penRequestEvent);
  }

  private void handleUpdatePenRequest(Event event) throws JsonProcessingException {
    val penRequestEventOptional = getPenRequestEventRepository().findBySagaIdAndEventType(event.getSagaId(), event.getEventType().toString());
    PenRequestEvent penRequestEvent;
    if (penRequestEventOptional.isEmpty()) {
      log.info(NO_RECORD_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_PAYLOAD, event);
      PenRequestEntity entity = mapper.toModel(JsonUtil.getJsonObjectFromString(PenRequest.class, event.getEventPayload()));
      val optionalPenRequestEntity = getPenRequestRepository().findById(entity.getPenRequestID());
      if (optionalPenRequestEntity.isPresent()) {
        val attachedEntity = optionalPenRequestEntity.get();
        entity.setPenRequestComments(attachedEntity.getPenRequestComments()); // need to add this , otherwise child entities will be out of reference.
        BeanUtils.copyProperties(entity, attachedEntity);
        attachedEntity.setUpdateDate(LocalDateTime.now());
        getPenRequestRepository().save(attachedEntity);
        event.setEventPayload(JsonUtil.getJsonStringFromObject(mapper.toStructure(attachedEntity)));// need to convert to structure MANDATORY otherwise jackson will break.
        event.setEventOutcome(EventOutcome.PEN_REQUEST_UPDATED);
      } else {
        event.setEventOutcome(EventOutcome.PEN_REQUEST_NOT_FOUND);
      }
      penRequestEvent = createPenRequestEvent(event);
    } else {
      log.info(RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_PAYLOAD, event);
      penRequestEvent = penRequestEventOptional.get();
      penRequestEvent.setEventStatus(DB_COMMITTED.toString());
    }
    getPenRequestEventRepository().save(penRequestEvent);
  }

  private void handlePenReqEventOutboxProcessed(String penRequestEventID) {
    val penRequestEventOptional = getPenRequestEventRepository().findById(UUID.fromString(penRequestEventID));
    if (penRequestEventOptional.isPresent()) {
      val digIdEvent = penRequestEventOptional.get();
      digIdEvent.setEventStatus(MESSAGE_PUBLISHED.toString());
      getPenRequestEventRepository().save(digIdEvent);
    }
  }


  private PenRequestEvent createPenRequestEvent(Event event) {
    return PenRequestEvent.builder()
            .createDate(LocalDateTime.now())
            .updateDate(LocalDateTime.now())
            .createUser(event.getEventType().toString()) //need to discuss what to put here.
            .updateUser(event.getEventType().toString())
            .eventPayload(event.getEventPayload())
            .eventType(event.getEventType().toString())
            .sagaId(event.getSagaId())
            .eventStatus(DB_COMMITTED.toString())
            .eventOutcome(event.getEventOutcome().toString())
            .replyChannel(event.getReplyTo())
            .build();
  }
}
