package ca.bc.gov.educ.api.penrequest.endpoint;

import ca.bc.gov.educ.api.penrequest.struct.PenRequestMacro;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping("/pen-request-macro")
public interface PenRequestMacroEndpoint {

  @GetMapping
  @PreAuthorize("#oauth2.hasScope('READ_PEN_REQ_MACRO')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  List<PenRequestMacro> findPenReqMacros(@RequestParam(value = "macroTypeCode", required = false) String macroTypeCode);

  @GetMapping("/{macroId}")
  @PreAuthorize("#oauth2.hasScope('READ_PEN_REQ_MACRO')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  PenRequestMacro findPenReqMacroById(@PathVariable UUID macroId);

  @PostMapping
  @PreAuthorize("#oauth2.hasAnyScope('WRITE_PEN_REQ_MACRO')")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED")})
  @ResponseStatus(CREATED)
  PenRequestMacro createPenReqMacro(@Validated @RequestBody PenRequestMacro penRequestMacro);

  @PutMapping("/{macroId}")
  @PreAuthorize("#oauth2.hasAnyScope('WRITE_PEN_REQ_MACRO')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  PenRequestMacro updatePenReqMacro(@PathVariable UUID macroId, @Validated @RequestBody PenRequestMacro penRequestMacro);
}
