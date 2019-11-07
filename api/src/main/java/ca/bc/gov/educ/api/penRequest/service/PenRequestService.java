package ca.bc.gov.educ.api.penRequest.service;

import ca.bc.gov.educ.api.penRequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penRequest.props.ApplicationProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

@Service
public class PenRequestService {

    @Autowired
    private PenRequestRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public PenRequestEntity retrievePenRequest(String id) throws Exception{
        try{
            PenRequestRepository.findById(id);
        } catch(Exception e){
            logger.error("Error while retrieving PenRequest: " + e);
            throw new Exception("Error while retrieving PenRequest", e);
        }
    }

    public PenRequestEntity createPenRequest(PenRequestEntity penRequest) throws Exception {
        try{
            PenRequestRepository.save(penRequest);
        } catch(Exception e){
            logger.error("Error while creating PenRequest: " + e);
            throw new Exception("Error while creating PenRequest", e);
        }
    }

    public PenRequestEntity updatePenRequest(PenRequesEntity penRequest) throws Exception {
        try{
            PenRequestRepository.save(penRequest);
        } catch(Exception e){
            logger.error("Error while updating PenRequest: " + e);
            throw new Exception("Error while updating PenRequest", e);
        }
    }
}