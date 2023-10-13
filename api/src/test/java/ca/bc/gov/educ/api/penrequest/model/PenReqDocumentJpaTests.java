package ca.bc.gov.educ.api.penrequest.model;

import ca.bc.gov.educ.api.penrequest.PenRequestApiResourceApplication;
import ca.bc.gov.educ.api.penrequest.model.v1.DocumentEntity;
import ca.bc.gov.educ.api.penrequest.model.v1.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.repository.DocumentRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.support.DocumentBuilder;
import ca.bc.gov.educ.api.penrequest.support.PenRequestBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PenRequestApiResourceApplication.class})
@ActiveProfiles("test")
public class PenReqDocumentJpaTests {

    @Autowired
    private DocumentRepository repository;

    @Autowired
    private PenRequestRepository penRequestRepsository;

    private DocumentEntity document;

    private PenRequestEntity penRequest;

    @Before
    public void setUp() {
        this.penRequest = penRequestRepsository.save(new PenRequestBuilder().withoutPenRequestID().build());
        this.document = new DocumentBuilder()
                            .withoutDocumentID()
                            .withPenRequest(this.penRequest).build();

        document = this.repository.save(document);
    }

    @After
    public void after() {
        this.repository.deleteAll();
        this.penRequestRepsository.deleteAll();
    }

    @Test
    public void findDocumentTest() {
        Optional<DocumentEntity> myDocument = this.repository.findById(this.document.getDocumentID());
        assertThat(myDocument).isPresent();
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

        assertThat(this.repository.findById(savedDocument.getDocumentID())).isPresent();
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
        assertThat(this.repository.findById(this.document.getDocumentID())).isEmpty();
    }
}
