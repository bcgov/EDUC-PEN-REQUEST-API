package ca.bc.gov.educ.api.penrequest.props;

import java.util.List;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {
  public static final String CLIENT_ID = "PEN-REQUEST-API";
  public static final String YES = "Y";
  public static final String TRUE = "TRUE";

  @Getter
  @Value("${file.maxsize}")
  private int maxFileSize;
  @Value("${file.extensions}")
  @Getter
  private List<String> fileExtensions;

  @Value("${bcsc.auto.match.outcomes}")
  @Getter
  private List<String> bcscAutoMatchOutcomes;
  @Value("${nats.streaming.server.url}")
  @Getter
  private String natsUrl;

  @Value("${nats.streaming.server.clusterId}")
  @Getter
  private String natsClusterId;

}
