package ca.bc.gov.educ.api.penrequest.endpoint;

import ca.bc.gov.educ.api.penrequest.struct.PenReqDocMetadata;
import ca.bc.gov.educ.api.penrequest.struct.PenReqDocRequirement;
import ca.bc.gov.educ.api.penrequest.struct.PenReqDocTypeCode;
import ca.bc.gov.educ.api.penrequest.struct.PenReqDocument;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping("/")
@Tag(name = "API for Pen Request Documents.", description = "This API is for Pen Request Documents.")
public interface PenReqDocumentEndpoint {

  @GetMapping("/{penRequestID}/documents/{documentID}")
  @PreAuthorize("#oauth2.hasScope('READ_DOCUMENT')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  PenReqDocument readDocument(@PathVariable String penRequestID, @PathVariable String documentID, @RequestParam(value = "includeDocData", defaultValue = "Y") String includeDocData);

  @PostMapping("/{penRequestID}/documents")
  @PreAuthorize("#oauth2.hasAnyScope('WRITE_DOCUMENT')")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED"), @ApiResponse(responseCode = "200", description = "OK")})
  @ResponseStatus(CREATED)
  PenReqDocMetadata createDocument(@PathVariable String penRequestID, @Validated @RequestBody PenReqDocument penReqDocument);

  @PutMapping("/{penRequestID}/documents/{documentID}")
  @PreAuthorize("#oauth2.hasScope('WRITE_DOCUMENT')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  PenReqDocMetadata updateDocument(@PathVariable UUID penRequestID, @PathVariable UUID documentID, @Validated @RequestBody PenReqDocument penReqDocument);

  @DeleteMapping("/{penRequestID}/documents/{documentID}")
  @PreAuthorize("#oauth2.hasScope('DELETE_DOCUMENT')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  PenReqDocMetadata deleteDocument(@PathVariable String penRequestID, @PathVariable String documentID);

  @GetMapping("/{penRequestID}/documents")
  @PreAuthorize("#oauth2.hasScope('READ_DOCUMENT')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  Iterable<PenReqDocMetadata> readAllDocumentMetadata(@PathVariable String penRequestID);

  @GetMapping("/file-requirements")
  @PreAuthorize("#oauth2.hasScope('READ_DOCUMENT_REQUIREMENTS')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  PenReqDocRequirement getDocumentRequirements();

  @PreAuthorize("#oauth2.hasScope('READ_DOCUMENT_TYPES')")
  @GetMapping("/document-types")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  Iterable<PenReqDocTypeCode> getDocumentTypeCodes();
}
