package ca.bc.gov.educ.api.penrequest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;

@Entity
@Table(name = "pen_retrieval_request_status_code")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PenRequestCommentsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PEN_RETRIEVAL_REQUEST_COMMENT_ID", unique = true, updatable = false)
	String penRequestStatusCode;

	@Column(name = "STAFF_MEMBER_IDIR_GUID")
	String staffMemberIdirGuid;

	@NotNull(message = "staffMemberName cannot be null")
	@Column(name = "STAFF_MEMBER_NAME")
	String staffMemberName;

	@Column(name = "COMMENT_CONTENT")
	String commentContent;

	@Column(name = "COMMENT_TIMESTAMP")
	Date commentTimestamp;

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
}
