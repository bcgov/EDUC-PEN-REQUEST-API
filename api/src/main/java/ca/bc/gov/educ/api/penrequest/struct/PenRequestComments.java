package ca.bc.gov.educ.api.penrequest.struct;

import lombok.*;

import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PenRequestComments extends BaseRequest implements Serializable {
  private static final long serialVersionUID = -6904836038828419985L;

  String penRetrievalReqCommentID;
  String penRetrievalRequestID;
  String staffMemberIDIRGUID;
  String staffMemberName;
  @NotNull(message = "Comment content can not be null")
  String commentContent;
  Date commentTimestamp;
}
