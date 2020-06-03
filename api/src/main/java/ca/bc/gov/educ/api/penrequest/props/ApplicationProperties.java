package ca.bc.gov.educ.api.penrequest.props;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

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

  /**
   * This property value will indicate how many days a pen request can be in DRAFT status before eligible to move to STALE state.
   */

  @Value("${days.status.draft.penrequest}")
  @Getter
  private Long numOfDaysInDraftStatusForStale;

}
