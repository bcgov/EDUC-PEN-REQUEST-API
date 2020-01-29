package ca.bc.gov.educ.api.penrequest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "PEN_RETRIEVAL_REQUEST_COMMENT")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PenRequestCommentsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "PEN_RETRIEVAL_REQUEST_COMMENT_ID", unique = true, updatable = false)
  UUID penRetrievalReqCommentID;
  @Column(name = "PEN_RETRIEVAL_REQUEST_ID")
  UUID penRetrievalRequestID;
  @Column(name = "STAFF_MEMBER_IDIR_GUID")
  String staffMemberIDIRGUID;

  @NotNull(message = "staffMemberName cannot be null")
  @Column(name = "STAFF_MEMBER_NAME")
  String staffMemberName;

  @Column(name = "COMMENT_CONTENT")
  String commentContent;

  @Column(name = "COMMENT_TIMESTAMP")
  Date commentTimestamp;

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

  @ManyToOne(cascade = CascadeType.ALL, optional = false, targetEntity = PenRequestEntity.class)
  @JoinColumn(name = "pen_retrieval_request_id", updatable = false, insertable = false)
  private PenRequestEntity penRequestEntity;
}
