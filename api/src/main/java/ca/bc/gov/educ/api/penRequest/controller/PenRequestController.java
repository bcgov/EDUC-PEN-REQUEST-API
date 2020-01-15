package ca.bc.gov.educ.api.penRequest.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.educ.api.penRequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penRequest.model.PenRequestStatusCodeEntity;
import ca.bc.gov.educ.api.penRequest.service.PenRequestService;

@RestController
@RequestMapping("/")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableResourceServer
public class PenRequestController {

    @Autowired
    private final PenRequestService service;

    PenRequestController(PenRequestService penRequest){
        this.service = penRequest;
    }

    @PreAuthorize("#oauth2.hasScope('READ_PEN_REQUEST')")
    @GetMapping("/{id}")
    public PenRequestEntity retrievePenRequest(@PathVariable UUID id) throws Exception {
        return service.retrievePenRequest(id);
    }

    @PreAuthorize("#oauth2.hasScope('READ_PEN_REQUEST')")
    @GetMapping("/")
    public Iterable<PenRequestEntity> retrieveAllRequests() throws Exception {
        return service.retrieveAllRequests();
    }

    @PreAuthorize("#oauth2.hasScope('WRITE_PEN_REQUEST')")
    @PostMapping()
    public PenRequestEntity createPenRequest(@Validated @RequestBody PenRequestEntity penRequest) throws Exception {
        return service.createPenRequest(penRequest);
    }

    @PreAuthorize("#oauth2.hasScope('WRITE_PEN_REQUEST')")
    @PutMapping()
    public PenRequestEntity updatePenRequest(@Validated @RequestBody PenRequestEntity penRequest) throws Exception {
        return service.updatePenRequest(penRequest);
    }
    
    @PreAuthorize("#oauth2.hasScope('READ_PEN_REQUEST_STATUSES')")
    @GetMapping("/statuses")
    public List<PenRequestStatusCodeEntity> getPenRequestStatusCodes() throws Exception {
        return service.getPenRequestStatusCodesList();
    }

    @GetMapping("/health")
    public void health(){
    }

}