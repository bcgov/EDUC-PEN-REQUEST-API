package ca.bc.gov.educ.api.penrequest.struct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PenRequestComments implements Serializable {
  private static final long serialVersionUID = -6904836038828419985L;
  
  String penRetrievalReqCommentID;
  String penRetrievalRequestID;
  String staffMemberIDIRGUID;
  String staffMemberName;
  @NotNull(message = "Comment content can not be null")
  String commentContent;
  Date commentTimestamp;
}
