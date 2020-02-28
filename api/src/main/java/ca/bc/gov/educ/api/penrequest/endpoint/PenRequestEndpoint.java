package ca.bc.gov.educ.api.penrequest.endpoint;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import ca.bc.gov.educ.api.penrequest.struct.GenderCode;
import ca.bc.gov.educ.api.penrequest.struct.PenRequest;
import ca.bc.gov.educ.api.penrequest.struct.PenRequestStatusCode;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RequestMapping("/")
@OpenAPIDefinition(info = @Info(title = "API for Pen Requests.", description = "This CRUD API is for Pen Requests tied to a Digital ID for a particular student in BC.", version = "1"), security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_PEN_REQUEST", "WRITE_PEN_REQUEST"})})
public interface PenRequestEndpoint {

  @PreAuthorize("#oauth2.hasScope('READ_PEN_REQUEST')")
  @GetMapping("/{id}")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  PenRequest retrievePenRequest(@PathVariable String id);

  @PreAuthorize("#oauth2.hasScope('READ_PEN_REQUEST')")
  @GetMapping
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  Iterable<PenRequest> findPenRequests(@Param("digitalID") String digitalID, @Param("status") String status);

  @PreAuthorize("#oauth2.hasScope('WRITE_PEN_REQUEST')")
  @PostMapping
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED"), @ApiResponse(responseCode = "200", description = "OK")})
  @ResponseStatus(CREATED)
  @Transactional
  PenRequest createPenRequest(@Validated @RequestBody PenRequest penRequest);

  @PreAuthorize("#oauth2.hasScope('WRITE_PEN_REQUEST')")
  @PutMapping
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  @Transactional
  PenRequest updatePenRequest(@Validated @RequestBody PenRequest penRequest);

  @PreAuthorize("#oauth2.hasScope('READ_PEN_REQUEST_STATUSES')")
  @GetMapping("/statuses")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  List<PenRequestStatusCode> getPenRequestStatusCodes();
  
  @PreAuthorize("#oauth2.hasScope('READ_PEN_REQUEST_CODES')")
  @GetMapping("/gender-codes")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  List<GenderCode> getGenderCodes();

  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  @GetMapping("/health")
  String health();
}
