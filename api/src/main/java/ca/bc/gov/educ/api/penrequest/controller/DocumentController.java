package ca.bc.gov.educ.api.penrequest.controller;

import ca.bc.gov.educ.api.penrequest.endpoint.DocumentEndpoint;
import ca.bc.gov.educ.api.penrequest.mappers.DocumentMapper;
import ca.bc.gov.educ.api.penrequest.mappers.DocumentTypeCodeMapper;
import ca.bc.gov.educ.api.penrequest.service.DocumentService;
import ca.bc.gov.educ.api.penrequest.struct.Document;
import ca.bc.gov.educ.api.penrequest.struct.DocumentMetadata;
import ca.bc.gov.educ.api.penrequest.struct.DocumentRequirement;
import ca.bc.gov.educ.api.penrequest.struct.DocumentTypeCode;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@EnableResourceServer
public class DocumentController implements DocumentEndpoint {

  private static final DocumentMapper mapper = DocumentMapper.mapper;
  private static final  DocumentTypeCodeMapper documentTypeCodeMapper = DocumentTypeCodeMapper.mapper;

  @Getter(AccessLevel.PRIVATE)
  private final DocumentService documentService;

  DocumentController(@Autowired final DocumentService documentService) {
    this.documentService = documentService;
  }

  @Override
  public Document readDocument(String penRequestID, String documentID) {
    return mapper.toStructure(getDocumentService().retrieveDocument(UUID.fromString(penRequestID), UUID.fromString(documentID)));
  }

  @Override
  public DocumentMetadata createDocument(String penRequestID, Document document) {
    return mapper.toMetadataStructure(getDocumentService().createDocument(UUID.fromString(penRequestID), mapper.toModel(document)));
  }

  public DocumentMetadata deleteDocument(String penRequestID, String documentID) {
    return mapper.toMetadataStructure(getDocumentService().deleteDocument(UUID.fromString(penRequestID), UUID.fromString(documentID)));
  }

  public Iterable<DocumentMetadata> readAllDocumentMetadata(String penRequestID) {
    return getDocumentService().retrieveAllDocumentMetadata(UUID.fromString(penRequestID))
            .stream().map(mapper::toMetadataStructure).collect(Collectors.toList());
  }

  public DocumentRequirement getDocumentRequirements() {
    return documentService.getDocumentRequirements();
  }

  public List<DocumentTypeCode> getDocumentTypeCodes() {
    return getDocumentService().getDocumentTypeCodeList().stream()
            .map(documentTypeCodeMapper::toStructure).collect(Collectors.toList());
  }

}
