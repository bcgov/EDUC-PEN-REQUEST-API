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
    @NotNull(message = "Staff Member Name cannot be null")
    String staffMemberName;
    String commentContent;
    Date commentTimestamp;
    @NotNull(message = "expiryDate cannot be null")
    Date expiryDate;
}
