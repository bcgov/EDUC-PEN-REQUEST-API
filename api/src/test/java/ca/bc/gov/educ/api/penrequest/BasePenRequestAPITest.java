package ca.bc.gov.educ.api.penrequest;

import ca.bc.gov.educ.api.penrequest.utils.PenRequestAPITestUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PenRequestApiResourceApplication.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class BasePenRequestAPITest {

  @Autowired
  PenRequestAPITestUtils penRequestAPITestUtils;

  @Before
  public void before() {
    this.penRequestAPITestUtils.cleanDB();
  }
}
