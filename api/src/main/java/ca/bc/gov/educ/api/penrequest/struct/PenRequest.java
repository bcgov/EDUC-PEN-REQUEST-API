package ca.bc.gov.educ.api.penrequest.struct;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
  private Date dob;
  @NotNull(message = "genderCode cannot be null")
  @Size(max = 1)
  private String genderCode;
  @NotNull(message = "dataSourceCode cannot be null")
  @Size(max = 10)
  private String dataSourceCode;
  @Size(max = 40)
  private String usualFirstName;
  @Size(max = 255)
  private String usualMiddleName;
  @Size(max = 40)
  private String usualLastName;
  @NotNull(message = "dataSourceCode cannot be null")
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
  private Date initialSubmitDate;
  private Date statusUpdateDate;
}
