package ca.bc.gov.educ.api.penrequest.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import ca.bc.gov.educ.api.penrequest.repository.DocumentRepository;
import ca.bc.gov.educ.api.support.DocumentBuilder;
import ca.bc.gov.educ.api.support.PenRequestBuilder;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DocumentJpaTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DocumentRepository repository;

    private DocumentEntity document;

    private PenRequestEntity penRequest;

    @Before
    public void setUp() {
        this.penRequest = new PenRequestBuilder()
                                            .withoutPenRequestID().build();
        this.document = new DocumentBuilder()
                            .withoutDocumentID()
                            .withPenRequest(this.penRequest).build();

        this.entityManager.persist(this.penRequest);
        this.entityManager.persist(this.document);
        this.entityManager.flush();
        //document = this.repository.save(document);
        this.entityManager.clear();
    }

    @Test
    public void findDocumentTest() {
        Optional<DocumentEntity> myDocument = this.repository.findById(this.document.getDocumentID());
        assertThat(myDocument.isPresent()).isTrue();
        assertThat(myDocument.get().getDocumentTypeCode()).isEqualTo("BCSCPHOTO");
    }

    @Test
    public void saveDocumentTest() {
        DocumentEntity myDocument = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .withPenRequest(this.penRequest).build();
        DocumentEntity savedDocument = this.repository.save(myDocument);
        assertThat(savedDocument.getDocumentID()).isNotEqualTo(this.document.getDocumentID());
        assertThat(savedDocument.getPenRequest()).isNotNull();

        assertThat(this.repository.findById(savedDocument.getDocumentID()).isPresent()).isTrue();
    }

    @Test
    public void findDocumentByPenRequestTest() {
        DocumentEntity myDocument = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .withPenRequest(this.penRequest).build();
        DocumentEntity savedDocument = this.repository.save(myDocument);
        assertThat(savedDocument.getDocumentID()).isNotEqualTo(this.document.getDocumentID());

        assertThat(this.repository.findByPenRequestPenRequestID(this.penRequest.getPenRequestID()).size()).isEqualTo(2);
    }

    @Test
    public void deleteDocumentTest() {
        this.repository.deleteById(this.document.getDocumentID());
        assertThat(this.repository.findById(this.document.getDocumentID()).isPresent()).isFalse();
    }
}