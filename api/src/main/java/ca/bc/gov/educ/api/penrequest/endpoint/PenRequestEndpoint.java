package ca.bc.gov.educ.api.penrequest.endpoint;

import ca.bc.gov.educ.api.penrequest.struct.GenderCode;
import ca.bc.gov.educ.api.penrequest.struct.PenRequest;
import ca.bc.gov.educ.api.penrequest.struct.PenRequestStatusCode;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

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
  @Tag(name = "findPenRequests", description = "This api method will accept all or individual parameters and search the DB. if any parameter is null then it will be not included in the query.")
  Iterable<PenRequest> findPenRequests(@RequestParam(name = "digitalID", required = false) String digitalID, @RequestParam(name = "status", required = false) String status, @RequestParam(name = "pen", required = false) String pen);

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

  @DeleteMapping
  @PreAuthorize("#oauth2.hasScope('DELETE_PEN_REQUEST')")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "NO CONTENT"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  ResponseEntity<Void> deleteAll();

  @DeleteMapping("/{id}")
  @PreAuthorize("#oauth2.hasScope('DELETE_PEN_REQUEST')")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "NO CONTENT"),  @ApiResponse(responseCode = "404", description = "NOT FOUND."), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  ResponseEntity<Void> deleteById(@PathVariable UUID id);
}
