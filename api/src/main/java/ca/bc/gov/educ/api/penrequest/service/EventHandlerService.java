package ca.bc.gov.educ.api.penrequest.service;

import ca.bc.gov.educ.api.penrequest.constants.EventOutcome;
import ca.bc.gov.educ.api.penrequest.mappers.PenRequestEntityMapper;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEvent;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestEventRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.struct.Event;
import ca.bc.gov.educ.api.penrequest.struct.PenRequest;
import ca.bc.gov.educ.api.penrequest.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import static ca.bc.gov.educ.api.penrequest.constants.EventStatus.DB_COMMITTED;
import static ca.bc.gov.educ.api.penrequest.constants.EventStatus.MESSAGE_PUBLISHED;
import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
public class EventHandlerService {
public EventHandlerService(){
  this(null,null);
}
  public static final String NO_RECORD_SAGA_ID_EVENT_TYPE = "no record found for the saga id and event type combination, processing. {}";
  public static final String RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE = "record found for the saga id and event type combination, might be a duplicate or replay," +
          " just updating the db status so that it will be polled and sent back again. {}";
  @Getter(PRIVATE)
  private final PenRequestRepository penRequestRepository;
  private static final PenRequestEntityMapper mapper = PenRequestEntityMapper.mapper;
  @Getter(PRIVATE)
  private final PenRequestEventRepository penRequestEventRepository;

  @Autowired
  public EventHandlerService(final PenRequestRepository penRequestRepository, final PenRequestEventRepository penRequestEventRepository) {
    this.penRequestRepository = penRequestRepository;
    this.penRequestEventRepository = penRequestEventRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleEvent(Event event) {
    try {
      switch (event.getEventType()) {
        case PEN_REQUEST_EVENT_OUTBOX_PROCESSED:
          log.info("received outbox processed event :: " + event.getEventPayload());
          handlePenReqEventOutboxProcessed(event.getEventPayload());
          break;
        case UPDATE_PEN_REQUEST:
          log.info("received UPDATE_PEN_REQUEST event :: " + event.getEventPayload());
          handleUpdatePenRequest(event);
          break;
        default:
          log.info("silently ignoring other events.");
          break;
      }
    } catch (final Exception e) {
      log.error("Exception", e);
    }
  }

  private void handleUpdatePenRequest(Event event) throws JsonProcessingException, IllegalAccessException {
    val penRequestEventOptional = getPenRequestEventRepository().findBySagaIdAndEventType(event.getSagaId(), event.getEventType().toString());
    PenRequestEvent penRequestEvent;
    if (!penRequestEventOptional.isPresent()) {
      log.info(NO_RECORD_SAGA_ID_EVENT_TYPE, event);
      PenRequestEntity entity = mapper.toModel(JsonUtil.getJsonObjectFromString(PenRequest.class, event.getEventPayload()));
      val optionalPenRequestEntity = getPenRequestRepository().findById(entity.getPenRequestID());
      if (optionalPenRequestEntity.isPresent()) {
        val attachedEntity = optionalPenRequestEntity.get();
        updateValuesInAttachedEntity(entity, attachedEntity);
        getPenRequestRepository().save(attachedEntity);
        event.setEventPayload(JsonUtil.getJsonStringFromObject(attachedEntity));
        event.setEventOutcome(EventOutcome.PEN_REQUEST_UPDATED);
      } else {
        event.setEventOutcome(EventOutcome.PEN_REQUEST_NOT_FOUND);
      }
      penRequestEvent = createPenRequestEvent(event);
    } else {
      log.info(RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE, event);
      penRequestEvent = penRequestEventOptional.get();
      penRequestEvent.setEventStatus(DB_COMMITTED.toString());
    }

    getPenRequestEventRepository().save(penRequestEvent);
  }

  /**
   * this method will update the fields with not null values from entity to attached entity.
   */
  private void updateValuesInAttachedEntity(PenRequestEntity entity, PenRequestEntity attachedEntity) throws IllegalAccessException {
    for (Field field : attachedEntity.getClass().getDeclaredFields()) {
      if (!field.isAccessible()) {
        field.setAccessible(true);
        Object value = field.get(entity);
        if (value != null) {
          field.set(attachedEntity, value);
        }
      }
    }
    attachedEntity.setUpdateDate(LocalDateTime.now());
  }

  private void handlePenReqEventOutboxProcessed(String digitalIdEventId) {
    val digitalIdEvent = getPenRequestEventRepository().findById(UUID.fromString(digitalIdEventId));
    if (digitalIdEvent.isPresent()) {
      val digIdEvent = digitalIdEvent.get();
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
