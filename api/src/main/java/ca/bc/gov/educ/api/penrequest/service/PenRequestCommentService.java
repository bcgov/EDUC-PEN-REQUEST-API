package ca.bc.gov.educ.api.penrequest.service;

import ca.bc.gov.educ.api.penrequest.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.penrequest.model.PenRequestCommentsEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.props.ApplicationProperties;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestCommentRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class PenRequestCommentService {

  @Getter(AccessLevel.PRIVATE)
  private final PenRequestRepository penRequestRepository;

  @Getter(AccessLevel.PRIVATE)
  private final PenRequestCommentRepository penRequestCommentRepository;

  PenRequestCommentService(@Autowired final PenRequestRepository penRequestRepository, @Autowired final PenRequestCommentRepository penRequestCommentRepository) {
    this.penRequestRepository = penRequestRepository;
    this.penRequestCommentRepository = penRequestCommentRepository;
  }

  public Iterable<PenRequestCommentsEntity> retrieveComments(UUID penRetrievalRequestId) {
    final Optional<PenRequestEntity> entity = getPenRequestRepository().findById(penRetrievalRequestId);
    if (entity.isPresent()) {
      return entity.get().getPenRequestComments();
    }
    throw new EntityNotFoundException(PenRequestEntity.class, "PenRequest", penRetrievalRequestId.toString());
  }

  /**
   * Need to find the entity first as it is the parent entity and system is trying to persist the child entity so need to attach it to the parent entity otherwise hibernate will throw detach entity exception.
   *
   * @param penRetrievalRequestId    The ID of the Pen Retrieval Request.
   * @param penRequestCommentsEntity The individual comment by staff or student.
   * @return PenRequestCommentsEntity, the saved instance.
   */
  public PenRequestCommentsEntity save(UUID penRetrievalRequestId, PenRequestCommentsEntity penRequestCommentsEntity) {
    val result = getPenRequestRepository().findById(penRetrievalRequestId);
    if (result.isPresent()) {
      penRequestCommentsEntity.setPenRequestEntity(result.get());
      penRequestCommentsEntity.setCreateUser(ApplicationProperties.CLIENT_ID);
      penRequestCommentsEntity.setCreateDate(new Date());
      penRequestCommentsEntity.setUpdateUser(ApplicationProperties.CLIENT_ID);
      penRequestCommentsEntity.setUpdateDate(new Date());
      return getPenRequestCommentRepository().save(penRequestCommentsEntity);
    }
    throw new EntityNotFoundException(PenRequestEntity.class, "PenRequest", penRetrievalRequestId.toString());
  }
}
