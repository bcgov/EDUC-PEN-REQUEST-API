package ca.bc.gov.educ.api.penrequest.service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import ca.bc.gov.educ.api.penrequest.constants.PenRequestStatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.bc.gov.educ.api.penrequest.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.penrequest.exception.InvalidParameterException;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestStatusCodeEntity;
import ca.bc.gov.educ.api.penrequest.props.ApplicationProperties;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestStatusCodeTableRepository;

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

  public PenRequestEntity createPenRequest(PenRequestEntity penRequest) {
    if (penRequest.getPenRequestID() != null) {
      throw new InvalidParameterException("penRequest");
    }
    if (penRequest.getInitialSubmitDate() != null) {
      throw new InvalidParameterException("initialSubmitDate");
    }
    penRequest.setPenRequestStatusCode(PenRequestStatusCode.INITREV.toString());
    penRequest.setStatusUpdateDate(new Date());
    penRequest.setInitialSubmitDate(new Date());
    penRequest.setCreateUser(ApplicationProperties.CLIENT_ID);
    penRequest.setCreateDate(new Date());
    penRequest.setUpdateUser(ApplicationProperties.CLIENT_ID);
    penRequest.setUpdateDate(new Date());

    return penRequestRepository.save(penRequest);
  }

  public Iterable<PenRequestStatusCodeEntity> getPenRequestStatusCodesList() {
    return penRequestStatusCodeTableRepo.findAll();
  }

  public Iterable<PenRequestEntity> retrieveAllRequests() {
    return penRequestRepository.findAll();
  }

  public PenRequestEntity updatePenRequest(PenRequestEntity penRequest) {


    Optional<PenRequestEntity> curPenRequest = penRequestRepository.findById(penRequest.getPenRequestID());

    if (curPenRequest.isPresent()) {
      PenRequestEntity newPenRequest = curPenRequest.get();
      penRequest.setPenRequestComments(newPenRequest.getPenRequestComments());
      BeanUtils.copyProperties(penRequest, newPenRequest);
      newPenRequest.setUpdateUser(ApplicationProperties.CLIENT_ID);
      newPenRequest.setUpdateDate(new Date());
      newPenRequest = penRequestRepository.save(newPenRequest);
      return newPenRequest;
    } else {
      throw new EntityNotFoundException(PenRequestEntity.class, "PenRequest", penRequest.getPenRequestID().toString());
    }
  }

}
