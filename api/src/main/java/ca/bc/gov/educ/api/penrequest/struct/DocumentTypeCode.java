package ca.bc.gov.educ.api.penrequest.struct;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentTypeCode implements Serializable {

    private static final long serialVersionUID = 6118916290604876032L;

    private String documentTypeCode;
    
    private String label;
    
    private String description;

    private Integer displayOrder;

    private Date effectiveDate;
    
    private Date expiryDate;
}
