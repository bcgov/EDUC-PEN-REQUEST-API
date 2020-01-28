package ca.bc.gov.educ.api.penrequest.struct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PenRequestStatusCode {

    String penRequestStatusCode;

    @NotNull(message = "label cannot be null")
    String label;

    @NotNull(message = "description cannot be null")
    String description;

    @NotNull(message = "displayOrder cannot be null")
    Integer displayOrder;

    @NotNull(message = "effectiveDate cannot be null")
    Date effectiveDate;

    @NotNull(message = "expiryDate cannot be null")
    Date expiryDate;
}
