package ca.bc.gov.educ.api.penrequest.controller;

import ca.bc.gov.educ.api.penrequest.endpoint.PenRequestEndpoint;
import ca.bc.gov.educ.api.penrequest.mappers.PenRequestEntityMapper;
import ca.bc.gov.educ.api.penrequest.mappers.PenRequestStatusCodeMapper;
import ca.bc.gov.educ.api.penrequest.service.PenRequestService;
import ca.bc.gov.educ.api.penrequest.struct.PenRequest;
import ca.bc.gov.educ.api.penrequest.struct.PenRequestStatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@EnableResourceServer
@Slf4j
public class PenRequestController implements PenRequestEndpoint {

  @Getter(AccessLevel.PRIVATE)
  private final PenRequestService service;
  private final PenRequestEntityMapper mapper = PenRequestEntityMapper.mapper;
  private final PenRequestStatusCodeMapper statusCodeMapper = PenRequestStatusCodeMapper.mapper;

  PenRequestController(@Autowired final PenRequestService penRequest) {
    this.service = penRequest;
  }

  public PenRequest retrievePenRequest(@PathVariable String id) {
    return mapper.toStructure(getService().retrievePenRequest(UUID.fromString(id)));
  }

  public Iterable<PenRequest> retrieveAllRequests() {
    val penRequests = new ArrayList<PenRequest>();
    getService().retrieveAllRequests().forEach(element -> penRequests.add(mapper.toStructure(element)));
    return penRequests;
  }

  public PenRequest createPenRequest(@Validated @RequestBody PenRequest penRequest) {
    return mapper.toStructure(getService().createPenRequest(mapper.toModel(penRequest)));
  }

  public PenRequest updatePenRequest(@Validated @RequestBody PenRequest penRequest) {
    return mapper.toStructure(getService().updatePenRequest(mapper.toModel(penRequest)));
  }

  public List<PenRequestStatusCode> getPenRequestStatusCodes() {
    val penRequestStatusCodes = new ArrayList<PenRequestStatusCode>();
    getService().getPenRequestStatusCodesList().forEach(element -> penRequestStatusCodes.add(statusCodeMapper.toStructure(element)));
    return penRequestStatusCodes;
  }

  @Override
  public String health() {
    log.info("Health Check OK, returning OK");
    return "OK";
  }

}
