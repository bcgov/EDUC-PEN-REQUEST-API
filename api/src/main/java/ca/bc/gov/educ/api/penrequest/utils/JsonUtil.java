package ca.bc.gov.educ.api.penrequest.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtil {
  private static final ObjectMapper mapper = new ObjectMapper();
  private JsonUtil(){
  }
  public static String getJsonStringFromObject(Object payload) throws JsonProcessingException {
    return mapper.writeValueAsString(payload);
  }

  public static <T> T getJsonObjectFromString(Class<T> clazz,  String payload) throws JsonProcessingException {
    return mapper.readValue(payload,clazz);
  }

  public static byte[] getJsonSBytesFromObject(Object payload) throws JsonProcessingException {
    return mapper.writeValueAsBytes(payload);
  }

  public static <T> T getJsonObjectFromBytes(Class<T> clazz,  byte[] payload) throws IOException {
    return mapper.readValue(payload,clazz);
  }
}
