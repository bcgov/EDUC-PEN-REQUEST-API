package ca.bc.gov.educ.api.penrequest.struct.v1;

import ca.bc.gov.educ.api.penrequest.struct.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

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
  String commentTimestamp;
}
