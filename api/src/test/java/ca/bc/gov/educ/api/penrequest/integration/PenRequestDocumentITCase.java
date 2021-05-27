package ca.bc.gov.educ.api.penrequest.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import ca.bc.gov.educ.api.penrequest.props.IntegrationTestProperties;
import ca.bc.gov.educ.api.penrequest.struct.v1.PenReqDocMetadata;
import ca.bc.gov.educ.api.penrequest.struct.v1.PenRequest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.nio.file.Files;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = IntegrationTestProperties.class)
public class PenRequestDocumentITCase {
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
  public void testPenRequestDocumentCrud() throws IOException {
    assertThat(token).isNotNull();
    var response = given().auth().oauth2(token).contentType(ContentType.JSON).body(getDocumentRequestJson()).when()
        .post(properties.getApiUrl() + "/" + penRequest.getPenRequestID() + "/documents");

    response.then().assertThat().statusCode(201).and()
        .body("documentID", any(String.class));

    var penRequestDocument = response.as(PenReqDocMetadata.class);
    penRequestDocument.setDocumentTypeCode("CAPASSPORT");
    penRequestDocument.setCreateDate(null);

    given().auth().oauth2(token).contentType(ContentType.JSON).body(penRequestDocument)
        .when().put(properties.getApiUrl()+ "/" + penRequest.getPenRequestID() + "/documents/" + penRequestDocument.getDocumentID())
        .then().assertThat().statusCode(200).and()
        .body("documentTypeCode", equalTo("CAPASSPORT"));

    given().auth().oauth2(token).param("includeDocData", "Y")
        .when().get(properties.getApiUrl()+ "/" + penRequest.getPenRequestID() + "/documents/" + penRequestDocument.getDocumentID())
        .then().assertThat().statusCode(200).and()
        .body("documentTypeCode", equalTo("CAPASSPORT")).and()
        .body("documentData", equalTo("TXkgY2FyZCE="));

    given().auth().oauth2(token).contentType(ContentType.JSON).body(getDocumentRequestJson()).when()
        .post(properties.getApiUrl() + "/" + penRequest.getPenRequestID() + "/documents")
        .then().assertThat().statusCode(201).and()
        .body("documentID", any(String.class));

    given().auth().oauth2(token)
        .when().get(properties.getApiUrl()+ "/" + penRequest.getPenRequestID() + "/documents")
        .then().assertThat().statusCode(200).and()
        .body("size()", equalTo(2));

    given().auth().oauth2(token)
        .when().delete(properties.getApiUrl()+ "/" + penRequest.getPenRequestID() + "/documents/" + penRequestDocument.getDocumentID())
        .then().assertThat().statusCode(200);

    given().auth().oauth2(token)
        .when().get(properties.getApiUrl()+ "/" + penRequest.getPenRequestID() + "/documents/" + penRequestDocument.getDocumentID())
        .then().assertThat().statusCode(404);
  }

  @Test
  public void testReadPenRequestDocumentCodes() {
    given().auth().oauth2(token).when().get(properties.getApiUrl() + "/file-requirements")
        .then().assertThat().statusCode(200)
        .and().body("maxSize", is(not(lessThan(1))))
        .and().body("extensions.size()", is(not(lessThan(1))));

    given().auth().oauth2(token).when().get(properties.getApiUrl() + "/document-types")
        .then().assertThat().statusCode(200)
        .and().body("size()", is(not(lessThan(1))));
  }

  protected String dummyPenRequestJson() {
    return "{\"digitalID\":\"b1e0788a-7dab-4b92-af86-c678e411f1e3\",\"legalFirstName\":\"Chester\",\"legalMiddleNames\":\"Grestie\",\"legalLastName\":\"Baulk\",\"dob\":\"1952-10-31\",\"genderCode\":\"M\",\"email\":\"cbaulk0@bluehost.com\",\"emailVerified\":\"N\",\"currentSchool\":\"Xanthoparmelia wyomingica (Gyel.) Hale\",\"pen\":\"127054021\"}";
  }

  protected String getDocumentRequestJson() throws IOException {
    return Files.readString(new ClassPathResource(
                    "../model/document-req.json", PenRequestDocumentITCase.class).getFile().toPath());
  }
}
