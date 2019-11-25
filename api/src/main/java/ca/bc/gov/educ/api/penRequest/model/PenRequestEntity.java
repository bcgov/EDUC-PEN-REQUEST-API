package ca.bc.gov.educ.api.penRequest.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import java.util.Date;

@Entity
@Data
@Getter
@Setter
@Table(name = "PEN_Retrieval_Request")
public class PenRequestEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @NotNull(message="penRequestID cannot be null")
    @Column(name = "PEN_Retrieval_Request_ID", unique = true, updatable = false)
    Integer penRequestID;

    @NotNull(message="digitalID cannot be null")
    @Column(name = "Digital_Identity_ID")
    Integer digitalID;

    @Column(name = "PEN_Retrieval_Request_Status_Code")
    String penRequestStatusCode;

    @Column(name = "Legal_First_Name")
    String legalFirstName;

    @Column(name = "Legal_Middle_Names")
    String legalMiddleNames;

    @Column(name = "Legal_Last_Name")
    String legalLastName;

    @Column(name = "DOB")
    Date dob;

    @Column(name = "Gender_Code")
    String genderCode;

    @Column(name = "Data_Source_Code")
    String dataSourceCode;

    @Column(name = "Usual_First_Name")
    String usualFirstName;

    @Column(name = "Usual_Middle_Names")
    String usualMiddleName;

    @Column(name = "Usual_Last_Name")
    String usualLastName;

    @Column(name = "Email")
    String email;

    @Column(name = "Maiden_Name")
    String maidenName;

    @Column(name = "Past_Names")
    String pastNames;

    @Column(name = "Last_BC_School")
    String lastBCSchool;

    @Column(name = "Last_BC_School_Student_Number")
    String lastBCSchoolStudentNumber;

    @Column(name = "Current_School")
    String currentSchool;

    @Column(name = "Reciever")
    String receiver;

    @NotNull(message="createUser cannot be null")
    @Column(name = "create_user", updatable=false)
    String createUser;

    @NotNull(message="createDate cannot be null")
    @PastOrPresent
    @Column(name = "create_date", updatable=false)
    Date createDate;

    @Column(name = "update_user")
    String updateUser;

    @PastOrPresent
    @Column(name = "update_date")
    Date updateDate;
}