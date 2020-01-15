package ca.bc.gov.educ.api.penRequest.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import ca.bc.gov.educ.api.penRequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penRequest.service.PenRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

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

    @GetMapping("/health")
    public void health(){
    }

}