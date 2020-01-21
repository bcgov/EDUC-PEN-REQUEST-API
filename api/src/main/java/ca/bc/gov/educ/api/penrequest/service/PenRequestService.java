package ca.bc.gov.educ.api.penrequest.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.bc.gov.educ.api.penrequest.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.penrequest.exception.InvalidParameterException;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestStatusCodeEntity;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestStatusCodeTableRepository;

@Service
public class PenRequestService {

	private final String DIGITAL_ID_USER = "DIGITAL_ID_API";
	
    @Autowired
    private PenRequestRepository penRequestRepository;
    
    @Autowired
    private PenRequestStatusCodeTableRepository penRequestStatusCodeTableRepo;

    public PenRequestEntity retrievePenRequest(UUID id) {
        Optional<PenRequestEntity> res = penRequestRepository.findById(id);
        if(res.isPresent()){
            return res.get();
        } else {
            throw new EntityNotFoundException(PenRequestEntity.class, "penRequestId", id.toString());
        }
    }

    public PenRequestEntity createPenRequest(PenRequestEntity penRequest) {
        validateParameters(penRequest);
        
        if(penRequest.getPenRequestID()!=null){
            throw new InvalidParameterException("penRequest");
        }
        penRequest.setPenRequestStatusCode("INITREV");
        penRequest.setCreateUser(DIGITAL_ID_USER);
        penRequest.setCreateDate(new Date());
	penRequest.setUpdateUser(DIGITAL_ID_USER);
        penRequest.setUpdateDate(new Date());

        return penRequestRepository.save(penRequest);
    }
    
    public List<PenRequestStatusCodeEntity> getPenRequestStatusCodesList() {
        List<PenRequestStatusCodeEntity> result =  penRequestStatusCodeTableRepo.findAll();
        if(result != null && !result.isEmpty()) {
            return result;
        } else {
            throw new EntityNotFoundException(PenRequestStatusCodeEntity.class);
        }
    }

    public Iterable<PenRequestEntity> retrieveAllRequests() {
        if(penRequestRepository.findAll() == null) {
            throw new EntityNotFoundException(PenRequestEntity.class, "penRequestId", "any");
        }
        return penRequestRepository.findAll();
    }

    public PenRequestEntity updatePenRequest(PenRequestEntity penRequest) {
        
        validateParameters(penRequest);
        
        Optional<PenRequestEntity> curPenRequest = penRequestRepository.findById(penRequest.getPenRequestID());

        if(curPenRequest.isPresent())
        {
            PenRequestEntity newPenRequest = curPenRequest.get();
            newPenRequest.setPenRequestStatusCode(penRequest.getPenRequestStatusCode());
            newPenRequest.setLegalFirstName(penRequest.getLegalFirstName());
            newPenRequest.setLegalLastName(penRequest.getLegalLastName());
            newPenRequest.setDob(penRequest.getDob());
            newPenRequest.setGenderCode(penRequest.getGenderCode());
            newPenRequest.setDataSourceCode(penRequest.getDataSourceCode());
            newPenRequest.setUsualFirstName(penRequest.getUsualFirstName());
            newPenRequest.setUsualMiddleName(penRequest.getUsualMiddleName());
            newPenRequest.setUsualLastName(penRequest.getUsualLastName());
            newPenRequest.setEmail(penRequest.getEmail());
            newPenRequest.setMaidenName(penRequest.getMaidenName());
            newPenRequest.setPastNames(penRequest.getPastNames());
            newPenRequest.setLastBCSchool(penRequest.getLastBCSchool());
            newPenRequest.setLastBCSchoolStudentNumber(penRequest.getLastBCSchoolStudentNumber());
            newPenRequest.setCurrentSchool(penRequest.getCurrentSchool());
            newPenRequest.setFailureReason(penRequest.getFailureReason);
            newPenRequest.setReviewer(penRequest.getReviewer());
            newPenRequest.setUpdateUser(DIGITAL_ID_USER);
            newPenRequest.setUpdateDate(new Date());
            newPenRequest = penRequestRepository.save(newPenRequest);

            return newPenRequest;
        } else {
            throw new EntityNotFoundException(PenRequestEntity.class, "PenRequest", penRequest.getPenRequestID().toString());
        }
    }

    private void validateParameters(PenRequestEntity penRequestEntity) {
        if(penRequestEntity.getCreateDate()!=null)
            throw new InvalidParameterException("createDate");
        if(penRequestEntity.getUpdateDate()!=null)
            throw new InvalidParameterException("updateDate");
    }
}
