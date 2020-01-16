package ca.bc.gov.educ.api.penrequest.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

@Entity
@Table(name = "pen_retrieval_request_status_code")
public class PenRequestStatusCodeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pen_retrieval_request_status_code", unique = true, updatable = false)
	String penRequestStatusCode;

	@NotNull(message = "label cannot be null")
	@Column(name = "label")
	String label;

	@NotNull(message = "description cannot be null")
	@Column(name = "description")
	String description;

	@NotNull(message = "displayOrder cannot be null")
	@Column(name = "display_order")
	Integer displayOrder;

	@NotNull(message = "effectiveDate cannot be null")
	@Column(name = "effective_date")
	Date effectiveDate;

	@NotNull(message = "expiryDate cannot be null")
	@Column(name = "expiry_date")
	Date expiryDate;

	@Column(name = "create_user", updatable = false)
	String createUser;

	@PastOrPresent
	@Column(name = "create_date", updatable = false)
	Date createDate;

	@Column(name = "update_user", updatable = false)
	String updateUser;

	@PastOrPresent
	@Column(name = "update_date", updatable = false)
	Date updateDate;

	public String getPenRequestStatusCode() {
		return penRequestStatusCode;
	}

	public void setPenRequestStatusCode(String penRequestStatusCode) {
		this.penRequestStatusCode = penRequestStatusCode;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

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
