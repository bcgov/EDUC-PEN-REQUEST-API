package ca.bc.gov.educ.api.penrequest.service;

import ca.bc.gov.educ.api.penrequest.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.penrequest.exception.InvalidParameterException;
import ca.bc.gov.educ.api.penrequest.exception.InvalidValueException;
import ca.bc.gov.educ.api.penrequest.model.DocumentEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.props.ApplicationProperties;
import ca.bc.gov.educ.api.penrequest.repository.DocumentRepository;
import ca.bc.gov.educ.api.penrequest.repository.DocumentTypeCodeTableRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.support.DocumentBuilder;
import ca.bc.gov.educ.api.support.DocumentTypeCodeBuilder;
import ca.bc.gov.educ.api.support.PenRequestBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;


@RunWith(SpringRunner.class)
@SpringBootTest
//@Transactional
public class DocumentServiceTests {

    @Autowired
    DocumentService service;

    @Autowired
    private DocumentRepository repository;

    @Autowired
    private PenRequestRepository PenRequestRepository;

    @Autowired
    private DocumentTypeCodeTableRepository documentTypeCodeRepository;

    @Autowired
    private ApplicationProperties props;

    private DocumentEntity bcscPhoto;

    private PenRequestEntity penRequest;

    private UUID penRequestID;

    @Before
    public void setUp() throws JsonMappingException, JsonProcessingException {
        DocumentTypeCodeBuilder.setUpDocumentTypeCodes(documentTypeCodeRepository);
        this.penRequest = new PenRequestBuilder()
                            .withoutPenRequestID().build();
        this.bcscPhoto = new DocumentBuilder()
                            .withoutDocumentID()
                            .withPenRequest(this.penRequest)
                            .build();
        this.penRequest = this.PenRequestRepository.save(this.penRequest);
        this.bcscPhoto = this.repository.save(this.bcscPhoto);
        this.penRequestID = this.penRequest.getPenRequestID();
    }

    @Test
    public void createValidDocumentTest() throws ParseException {
        DocumentEntity document = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .withoutCreateAndUpdateUser()
                                        .build();
        document = service.createDocument(this.penRequestID, document);

        assertThat(document).isNotNull();
        assertThat(document.getDocumentID()).isNotNull();
        assertThat(document.getPenRequest().getPenRequestID()).isEqualTo(penRequestID);
    }

    @Test
    public void createDocumentThrowsExceptionWhenIDGivenTest() throws ParseException {
        DocumentEntity document = new DocumentBuilder()
                                        .withoutCreateAndUpdateUser()
                                        .build();
        assertThatThrownBy(() -> service.createDocument(this.penRequestID, document))
            .isInstanceOf(InvalidParameterException.class)
            .hasMessageContaining("documentID");
    }

    @Test
    public void createDocumentThrowsExceptionWhenInvalidDocTypeGivenTest() throws ParseException {
        DocumentEntity document = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .withoutCreateAndUpdateUser()
                                        .withTypeCode("typeCode")
                                        .build();
        assertThatThrownBy(() -> service.createDocument(this.penRequestID, document))
            .isInstanceOf(InvalidValueException.class)
            .hasMessageContaining("documentTypeCode");
    }

    @Test
    public void createDocumentThrowsExceptionWhenInvalidPenRequestIDGivenTest() throws ParseException {
        DocumentEntity document = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .withoutCreateAndUpdateUser()
                                        .build();
        assertThatThrownBy(() -> service.createDocument(UUID.randomUUID(), document))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("penRequestId");
    }

    @Test
    public void createDocumentThrowsExceptionWhenWrongFileSizeGivenTest() throws ParseException {
        DocumentEntity document = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .withoutCreateAndUpdateUser()
                                        .withFileSize(2)
                                        .build();
        assertThatThrownBy(() -> service.createDocument(this.penRequestID, document))
            .isInstanceOf(InvalidValueException.class)
            .hasMessageContaining("fileSize");
    }

    @Test
    public void createDocumentThrowsExceptionWhenLargeFileGivenTest() throws ParseException {
        DocumentEntity document = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .withoutCreateAndUpdateUser()
                                        .withFileSize(props.getMaxFileSize() + 1)
                                        .build();
        assertThatThrownBy(() -> service.createDocument(this.penRequestID, document))
            .isInstanceOf(InvalidValueException.class)
            .hasMessageContaining("Max fileSize");
    }

    @Test
    public void createDocumentThrowsExceptionWhenInvalidFileExtensionGivenTest() throws ParseException {
        DocumentEntity document = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .withoutCreateAndUpdateUser()
                                        .withFileExtension("txt")
                                        .build();
        assertThatThrownBy(() -> service.createDocument(this.penRequestID, document))
            .isInstanceOf(InvalidValueException.class)
            .hasMessageContaining("fileExtension");
    }

    @Test
    public void retrieveDocumentMetadataTest() throws ParseException{
        DocumentEntity retrievedDocument = service.retrieveDocumentMetadata(this.penRequestID, bcscPhoto.getDocumentID());
        assertThat(retrievedDocument).isNotNull();
        assertThat(retrievedDocument.getDocumentTypeCode()).isEqualTo("BCSCPHOTO");
        
        assertThat(retrievedDocument.getPenRequest().getPenRequestID()).isEqualTo(this.penRequestID);
    }

    @Test
    public void retrieveDocumentMetadataThrowsExceptionWhenInvalidDocumentIdGivenTest() throws ParseException{
        assertThatThrownBy(() -> service.retrieveDocumentMetadata(this.penRequestID, UUID.randomUUID()))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("DocumentEntity");
    }

    @Test
    public void retrieveDocumentMetadataThrowsExceptionWhenInvalidPenRequestIdGivenTest() throws ParseException{
        assertThatThrownBy(() -> service.retrieveDocumentMetadata(UUID.randomUUID(), bcscPhoto.getDocumentID()))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("DocumentEntity");
    }

    @Test
    public void retrieveDocumentDataTest() throws ParseException{
        DocumentEntity retrievedDocument = service.retrieveDocument(this.penRequestID, bcscPhoto.getDocumentID());
        assertThat(retrievedDocument).isNotNull();
        assertThat(retrievedDocument.getDocumentTypeCode()).isEqualTo("BCSCPHOTO");
        
        assertThat(retrievedDocument.getDocumentData()).isEqualTo(bcscPhoto.getDocumentData());
    }

    @Test
    public void retrieveAllDocumentMetadataTest() throws ParseException {
        DocumentEntity document = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .withoutCreateAndUpdateUser()
                                        .withPenRequest(this.penRequest)
                                        .build();
        this.repository.save(document);

        List<DocumentEntity> documents = service.retrieveAllDocumentMetadata(this.penRequestID);
        assertThat(documents.size()).isEqualTo(2);
    }


    @Test
    public void deleteDocumentTest() throws ParseException{
        DocumentEntity deletedDocument = service.deleteDocument(this.penRequestID, this.bcscPhoto.getDocumentID());
        assertThat(deletedDocument).isNotNull();

        assertThatThrownBy(() -> service.retrieveDocumentMetadata(this.penRequestID, this.bcscPhoto.getDocumentID()))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void deleteDocumentThrowsExceptionWhenInvalidIdGivenTest() throws ParseException{
        assertThatThrownBy(() -> service.deleteDocument(this.penRequestID, UUID.randomUUID()))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("DocumentEntity");
    }
}
