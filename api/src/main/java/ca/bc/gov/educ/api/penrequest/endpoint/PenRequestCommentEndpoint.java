package ca.bc.gov.educ.api.penrequest.endpoint;

import ca.bc.gov.educ.api.penrequest.struct.PenRequestComments;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;


@RequestMapping("/")
public interface PenRequestCommentEndpoint {

  @PreAuthorize("hasAuthority('SCOPE_READ_PEN_REQUEST')")
  @GetMapping("/{penRequestId}/comments")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  @Transactional
  List<PenRequestComments> retrieveComments(@PathVariable String penRequestId);

  @PreAuthorize("hasAuthority('SCOPE_WRITE_PEN_REQUEST')")
  @PostMapping("/{penRequestId}/comments")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED"), @ApiResponse(responseCode = "400", description = "BAD REQUEST")})
  @ResponseStatus(CREATED)
  @Transactional
  PenRequestComments save(@PathVariable String penRequestId, @Validated @RequestBody PenRequestComments penRequestComments);


}
