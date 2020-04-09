package ca.bc.gov.educ.api.penrequest.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.educ.api.penrequest.endpoint.PenRequestEndpoint;
import ca.bc.gov.educ.api.penrequest.exception.InvalidPayloadException;
import ca.bc.gov.educ.api.penrequest.exception.errors.ApiError;
import ca.bc.gov.educ.api.penrequest.mappers.PenRequestEntityMapper;
import ca.bc.gov.educ.api.penrequest.mappers.PenRequestGenderCodeMapper;
import ca.bc.gov.educ.api.penrequest.mappers.PenRequestStatusCodeMapper;
import ca.bc.gov.educ.api.penrequest.service.PenRequestService;
import ca.bc.gov.educ.api.penrequest.struct.GenderCode;
import ca.bc.gov.educ.api.penrequest.struct.PenRequest;
import ca.bc.gov.educ.api.penrequest.struct.PenRequestStatusCode;
import ca.bc.gov.educ.api.penrequest.utils.UUIDUtil;
import ca.bc.gov.educ.api.penrequest.validator.PenRequestPayloadValidator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@EnableResourceServer
@Slf4j
public class PenRequestController extends BaseController implements PenRequestEndpoint {

  @Getter(AccessLevel.PRIVATE)
  private final PenRequestPayloadValidator payloadValidator;
  @Getter(AccessLevel.PRIVATE)
  private final PenRequestService service;
  private static final PenRequestEntityMapper mapper = PenRequestEntityMapper.mapper;
  private static final PenRequestStatusCodeMapper statusCodeMapper = PenRequestStatusCodeMapper.mapper;
  private static final PenRequestGenderCodeMapper genderCodeMapper = PenRequestGenderCodeMapper.mapper;

  @Autowired
  PenRequestController(final PenRequestService penRequest, final PenRequestPayloadValidator payloadValidator) {
    this.service = penRequest;
    this.payloadValidator = payloadValidator;
  }

  public PenRequest retrievePenRequest(String id) {
    return mapper.toStructure(getService().retrievePenRequest(UUIDUtil.fromString(id)));
  }

  @Override
  public Iterable<PenRequest> findPenRequests(final String digitalID, final String status, final String pen) {
    return getService().findPenRequests(UUIDUtil.fromString(digitalID), status, pen).stream().map(mapper::toStructure).collect(Collectors.toList());
  }

  public PenRequest createPenRequest(PenRequest penRequest) {
    validatePayload(penRequest, true);
    setAuditColumns(penRequest);
    return mapper.toStructure(getService().createPenRequest(mapper.toModel(penRequest)));
  }

  public PenRequest updatePenRequest(PenRequest penRequest) {
    validatePayload(penRequest, false);
    setAuditColumns(penRequest);
    return mapper.toStructure(getService().updatePenRequest(mapper.toModel(penRequest)));
  }

  public List<PenRequestStatusCode> getPenRequestStatusCodes() {
    val penRequestStatusCodes = new ArrayList<PenRequestStatusCode>();
    getService().getPenRequestStatusCodesList().forEach(element -> penRequestStatusCodes.add(statusCodeMapper.toStructure(element)));
    return penRequestStatusCodes;
  }

  public List<GenderCode> getGenderCodes() {
    return getService().getGenderCodesList().stream().map(genderCodeMapper::toStructure).collect(Collectors.toList());
  }

  @Override
  public String health() {
    return "OK";
  }

  private void validatePayload(PenRequest penRequest, boolean isCreateOperation) {
    val validationResult = getPayloadValidator().validatePayload(penRequest, isCreateOperation);
    if (!validationResult.isEmpty()) {
      ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).message("Payload contains invalid data.").status(BAD_REQUEST).build();
      error.addValidationErrors(validationResult);
      throw new InvalidPayloadException(error);
    }
  }

}

