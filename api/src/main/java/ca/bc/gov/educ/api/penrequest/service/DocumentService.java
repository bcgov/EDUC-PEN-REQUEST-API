package ca.bc.gov.educ.api.penrequest.service;

import ca.bc.gov.educ.api.penrequest.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.penrequest.exception.InvalidParameterException;
import ca.bc.gov.educ.api.penrequest.exception.InvalidValueException;
import ca.bc.gov.educ.api.penrequest.model.DocumentEntity;
import ca.bc.gov.educ.api.penrequest.model.DocumentTypeCodeEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.props.ApplicationProperties;
import ca.bc.gov.educ.api.penrequest.repository.DocumentRepository;
import ca.bc.gov.educ.api.penrequest.repository.DocumentTypeCodeTableRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.struct.DocumentRequirement;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

@Service
public class DocumentService {

    private static Logger logger = Logger.getLogger(DocumentService.class);

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private PenRequestRepository penRequestRepository;

    @Autowired
    private DocumentTypeCodeTableRepository documentTypeCodeRepository;

    @Autowired
    private ApplicationProperties properties;

    /**
     * Search for Document Metadata by id
     *
     * @param documentID
     * @return
     * @throws EntityNotFoundException
     */
    public DocumentEntity retrieveDocumentMetadata(UUID penrequestId, UUID documentID) throws EntityNotFoundException {
        logger.info("retrieving Document Metadata, documentID: " + documentID.toString());

        Optional<DocumentEntity> result = documentRepository.findById(documentID);
        if (! result.isPresent()) {
            throw new EntityNotFoundException(DocumentEntity.class, "documentID", documentID.toString());
        }

        DocumentEntity document = result.get();

        if(! document.getPenRequest().getPenRequestID().equals(penrequestId)) {
            throw new EntityNotFoundException(DocumentEntity.class, "penrequestId", penrequestId.toString());
        }
        
        return document;
    }

    /**
     * Search for Document with data by id
     *
     * @param documentID
     * @return
     * @throws EntityNotFoundException
     */
    @Transactional
    public DocumentEntity retrieveDocument(UUID penrequestId, UUID documentID) throws EntityNotFoundException {
        logger.info("retrieving Document, documentID: " + documentID.toString());

        DocumentEntity document = retrieveDocumentMetadata(penrequestId, documentID);
        // triger lazy loading
        if (document.getDocumentData().length == 0) {
            document.setFileSize(0);
        }
        return document;
    }

    /**
     * Search for all document metadata by penrequestId 
     * 
     * @return
     */
    public List<DocumentEntity> retrieveAllDocumentMetadata(UUID penrequestId) {
        List<DocumentEntity> documents = documentRepository.findByPenRequestPenRequestID(penrequestId);
        if(documents == null || documents.size() == 0) {
            throw new EntityNotFoundException(DocumentEntity.class, "penrequestId", penrequestId.toString());
        }
        return documents;
    }

    /**
     * Creates a DocumentEntity
     *
     * @param document
     * @return
     * @throws InvalidParameterException
     */
    public DocumentEntity createDocument(UUID penRequestId, DocumentEntity document) throws InvalidParameterException {
        logger.info(
                "creating Document, penRequestId: " + penRequestId.toString() + ", document: " + document.toString());

        validateParameters(document);

        Optional<PenRequestEntity> option = penRequestRepository.findById(penRequestId);

        if (option.isPresent()) {
            PenRequestEntity penRequest = option.get();
            document.setPenRequest(penRequest);

            Date curDate = new Date();
            document.setUpdateUser(ApplicationProperties.CLIENT_ID);
            document.setUpdateDate(curDate);
            document.setCreateUser(ApplicationProperties.CLIENT_ID);
            document.setCreateDate(curDate);
            return documentRepository.save(document);
        } else {
            throw new EntityNotFoundException(PenRequestEntity.class, "penRequestId", penRequestId.toString());
        }
    }

    /**
     * Delete DocumentEntity by id
     *
     * @param documentID
     * @return
     * @throws EntityNotFoundException
     */
    public DocumentEntity deleteDocument(UUID penrequestId, UUID documentID) throws EntityNotFoundException {
        logger.info("deleting Document, documentID: " + documentID.toString());

        DocumentEntity document = retrieveDocumentMetadata(penrequestId, documentID);
        documentRepository.delete(document);
        return document;
    }

    public List<DocumentTypeCodeEntity> getDocumentTypeCodeList() {
        List<DocumentTypeCodeEntity> result =  documentTypeCodeRepository.findAll();
        if(result != null && !result.isEmpty()) {
            return result;
        } else {
            throw new EntityNotFoundException(DocumentTypeCodeEntity.class);
        }
    }

    /**
     * Get File Upload Requirement
     *
     * @return DocumentRequirementEntity
     */
    public DocumentRequirement getDocumentRequirements() {
        logger.info("retrieving Document Requirements");

        return new DocumentRequirement(properties.getMaxFileSize(), properties.getFileExtensions());
    }

    private void validateParameters(DocumentEntity document) throws InvalidParameterException {

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

        if (! documentTypeCodeRepository.findById(document.getDocumentTypeCode()).isPresent()) {
            throw new InvalidValueException("documentTypeCode", document.getDocumentTypeCode());
        }
    }
}
