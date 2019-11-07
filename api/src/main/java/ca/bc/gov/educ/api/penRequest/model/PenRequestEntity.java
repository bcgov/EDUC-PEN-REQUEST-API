package ca.bc.gov.educ.api.penRequest.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import java.util.Date;

@Entity
@Data
public class PenRequestEntity {
    @Id
    @NotNull(message="penRequestID cannot be null")
    Integer penRequestID;
    @NotNull(message="digitalID cannot be null")
    Integer digitalID;
    String penRequestStatusCode;
    String legalFirstName;
    String legalMiddleNames;
    String legalLastName;
    Date dob;
    String sexCode;
    String genderCode;
    String dataSourceCode;
    String usualFirstName;
    String usualMiddleName;
    String usualLastName;
    String email;
    String maidenName;
    String pastNames;
    String lastBCSchool;
    String lastBCSchoolStudentNumber;
    String currentSchool;
    String addressLine1;
    String addressLine2;
    String city;
    String provinceCode;
    String countryCode;
    String postalCode;
    String createUser;
    @PastOrPresent
    Date createDate;
    StringUpdateUser;
    @PastOrPresent
    Date updateDate;
}