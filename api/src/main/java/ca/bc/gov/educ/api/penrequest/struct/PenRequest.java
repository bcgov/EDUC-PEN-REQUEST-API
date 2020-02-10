package ca.bc.gov.educ.api.penrequest.struct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PenRequest implements Serializable {
  private static final long serialVersionUID = 583620260139143932L;

  private String penRequestID;
  @NotNull(message = "digitalID cannot be null")
  private String digitalID;
  private String penRequestStatusCode;
  private String legalFirstName;
  private String legalMiddleNames;
  @NotNull(message = "legalLastName cannot be null")
  private String legalLastName;
  private Date dob;
  private String genderCode;
  private String dataSourceCode;
  private String usualFirstName;
  private String usualMiddleName;
  private String usualLastName;
  private String email;
  private String maidenName;
  private String pastNames;
  private String lastBCSchool;
  private String lastBCSchoolStudentNumber;
  private String currentSchool;
  private String reviewer;
  private String failureReason;
  private Date initialSubmitDate;
  private Date statusUpdateDate;
}
