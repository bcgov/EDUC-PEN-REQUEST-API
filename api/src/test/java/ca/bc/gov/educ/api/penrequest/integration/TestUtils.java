package ca.bc.gov.educ.api.penrequest.integration;

import ca.bc.gov.educ.api.penrequest.props.IntegrationTestProperties;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

import java.util.ArrayList;

public class TestUtils {
  public static String getAccessToken(IntegrationTestProperties properties) {
    Response response = when().get(properties.getSoamUrl()).then().contentType(ContentType.JSON).extract().response();
    String tokenUrl = response.path("token_endpoint");

    Response tokenResponse = given().contentType(ContentType.URLENC.withCharset("UTF-8"))
        .formParam("grant_type", "client_credentials").formParam("client_id", properties.getSoamClientId())
        .formParam("client_secret", properties.getSoamClientSecret()).when().post(tokenUrl).then()
        .contentType(ContentType.JSON).extract().response();
    return tokenResponse.path("access_token");
  }

  public static void deletePenRequestByDigitalId(String token, String digitalId, IntegrationTestProperties properties) {
    var response = given().auth().oauth2(token).when()
      .get(properties.getApiUrl() + "?digitalID=" + digitalId);

    ArrayList<String> penRequests = response.path("penRequestID");
    penRequests.forEach((penRequestID) -> {
      given().auth().oauth2(token).when().delete(properties.getApiUrl() + "/" + penRequestID);
    });
  }
}