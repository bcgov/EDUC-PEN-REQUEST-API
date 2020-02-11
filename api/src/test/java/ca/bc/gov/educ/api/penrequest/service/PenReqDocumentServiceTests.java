package ca.bc.gov.educ.api.penrequest.service;

import ca.bc.gov.educ.api.penrequest.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.penrequest.model.DocumentEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.repository.DocumentRepository;
import ca.bc.gov.educ.api.penrequest.repository.DocumentTypeCodeTableRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.support.DocumentBuilder;
import ca.bc.gov.educ.api.penrequest.support.DocumentTypeCodeBuilder;
import ca.bc.gov.educ.api.penrequest.support.PenRequestBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//import javax.transaction.Transactional;


@RunWith(SpringRunner.class)
@SpringBootTest
//@Transactional
public class PenReqDocumentServiceTests {

  @Autowired
  DocumentService service;

  @Autowired
  private DocumentRepository repository;

  @Autowired
  private PenRequestRepository PenRequestRepository;

  @Autowired
  private DocumentTypeCodeTableRepository documentTypeCodeRepository;

  private DocumentEntity bcscPhoto;

  private PenRequestEntity penRequest;

  private UUID penRequestID;

  @Before
  public void setUp() {
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
  public void createValidDocumentTest() {
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
  public void retrieveDocumentMetadataTest() {
    DocumentEntity retrievedDocument = service.retrieveDocumentMetadata(this.penRequestID, bcscPhoto.getDocumentID());
    assertThat(retrievedDocument).isNotNull();
    assertThat(retrievedDocument.getDocumentTypeCode()).isEqualTo("BCSCPHOTO");

    assertThat(retrievedDocument.getPenRequest().getPenRequestID()).isEqualTo(this.penRequestID);
  }

  @Test
  public void retrieveDocumentMetadataThrowsExceptionWhenInvalidDocumentIdGivenTest() {
    assertThatThrownBy(() -> service.retrieveDocumentMetadata(this.penRequestID, UUID.randomUUID()))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("DocumentEntity");
  }

  @Test
  public void retrieveDocumentMetadataThrowsExceptionWhenInvalidPenRequestIdGivenTest() {
    assertThatThrownBy(() -> service.retrieveDocumentMetadata(UUID.randomUUID(), bcscPhoto.getDocumentID()))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("DocumentEntity");
  }

  @Test
  public void retrieveDocumentDataTest() {
    DocumentEntity retrievedDocument = service.retrieveDocument(this.penRequestID, bcscPhoto.getDocumentID());
    assertThat(retrievedDocument).isNotNull();
    assertThat(retrievedDocument.getDocumentTypeCode()).isEqualTo("BCSCPHOTO");

    assertThat(retrievedDocument.getDocumentData()).isEqualTo(bcscPhoto.getDocumentData());
  }

  @Test
  public void retrieveAllDocumentMetadataTest() {
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
  public void deleteDocumentTest() {
    DocumentEntity deletedDocument = service.deleteDocument(this.penRequestID, this.bcscPhoto.getDocumentID());
    assertThat(deletedDocument).isNotNull();

    assertThatThrownBy(() -> service.retrieveDocumentMetadata(this.penRequestID, this.bcscPhoto.getDocumentID()))
            .isInstanceOf(EntityNotFoundException.class);
  }

  @Test
  public void deleteDocumentThrowsExceptionWhenInvalidIdGivenTest() {
    assertThatThrownBy(() -> service.deleteDocument(this.penRequestID, UUID.randomUUID()))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("DocumentEntity");
  }
}
