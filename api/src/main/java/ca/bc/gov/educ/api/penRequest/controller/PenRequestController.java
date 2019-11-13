package ca.bc.gov.educ.api.penRequest.controller;

import ca.bc.gov.educ.api.penRequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penRequest.service.PenRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("penrequest")
public class PenRequestController {

    @Autowired
    private final PenRequestService service;

    PenRequestController(PenRequestService penRequest){
        this.service = penRequest;
    }

    @GetMapping("/{id}")
    public PenRequestEntity retrievePenRequest(@PathVariable String id) throws Exception {
        return service.retrievePenRequest(id);
    }

    @PostMapping()
    public PenRequestEntity createPenRequest(@Validated @RequestBody PenRequestEntity penRequest) throws Exception {
        return service.createPenRequest(penRequest);
    }

    @PutMapping()
    public PenRequestEntity updatePenRequest(@Validated @RequestBody PenRequestEntity penRequest) throws Exception {
        return service.updatePenRequest(penRequest);
    }
}