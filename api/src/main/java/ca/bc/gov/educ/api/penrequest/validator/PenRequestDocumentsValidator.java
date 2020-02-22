package ca.bc.gov.educ.api.penrequest.validator;

import ca.bc.gov.educ.api.penrequest.exception.InvalidParameterException;
import ca.bc.gov.educ.api.penrequest.exception.InvalidValueException;
import ca.bc.gov.educ.api.penrequest.model.DocumentEntity;
import ca.bc.gov.educ.api.penrequest.model.DocumentTypeCodeEntity;
import ca.bc.gov.educ.api.penrequest.props.ApplicationProperties;
import ca.bc.gov.educ.api.penrequest.repository.DocumentTypeCodeTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class PenRequestDocumentsValidator {

  private final ApplicationProperties properties;
  private final DocumentTypeCodeTableRepository documentTypeCodeRepository;

  @Autowired
  public PenRequestDocumentsValidator(final ApplicationProperties properties, final DocumentTypeCodeTableRepository documentTypeCodeRepository) {
    this.properties = properties;
    this.documentTypeCodeRepository = documentTypeCodeRepository;
  }

  @PostConstruct
  public void init() {
    loadDocumentType();
  }

  @Cacheable("documentTypeCodes")
  public List<DocumentTypeCodeEntity> loadDocumentType() {
    return documentTypeCodeRepository.findAll();
  }

  public void validateDocumentPayload(final DocumentEntity document) {
    if (document.getDocumentID() != null) {
      throw new InvalidParameterException("documentID");
    }

    if (!properties.getFileExtensions().contains(document.getFileExtension())) {
      throw new InvalidValueException("fileExtension", document.getFileExtension());
    }

    if (document.getFileSize() > properties.getMaxFileSize()) {
      throw new InvalidValueException("fileSize", document.getFileSize().toString(), "Max fileSize",
              String.valueOf(properties.getMaxFileSize()));
    }

    if (document.getFileSize() != document.getDocumentData().length) {
      throw new InvalidValueException("fileSize", document.getFileSize().toString(), "documentData length",
              String.valueOf(document.getDocumentData().length));
    }

    if (!isDocumentTypeCodeValid(document.getDocumentTypeCode())) {
      throw new InvalidValueException("documentTypeCode", document.getDocumentTypeCode());
    }
  }

  public boolean isDocumentTypeCodeValid(final String documentTypeCode) {
    for (DocumentTypeCodeEntity entity : loadDocumentType()) {
      if (entity.getDocumentTypeCode().equalsIgnoreCase(documentTypeCode)) {
        return entity.getEffectiveDate().isBefore(LocalDateTime.now()) && entity.getExpiryDate().isAfter(LocalDateTime.now());
      }
    }
    return false;
  }
}
