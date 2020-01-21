package ca.bc.gov.educ.api.penrequest.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "pen_retrieval_request")
public class PenRequestEntity {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
			@Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy") })
	@Column(name = "pen_retrieval_request_id", unique = true, updatable = false, columnDefinition = "BINARY(16)")
	UUID penRequestID;

	@NotNull(message = "digitalID cannot be null")
	@Column(name = "digital_identity_id", columnDefinition = "BINARY(16)")
	UUID digitalID;

	@Column(name = "pen_retrieval_request_status_code")
	String penRequestStatusCode;

	@Column(name = "legal_first_name")
	String legalFirstName;

	@Column(name = "legal_middle_names")
	String legalMiddleNames;

	@NotNull(message = "legalLastName cannot be null")
	@Column(name = "legal_last_name")
	String legalLastName;

	@Column(name = "dob")
	Date dob;

	@Column(name = "gender_code")
	String genderCode;

	@Column(name = "data_source_code")
	String dataSourceCode;

	@Column(name = "usual_first_name")
	String usualFirstName;

	@Column(name = "usual_middle_names")
	String usualMiddleName;

	@Column(name = "usual_last_name")
	String usualLastName;

	@Column(name = "email")
	String email;

	@Column(name = "maiden_name")
	String maidenName;

	@Column(name = "past_names")
	String pastNames;

	@Column(name = "last_bc_school")
	String lastBCSchool;

	@Column(name = "last_bc_school_student_number")
	String lastBCSchoolStudentNumber;

	@Column(name = "current_school")
	String currentSchool;

	@Column(name = "reviewer")
	String reviewer;

	@Column(name = "failure_reason")
	String failureReason;

	@PastOrPresent
	@Column(name = "INITIAL_SUBMIT_DATE")
	Date initialSubmitDate;

	@PastOrPresent
	@Column(name = "STATUS_UPDATE_DATE")
	Date statusUpdateDate;

	@Column(name = "create_user", updatable = false)
	String createUser;

	@PastOrPresent
	@Column(name = "create_date", updatable = false)
	Date createDate;

	@Column(name = "update_user")
	String updateUser;

	@PastOrPresent
	@Column(name = "update_date")
	Date updateDate;

	public UUID getPenRequestID() {
		return penRequestID;
	}

	public void setPenRequestID(UUID penRequestID) {
		this.penRequestID = penRequestID;
	}

	public UUID getDigitalID() {
		return digitalID;
	}

	public void setDigitalID(UUID digitalID) {
		this.digitalID = digitalID;
	}

	public String getPenRequestStatusCode() {
		return penRequestStatusCode;
	}

	public void setPenRequestStatusCode(String penRequestStatusCode) {
		this.penRequestStatusCode = penRequestStatusCode;
	}

	public String getLegalFirstName() {
		return legalFirstName;
	}

	public void setLegalFirstName(String legalFirstName) {
		this.legalFirstName = legalFirstName;
	}

	public String getLegalMiddleNames() {
		return legalMiddleNames;
	}

	public void setLegalMiddleNames(String legalMiddleNames) {
		this.legalMiddleNames = legalMiddleNames;
	}

	public String getLegalLastName() {
		return legalLastName;
	}

	public void setLegalLastName(String legalLastName) {
		this.legalLastName = legalLastName;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getGenderCode() {
		return genderCode;
	}

	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}

	public String getDataSourceCode() {
		return dataSourceCode;
	}

	public void setDataSourceCode(String dataSourceCode) {
		this.dataSourceCode = dataSourceCode;
	}

	public String getUsualFirstName() {
		return usualFirstName;
	}

	public void setUsualFirstName(String usualFirstName) {
		this.usualFirstName = usualFirstName;
	}

	public String getUsualMiddleName() {
		return usualMiddleName;
	}

	public void setUsualMiddleName(String usualMiddleName) {
		this.usualMiddleName = usualMiddleName;
	}

	public String getUsualLastName() {
		return usualLastName;
	}

	public void setUsualLastName(String usualLastName) {
		this.usualLastName = usualLastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMaidenName() {
		return maidenName;
	}

	public void setMaidenName(String maidenName) {
		this.maidenName = maidenName;
	}

	public String getPastNames() {
		return pastNames;
	}

	public void setPastNames(String pastNames) {
		this.pastNames = pastNames;
	}

	public String getLastBCSchool() {
		return lastBCSchool;
	}

	public void setLastBCSchool(String lastBCSchool) {
		this.lastBCSchool = lastBCSchool;
	}

	public String getLastBCSchoolStudentNumber() {
		return lastBCSchoolStudentNumber;
	}

	public void setLastBCSchoolStudentNumber(String lastBCSchoolStudentNumber) {
		this.lastBCSchoolStudentNumber = lastBCSchoolStudentNumber;
	}

	public String getCurrentSchool() {
		return currentSchool;
	}

	public void setCurrentSchool(String currentSchool) {
		this.currentSchool = currentSchool;
	}

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}
	
	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public Date getStatusUpdateDate() { return statusUpdateDate; }

	public void setStatusUpdateDate(Date statusUpdateDate) { this.statusUpdateDate = statusUpdateDate; }

	public Date getInitialSubmitDate() { return initialSubmitDate; }

	public void setInitialSubmitDate(Date initialSubmitDate) { this.initialSubmitDate = initialSubmitDate; }

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

}