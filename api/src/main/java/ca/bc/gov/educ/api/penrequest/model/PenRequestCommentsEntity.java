package ca.bc.gov.educ.api.penrequest.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "PEN_RETRIEVAL_REQUEST_COMMENT")
@Getter
@Setter
public class PenRequestCommentsEntity {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
          @Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy")})
  @Column(name = "PEN_RETRIEVAL_REQUEST_COMMENT_ID", unique = true, updatable = false, columnDefinition = "BINARY(16)")
  UUID penRetrievalReqCommentID;
  @Column(name = "PEN_RETRIEVAL_REQUEST_ID")
  UUID penRetrievalRequestID;
  @Column(name = "STAFF_MEMBER_IDIR_GUID")
  String staffMemberIDIRGUID;

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
  @JoinColumn(name = "pen_retrieval_request_id", referencedColumnName = "pen_retrieval_request_id", updatable = false, insertable = false)
  private PenRequestEntity penRequestEntity;

}
