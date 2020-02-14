package ca.bc.gov.educ.api.penrequest.struct;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class PenRequestComments extends BaseRequest implements Serializable {
  private static final long serialVersionUID = -6904836038828419985L;

  String penRetrievalReqCommentID;
  String penRetrievalRequestID;
  @Size(max = 50)
  String staffMemberIDIRGUID;
  @Size(max = 255)
  String staffMemberName;
  @NotNull(message = "Comment content can not be null")
  String commentContent;
  Date commentTimestamp;
}
