package ca.bc.gov.educ.api.penrequest.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import ca.bc.gov.educ.api.penrequest.props.IntegrationTestProperties;
import ca.bc.gov.educ.api.penrequest.struct.v1.PenRequestMacro;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.*;

import java.io.IOException;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = IntegrationTestProperties.class)
public class PenRequestMacroITCase {
  @Autowired
  IntegrationTestProperties properties;

  private String token;

  @Before
  public void setUp() {
    token = TestUtils.getAccessToken(properties);
  }

  @Test
  public void testPenRequestMacroQuery() throws IOException {
    assertThat(token).isNotNull();

    var response = given().auth().oauth2(token).param("macroTypeCode", "MOREINFO")
        .when().get(properties.getApiUrl()+ "/pen-request-macro");

    response.then().assertThat().statusCode(200).and()
        .body("size()", is(not(lessThan(1))));

    var macros = response.as(PenRequestMacro[].class);

    given().auth().oauth2(token)
        .when().get(properties.getApiUrl()+ "/pen-request-macro/" + macros[0].getMacroId())
        .then().assertThat().statusCode(200).and()
        .body("macroTypeCode", equalTo("MOREINFO"));
  }
}
