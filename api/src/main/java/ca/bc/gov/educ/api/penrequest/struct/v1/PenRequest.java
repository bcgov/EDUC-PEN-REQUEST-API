package ca.bc.gov.educ.api.penrequest.struct.v1;

import ca.bc.gov.educ.api.penrequest.struct.BaseRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PenRequest extends BaseRequest implements Serializable {
  private static final long serialVersionUID = 583620260139143932L;

  private String penRequestID;
  @NotNull(message = "digitalID cannot be null")
  private String digitalID;
  @Size(max = 10)
  private String penRequestStatusCode;
  @Size(max = 40)
  private String legalFirstName;
  @Size(max = 255)
  private String legalMiddleNames;
  @NotNull(message = "legalLastName cannot be null")
  @Size(max = 40)
  private String legalLastName;
  @NotNull(message = "dob cannot be null")
  private String dob;
  @Size(max = 1)
  private String genderCode;
  @Size(max = 40)
  private String usualFirstName;
  @Size(max = 255)
  private String usualMiddleName;
  @Size(max = 40)
  private String usualLastName;
  @NotNull(message = "email cannot be null")
  @Size(max = 255)
  private String email;
  @Size(max = 40)
  private String maidenName;
  @Size(max = 255)
  private String pastNames;
  @Size(max = 255)
  private String lastBCSchool;
  @Size(max = 12)
  private String lastBCSchoolStudentNumber;
  @Size(max = 255)
  private String currentSchool;
  @Size(max = 255)
  private String reviewer;
  private String failureReason;
  private String initialSubmitDate;
  private String statusUpdateDate;
  @Size(max = 1)
  @Pattern(regexp = "[YN]")
  @NotNull(message = "emailVerified cannot be null")
  private String emailVerified;
  @Size(max = 255)
  private String bcscAutoMatchOutcome;
  @Size(max = 255)
  private String bcscAutoMatchDetails;
  @Size(max = 9)
  private String pen;
  @Size(max = 1)
  @Pattern(regexp = "[YN]")
  private String demogChanged;
  private String completeComment;
}
