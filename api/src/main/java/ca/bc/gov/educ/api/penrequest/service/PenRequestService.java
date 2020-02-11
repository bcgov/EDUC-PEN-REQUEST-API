package ca.bc.gov.educ.api.penrequest.service;

import ca.bc.gov.educ.api.penrequest.constants.PenRequestStatusCode;
import ca.bc.gov.educ.api.penrequest.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestStatusCodeEntity;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestStatusCodeTableRepository;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PenRequestService {

  @Getter(AccessLevel.PRIVATE)
  private final PenRequestRepository penRequestRepository;

  @Getter(AccessLevel.PRIVATE)
  private final PenRequestStatusCodeTableRepository penRequestStatusCodeTableRepo;

  PenRequestService(@Autowired final PenRequestRepository penRequestRepository, @Autowired final PenRequestStatusCodeTableRepository penRequestStatusCodeTableRepo) {
    this.penRequestRepository = penRequestRepository;
    this.penRequestStatusCodeTableRepo = penRequestStatusCodeTableRepo;
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
    penRequest.setStatusUpdateDate(new Date());
    return getPenRequestRepository().save(penRequest);
  }


  public Iterable<PenRequestStatusCodeEntity> getPenRequestStatusCodesList() {
    return getPenRequestStatusCodeTableRepo().findAll();
  }

  public List<PenRequestEntity> findPenRequests(UUID digitalID, String statusCode) {
    return getPenRequestRepository().findPenRequests(digitalID, statusCode);
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
      Date createDate = newPenRequest.getCreateDate();
      String createUser = newPenRequest.getCreateUser();
      penRequest.setPenRequestComments(newPenRequest.getPenRequestComments());
      BeanUtils.copyProperties(penRequest, newPenRequest);
      newPenRequest.setCreateDate(createDate);
      newPenRequest.setCreateUser(createUser);
      newPenRequest = penRequestRepository.save(newPenRequest);
      return newPenRequest;
    } else {
      throw new EntityNotFoundException(PenRequestEntity.class, "PenRequest", penRequest.getPenRequestID().toString());
    }
  }


}
