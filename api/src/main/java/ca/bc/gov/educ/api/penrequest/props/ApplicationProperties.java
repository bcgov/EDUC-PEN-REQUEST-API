package ca.bc.gov.educ.api.penrequest.props;

import java.util.List;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {
  public static final String CLIENT_ID = "PEN-REQUEST-API";

  @Getter
  @Value("${file.maxsize}")
  private int maxFileSize;
  @Value("${file.extensions}")
  @Getter
  private List<String> fileExtensions;

}
