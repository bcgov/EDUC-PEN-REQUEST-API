package ca.bc.gov.educ.api.penrequest.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import ca.bc.gov.educ.api.penrequest.props.IntegrationTestProperties;
import ca.bc.gov.educ.api.penrequest.struct.v1.PenRequest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.*;

import java.io.IOException;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = IntegrationTestProperties.class)
public class PenRequestCommentITCase {
  @Autowired
  IntegrationTestProperties properties;

  private String token;
  private PenRequest penRequest;

  @Before
  public void setUp() {
    token = TestUtils.getAccessToken(properties);
    var response = given().auth().oauth2(token).contentType(ContentType.JSON).body(dummyPenRequestJson()).when()
        .post(properties.getApiUrl());
    penRequest = response.as(PenRequest.class);
  }

  @After
  public void after() {
    TestUtils.deletePenRequestByDigitalId(token, "b1e0788a-7dab-4b92-af86-c678e411f1e3", properties);
  }

  @Test
  public void testPenRequestCommentCrud() throws IOException {
    assertThat(token).isNotNull();
    var response = given().auth().oauth2(token).contentType(ContentType.JSON).body(dummyPenRequestCommentsJsonWithValidPenReqID(penRequest.getPenRequestID()))
        .when().post(properties.getApiUrl() + "/" + penRequest.getPenRequestID() + "/comments");

    response.then().assertThat().statusCode(201).and()
        .body("penRetrievalReqCommentID", any(String.class));

    given().auth().oauth2(token).contentType(ContentType.JSON).body(dummyPenRequestCommentsJsonWithValidPenReqID(penRequest.getPenRequestID()))
        .when().post(properties.getApiUrl() + "/" + penRequest.getPenRequestID() + "/comments");

    given().auth().oauth2(token)
        .when().get(properties.getApiUrl()+ "/" + penRequest.getPenRequestID() + "/comments")
        .then().assertThat().statusCode(200).and()
        .body("size()", equalTo(2));
  }

  protected String dummyPenRequestJson() {
    return "{\"digitalID\":\"b1e0788a-7dab-4b92-af86-c678e411f1e3\",\"legalFirstName\":\"Chester\",\"legalMiddleNames\":\"Grestie\",\"legalLastName\":\"Baulk\",\"dob\":\"1952-10-31\",\"genderCode\":\"M\",\"email\":\"cbaulk0@bluehost.com\",\"emailVerified\":\"N\",\"currentSchool\":\"Xanthoparmelia wyomingica (Gyel.) Hale\",\"pen\":\"127054021\"}";
  }

  private String dummyPenRequestCommentsJsonWithValidPenReqID(String penReqId) {
    return "{\n" +
            "  \"penRetrievalRequestID\": \"" + penReqId + "\",\n" +
            "  \"commentContent\": \"" + "comment1" + "\",\n" +
            "  \"commentTimestamp\": \"2020-02-09T00:00:00\"\n" +
            "}";
  }
}
