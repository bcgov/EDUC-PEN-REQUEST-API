package ca.bc.gov.educ.api.penrequest.struct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PenRequestComments {
  String penRetrievalReqCommentID;
  String penRetrievalRequestID;
  String staffMemberIDIRGUID;
  String staffMemberName;
  @NotNull(message = "Comment content can not be null")
  String commentContent;
  Date commentTimestamp;
}
