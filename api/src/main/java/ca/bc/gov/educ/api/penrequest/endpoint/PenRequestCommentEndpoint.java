package ca.bc.gov.educ.api.penrequest.endpoint;

import ca.bc.gov.educ.api.penrequest.struct.PenRequestComments;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/")
public interface PenRequestCommentEndpoint {

  @PreAuthorize("#oauth2.hasScope('READ_PEN_REQUEST')")
  @GetMapping("/{penRequestId}/comments")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  List<PenRequestComments> retrieveComments(@PathVariable String penRequestId);

  @PreAuthorize("#oauth2.hasScope('WRITE_PEN_REQUEST')")
  @PostMapping("/{penRequestId}/comments")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED"), @ApiResponse(responseCode = "400", description = "BAD REQUEST")})
  PenRequestComments save(@PathVariable String penRequestId, @Validated @RequestBody PenRequestComments penRequestComments);


}
