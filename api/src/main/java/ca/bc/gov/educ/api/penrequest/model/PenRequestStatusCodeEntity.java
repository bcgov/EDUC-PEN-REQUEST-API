package ca.bc.gov.educ.api.penrequest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pen_retrieval_request_status_code")
public class PenRequestStatusCodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pen_retrieval_request_status_code", unique = true, updatable = false)
    String penRequestStatusCode;

    @NotNull(message = "label cannot be null")
    @Column(name = "label")
    String label;

    @NotNull(message = "description cannot be null")
    @Column(name = "description")
    String description;

    @NotNull(message = "displayOrder cannot be null")
    @Column(name = "display_order")
    Integer displayOrder;

    @NotNull(message = "effectiveDate cannot be null")
    @Column(name = "effective_date")
    Date effectiveDate;

    @NotNull(message = "expiryDate cannot be null")
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
