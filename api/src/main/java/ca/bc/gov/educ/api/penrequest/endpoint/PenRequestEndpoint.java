package ca.bc.gov.educ.api.penrequest.endpoint;

import ca.bc.gov.educ.api.penrequest.struct.PenRequest;
import ca.bc.gov.educ.api.penrequest.struct.PenRequestStatusCode;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/")
@OpenAPIDefinition(info = @Info(title = "API for Pen Requests.", description = "This CRUD API is for Pen Requests tied to a Digital ID for a particular student in BC.", version = "1"), security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_PEN_REQUEST", "WRITE_PEN_REQUEST"})})
public interface PenRequestEndpoint {

  @PreAuthorize("#oauth2.hasScope('READ_PEN_REQUEST')")
  @GetMapping("/{id}")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  PenRequest retrievePenRequest(@PathVariable String id);

  @PreAuthorize("#oauth2.hasScope('READ_PEN_REQUEST')")
  @GetMapping("/")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  Iterable<PenRequest> retrieveAllRequests();

  @PreAuthorize("#oauth2.hasScope('WRITE_PEN_REQUEST')")
  @PostMapping()
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED"), @ApiResponse(responseCode = "200", description = "OK")})
  PenRequest createPenRequest(@Validated @RequestBody PenRequest penRequest);

  @PreAuthorize("#oauth2.hasScope('WRITE_PEN_REQUEST')")
  @PutMapping()
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  PenRequest updatePenRequest(@Validated @RequestBody PenRequest penRequest);

  @PreAuthorize("#oauth2.hasScope('READ_PEN_REQUEST_STATUSES')")
  @GetMapping("/statuses")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  List<PenRequestStatusCode> getPenRequestStatusCodes();


}
