package ca.bc.gov.educ.api.penrequest.endpoint;

import ca.bc.gov.educ.api.penrequest.struct.Document;
import ca.bc.gov.educ.api.penrequest.struct.DocumentMetadata;
import ca.bc.gov.educ.api.penrequest.struct.DocumentRequirement;
import ca.bc.gov.educ.api.penrequest.struct.DocumentTypeCode;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping("/")
@OpenAPIDefinition(info = @Info(title = "API for Pen Request Documents.", description = "This API is for Pen Request Documents.", version = "1"), 
                  security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_DOCUMENT", "WRITE_DOCUMENT", "DELETE_DOCUMENT", "READ_DOCUMENT_REQUIREMENTS", "READ_DOCUMENT_TYPES"})})
public interface DocumentEndpoint {

  @GetMapping("/{penRequestID}/documents/{documentID}")
  @PreAuthorize("#oauth2.hasScope('READ_DOCUMENT')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  Document readDocument(@PathVariable String penRequestID, @PathVariable String documentID);

  @PostMapping("/{penRequestID}/documents")
  @PreAuthorize("#oauth2.hasAnyScope('WRITE_DOCUMENT')")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED"), @ApiResponse(responseCode = "200", description = "OK")})
  @ResponseStatus(CREATED)
  DocumentMetadata createDocument(@PathVariable String penRequestID, @Validated @RequestBody Document document);

  @DeleteMapping("/{penRequestID}/documents/{documentID}")
  @PreAuthorize("#oauth2.hasScope('DELETE_DOCUMENT')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  DocumentMetadata deleteDocument(@PathVariable String penRequestID, @PathVariable String documentID);

  @GetMapping("/{penRequestID}/documents")
  @PreAuthorize("#oauth2.hasScope('READ_DOCUMENT')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  Iterable<DocumentMetadata> readAllDocumentMetadata(@PathVariable String penRequestID);

  @GetMapping("/file-requirements")
  @PreAuthorize("#oauth2.hasScope('READ_DOCUMENT_REQUIREMENTS')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  DocumentRequirement getDocumentRequirements();

  @PreAuthorize("#oauth2.hasScope('READ_DOCUMENT_TYPES')")
  @GetMapping("/document-types")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  Iterable<DocumentTypeCode> getDocumentTypeCodes();
}
