package ca.bc.gov.educ.api.penrequest.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.PastOrPresent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pen_retrieval_request_document_type_code")
public class DocumentTypeCodeEntity {

    @Id
    @Column(name = "pen_retrieval_request_document_type_code", unique = true, updatable = false)
    String documentTypeCode;
    
    @Column(name = "label")
    String label;
    
    @Column(name = "description")
    String description;

    @Column(name = "display_order")
    Integer displayOrder;

    @Column(name = "effective_date")
    Date effectiveDate;
    
    @Column(name = "expiry_date")
    Date expiryDate;

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
}
