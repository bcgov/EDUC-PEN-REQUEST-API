package ca.bc.gov.educ.api.penRequest.service;

import ca.bc.gov.educ.api.penRequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penRequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penRequest.props.ApplicationProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ca.bc.gov.educ.api.penRequest.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

@Service
public class PenRequestService {

    @Autowired
    private PenRequestRepository penRequestRepository;

    public PenRequestEntity retrievePenRequest(String id) throws Exception, EntityNotFoundException{
        try{
            Boolean res = penRequestRepository.existsById(id);
            if(!res){
                throw new EntityNotFoundException(PenRequestEntity.class);
            }

            return penRequestRepository.findById(id).orElse(null);
        } catch(Exception e){
            throw new Exception("Error while retrieving PenRequest", e);
        }
    }

    public PenRequestEntity createPenRequest(PenRequestEntity penRequest) throws Exception {
        try{
            return penRequestRepository.save(penRequest);
        } catch(Exception e){
            throw new Exception("Error while creating PenRequest", e);
        }
    }

    public PenRequestEntity updatePenRequest(PenRequestEntity penRequest) throws Exception {
        try{
            return penRequestRepository.save(penRequest);
        } catch(Exception e){
            throw new Exception("Error while updating PenRequest", e);
        }
    }
}