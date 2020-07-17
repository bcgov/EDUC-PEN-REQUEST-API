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

import ca.bc.gov.educ.api.penrequest.constants.PenRequestStatusCode;
import ca.bc.gov.educ.api.penrequest.filter.FilterOperation;
import ca.bc.gov.educ.api.penrequest.props.IntegrationTestProperties;
import ca.bc.gov.educ.api.penrequest.struct.PenRequest;
import ca.bc.gov.educ.api.penrequest.struct.SearchCriteria;
import ca.bc.gov.educ.api.penrequest.struct.ValueType;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.*;

// import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = IntegrationTestProperties.class)
public class PenRequestITCase {
  @Autowired
  IntegrationTestProperties properties;

  private String token;

  @Before
  public void setUp() {
    token = TestUtils.getAccessToken(properties);
  }

  @After
  public void after() {
    TestUtils.deletePenRequestByDigitalId(token, "b1e0788a-7dab-4b92-af86-c678e411f1e3", properties);
  }

  @Test
  public void testPenRequestCrud() {
    assertThat(token).isNotNull();
    var response = given().auth().oauth2(token).contentType(ContentType.JSON).body(dummyPenRequestJson()).when()
        .post(properties.getApiUrl());

    response.then().assertThat().statusCode(201).and().body("legalFirstName", equalTo("Chester")).and()
        .body("penRequestID", any(String.class));

    given().auth().oauth2(token).when().get(properties.getApiUrl() + "?digitalID=b1e0788a-7dab-4b92-af86-c678e411f1e3")
        .then().assertThat().statusCode(200).and().body("size()", equalTo(1));

    var penRequest = response.as(PenRequest.class);
    penRequest.setPenRequestStatusCode(PenRequestStatusCode.MANUAL.toString());

    given().auth().oauth2(token).contentType(ContentType.JSON).body(penRequest).when().put(properties.getApiUrl())
        .then().assertThat().statusCode(200).and()
        .body("penRequestStatusCode", equalTo(PenRequestStatusCode.MANUAL.toString()));

    given().auth().oauth2(token).when().get(properties.getApiUrl() + "/" + response.path("penRequestID")).then()
        .assertThat().statusCode(200).and().body("legalFirstName", equalTo("Chester")).and()
        .body("penRequestStatusCode", equalTo(PenRequestStatusCode.MANUAL.toString()));

    given().auth().oauth2(token).when().delete(properties.getApiUrl() + "/" + response.path("penRequestID")).then()
        .assertThat().statusCode(204);

    given().auth().oauth2(token).when().get(properties.getApiUrl() + "/" + response.path("penRequestID")).then()
        .assertThat().statusCode(404);
  }

  @Test
  public void testReadPenRequestCodes() {
    given().auth().oauth2(token).when().get(properties.getApiUrl() + "/statuses").then().assertThat().statusCode(200)
        .and().body("size()", is(not(lessThan(1))));

    given().auth().oauth2(token).when().get(properties.getApiUrl() + "/gender-codes").then().assertThat()
      .statusCode(200).and().body("size()", is(not(lessThan(1))));
  }

  @Test
  public void testReadPenRequestPaginated() throws JsonProcessingException {
    IntStream.range(1, 3).
      forEach(i -> given().auth().oauth2(token).contentType(ContentType.JSON).
        body(dummyPenRequestJson()).
        when().post(properties.getApiUrl()).
        then().assertThat().statusCode(201));
    
    SearchCriteria criteria = SearchCriteria.builder().key("digitalID").operation(FilterOperation.EQUAL).value("b1e0788a-7dab-4b92-af86-c678e411f1e3").valueType(ValueType.UUID).build();
    List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    var objectMapper = new ObjectMapper();
    var criteriaJSON = objectMapper.writeValueAsString(criteriaList);

    given().auth().oauth2(token).contentType(ContentType.URLENC.withCharset("UTF-8"))
      .formParam("searchCriteriaList", criteriaJSON)
      .when().get(properties.getApiUrl() + "/paginated").then().assertThat()
      .statusCode(200).and().body("numberOfElements", equalTo(2));
  }

  protected String dummyPenRequestJson() {
	  return "{\"digitalID\":\"b1e0788a-7dab-4b92-af86-c678e411f1e3\",\"legalFirstName\":\"Chester\",\"legalMiddleNames\":\"Grestie\",\"legalLastName\":\"Baulk\",\"dob\":\"1952-10-31\",\"genderCode\":\"M\",\"email\":\"cbaulk0@bluehost.com\",\"emailVerified\":\"N\",\"currentSchool\":\"Xanthoparmelia wyomingica (Gyel.) Hale\",\"pen\":\"127054021\"}";
  }
}
