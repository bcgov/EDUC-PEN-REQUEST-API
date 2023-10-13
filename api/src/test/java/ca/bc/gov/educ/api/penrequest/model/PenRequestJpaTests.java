package ca.bc.gov.educ.api.penrequest.model;

import static org.assertj.core.api.Assertions.assertThat;


import ca.bc.gov.educ.api.penrequest.PenRequestApiResourceApplication;
import ca.bc.gov.educ.api.penrequest.model.v1.PenRequestEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.support.PenRequestBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PenRequestApiResourceApplication.class})
@ActiveProfiles("test")
public class PenRequestJpaTests {
    @Autowired
    private PenRequestRepository repository;

    private PenRequestEntity penRequest;

    @Before
    public void setUp() {
        this.penRequest = new PenRequestBuilder()
                            .withoutPenRequestID().build();
    }

    @After
    public void after() {
        this.repository.deleteAll();
    }

    @Test
    public void saveDocumentTest() {
        PenRequestEntity savedPenRequest = this.repository.save(this.penRequest);
        assertThat(savedPenRequest.getPenRequestID()).isNotNull();
        assertThat(savedPenRequest.getInitialSubmitDate()).isNull();

        assertThat(this.repository.findById(savedPenRequest.getPenRequestID())).isPresent();
    }

}
