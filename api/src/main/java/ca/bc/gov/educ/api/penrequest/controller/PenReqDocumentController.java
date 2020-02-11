package ca.bc.gov.educ.api.penrequest.controller;

import ca.bc.gov.educ.api.penrequest.endpoint.PenReqDocumentEndpoint;
import ca.bc.gov.educ.api.penrequest.mappers.DocumentMapper;
import ca.bc.gov.educ.api.penrequest.mappers.DocumentTypeCodeMapper;
import ca.bc.gov.educ.api.penrequest.service.DocumentService;
import ca.bc.gov.educ.api.penrequest.struct.PenReqDocMetadata;
import ca.bc.gov.educ.api.penrequest.struct.PenReqDocTypeCode;
import ca.bc.gov.educ.api.penrequest.struct.PenReqDocument;
import ca.bc.gov.educ.api.penrequest.struct.PenReqDocRequirement;
import ca.bc.gov.educ.api.penrequest.validator.PenRequestDocumentsValidator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@EnableResourceServer
public class PenReqDocumentController extends BaseController implements PenReqDocumentEndpoint {

  private static final DocumentMapper mapper = DocumentMapper.mapper;

  private static final DocumentTypeCodeMapper documentTypeCodeMapper = DocumentTypeCodeMapper.mapper;

  @Getter(AccessLevel.PRIVATE)
  private final DocumentService documentService;
  @Getter(AccessLevel.PRIVATE)
  private final PenRequestDocumentsValidator validator;

  @Autowired
  PenReqDocumentController(final DocumentService documentService, final PenRequestDocumentsValidator validator) {
    this.documentService = documentService;
    this.validator = validator;
  }

  @Override
  public PenReqDocument readDocument(String penRequestID, String documentID) {
    return mapper.toStructure(getDocumentService().retrieveDocument(UUID.fromString(penRequestID), UUID.fromString(documentID)));
  }

  @Override
  public PenReqDocMetadata createDocument(String penRequestID, PenReqDocument penReqDocument) {
    setAuditColumns(penReqDocument);
    val model = mapper.toModel(penReqDocument);
    getValidator().validateDocumentPayload(model);
    return mapper.toMetadataStructure(getDocumentService().createDocument(UUID.fromString(penRequestID), model));
  }

  public PenReqDocMetadata deleteDocument(String penRequestID, String documentID) {
    return mapper.toMetadataStructure(getDocumentService().deleteDocument(UUID.fromString(penRequestID), UUID.fromString(documentID)));
  }

  public Iterable<PenReqDocMetadata> readAllDocumentMetadata(String penRequestID) {
    return getDocumentService().retrieveAllDocumentMetadata(UUID.fromString(penRequestID))
            .stream().map(mapper::toMetadataStructure).collect(Collectors.toList());
  }

  public PenReqDocRequirement getDocumentRequirements() {
    return documentService.getDocumentRequirements();
  }

  public List<PenReqDocTypeCode> getDocumentTypeCodes() {
    return getDocumentService().getDocumentTypeCodeList().stream()
            .map(documentTypeCodeMapper::toStructure).collect(Collectors.toList());
  }

}
