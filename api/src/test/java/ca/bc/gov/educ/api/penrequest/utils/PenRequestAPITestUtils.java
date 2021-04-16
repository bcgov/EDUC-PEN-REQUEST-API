package ca.bc.gov.educ.api.penrequest.utils;

import ca.bc.gov.educ.api.penrequest.repository.DocumentRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("test")
public class PenRequestAPITestUtils {

  @Autowired
  private DocumentRepository documentRepository;


  @Autowired
  private PenRequestRepository penRequestRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void cleanDB() {
    this.documentRepository.deleteAll();
    this.penRequestRepository.deleteAll();
  }
}
