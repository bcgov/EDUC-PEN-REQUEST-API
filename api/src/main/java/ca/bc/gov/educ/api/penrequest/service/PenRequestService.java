package ca.bc.gov.educ.api.penrequest.service;

import ca.bc.gov.educ.api.penrequest.constants.PenRequestStatusCode;
import ca.bc.gov.educ.api.penrequest.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.penrequest.model.GenderCodeEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestStatusCodeEntity;
import ca.bc.gov.educ.api.penrequest.repository.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Service
public class PenRequestService {

  @Getter(AccessLevel.PRIVATE)
  private final PenRequestRepository penRequestRepository;
  @Getter(AccessLevel.PRIVATE)
  private final PenRequestCommentRepository penRequestCommentRepository;
  @Getter(AccessLevel.PRIVATE)
  private final DocumentRepository documentRepository;

  @Getter(AccessLevel.PRIVATE)
  private final PenRequestStatusCodeTableRepository penRequestStatusCodeTableRepo;

  @Getter(AccessLevel.PRIVATE)
  private final GenderCodeTableRepository genderCodeTableRepo;

  @Autowired
  public PenRequestService(final PenRequestRepository penRequestRepository, PenRequestCommentRepository penRequestCommentRepository, DocumentRepository documentRepository, final PenRequestStatusCodeTableRepository penRequestStatusCodeTableRepo, final GenderCodeTableRepository genderCodeTableRepo) {
    this.penRequestRepository = penRequestRepository;
    this.penRequestCommentRepository = penRequestCommentRepository;
    this.documentRepository = documentRepository;
    this.penRequestStatusCodeTableRepo = penRequestStatusCodeTableRepo;
    this.genderCodeTableRepo = genderCodeTableRepo;
  }

  public PenRequestEntity retrievePenRequest(UUID id) {
    Optional<PenRequestEntity> res = getPenRequestRepository().findById(id);
    if (res.isPresent()) {
      return res.get();
    } else {
      throw new EntityNotFoundException(PenRequestEntity.class, "penRequestId", id.toString());
    }
  }

  /**
   * set the status to DRAFT in the initial submit of pen request.
   *
   * @param penRequest the pen request object to be persisted in the DB.
   * @return the persisted entity.
   */
  public PenRequestEntity createPenRequest(PenRequestEntity penRequest) {
    penRequest.setPenRequestStatusCode(PenRequestStatusCode.DRAFT.toString());
    penRequest.setStatusUpdateDate(LocalDateTime.now());
    return getPenRequestRepository().save(penRequest);
  }


  public Iterable<PenRequestStatusCodeEntity> getPenRequestStatusCodesList() {
    return getPenRequestStatusCodeTableRepo().findAll();
  }

  public List<PenRequestEntity> findPenRequests(UUID digitalID, String statusCode, String pen) {
    return getPenRequestRepository().findPenRequests(digitalID, statusCode, pen);
  }

  /**
   * Returns the full list of access channel codes
   *
   * @return {@link List<GenderCodeEntity>}
   */
  @Cacheable("genderCodes")
  public List<GenderCodeEntity> getGenderCodesList() {
    return genderCodeTableRepo.findAll();
  }

  private Map<String, GenderCodeEntity> loadGenderCodes() {
    return getGenderCodesList().stream().collect(Collectors.toMap(GenderCodeEntity::getGenderCode, genderCodeEntity -> genderCodeEntity));
  }

  public Optional<GenderCodeEntity> findGenderCode(String genderCode) {
    return Optional.ofNullable(loadGenderCodes().get(genderCode));
  }

  /**
   * This method has to add some DB fields values to the incoming to keep track of audit columns and parent child relationship.
   *
   * @param penRequest the object which needs to be updated.
   * @return updated object.
   */
  public PenRequestEntity updatePenRequest(PenRequestEntity penRequest) {
    Optional<PenRequestEntity> curPenRequest = getPenRequestRepository().findById(penRequest.getPenRequestID());

    if (curPenRequest.isPresent()) {
      PenRequestEntity newPenRequest = curPenRequest.get();
      penRequest.setPenRequestComments(newPenRequest.getPenRequestComments());
      BeanUtils.copyProperties(penRequest, newPenRequest);
      newPenRequest = penRequestRepository.save(newPenRequest);
      return newPenRequest;
    } else {
      throw new EntityNotFoundException(PenRequestEntity.class, "PenRequest", penRequest.getPenRequestID().toString());
    }
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void deleteAll() {
    List<PenRequestEntity> penRequests = getPenRequestRepository().findAll();
    for (val entity : penRequests) {
      deleteAssociatedDocumentsAndComments(entity);
    }
    getPenRequestRepository().deleteAll();
  }

  private void deleteAssociatedDocumentsAndComments(PenRequestEntity entity) {
    val documents = getDocumentRepository().findByPenRequestPenRequestID(entity.getPenRequestID());
    if (documents != null && !documents.isEmpty()) {
      getDocumentRepository().deleteAll(documents);
    }
    if (entity.getPenRequestComments() != null && !entity.getPenRequestComments().isEmpty()) {
      getPenRequestCommentRepository().deleteAll(entity.getPenRequestComments());
    }
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void deleteById(UUID id) {
    val entity = getPenRequestRepository().findById(id);
    if (entity.isPresent()) {
      deleteAssociatedDocumentsAndComments(entity.get());
      getPenRequestRepository().delete(entity.get());
    } else {
      throw new EntityNotFoundException(PenRequestEntity.class, "PenRequestID", id.toString());
    }
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  public CompletableFuture<Page<PenRequestEntity>> findAll(Specification<PenRequestEntity> penRequestSpecs, final Integer pageNumber, final Integer pageSize, final List<Sort.Order> sorts) {
    Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sorts));
    try {
      val result = getPenRequestRepository().findAll(penRequestSpecs, paging);
      return CompletableFuture.completedFuture(result);
    } catch (final Exception ex) {
      throw new CompletionException(ex);
    }
  }
}
