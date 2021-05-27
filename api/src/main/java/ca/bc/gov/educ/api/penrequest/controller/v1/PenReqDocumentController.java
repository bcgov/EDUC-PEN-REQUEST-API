package ca.bc.gov.educ.api.penrequest.controller.v1;

import ca.bc.gov.educ.api.penrequest.controller.BaseController;
import ca.bc.gov.educ.api.penrequest.endpoint.v1.PenReqDocumentEndpoint;
import ca.bc.gov.educ.api.penrequest.mappers.v1.DocumentMapper;
import ca.bc.gov.educ.api.penrequest.mappers.v1.DocumentTypeCodeMapper;
import ca.bc.gov.educ.api.penrequest.service.v1.DocumentService;
import ca.bc.gov.educ.api.penrequest.struct.v1.PenReqDocMetadata;
import ca.bc.gov.educ.api.penrequest.struct.v1.PenReqDocTypeCode;
import ca.bc.gov.educ.api.penrequest.struct.v1.PenReqDocument;
import ca.bc.gov.educ.api.penrequest.struct.v1.PenReqDocRequirement;
import ca.bc.gov.educ.api.penrequest.validator.PenRequestDocumentsValidator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
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
  public PenReqDocument readDocument(String penRequestID, String documentID, String includeDocData) {
    return mapper.toStructure(getDocumentService().retrieveDocument(UUID.fromString(penRequestID), UUID.fromString(documentID), includeDocData));
  }

  @Override
  public PenReqDocMetadata createDocument(String penRequestID, PenReqDocument penReqDocument) {
    setAuditColumns(penReqDocument);
    val model = mapper.toModel(penReqDocument);
    getValidator().validateDocumentPayload(model, true);
    return mapper.toMetadataStructure(getDocumentService().createDocument(UUID.fromString(penRequestID), model));
  }

  @Override
  public PenReqDocMetadata updateDocument(UUID penRequestID, UUID documentID, PenReqDocument penReqDocument) {
    setAuditColumns(penReqDocument);
    val model = mapper.toModel(penReqDocument);
    getValidator().validateDocumentPayload(model, false);
    return mapper.toMetadataStructure(getDocumentService().updateDocument(penRequestID, documentID, model));
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
