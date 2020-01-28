package ca.bc.gov.educ.api.penrequest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "pen_retrieval_request")
public class PenRequestEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
            @Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy")})
    @Column(name = "pen_retrieval_request_id", unique = true, updatable = false, columnDefinition = "BINARY(16)")
    UUID penRequestID;

    @NotNull(message = "digitalID cannot be null")
    @Column(name = "digital_identity_id", columnDefinition = "BINARY(16)")
    UUID digitalID;

    @Column(name = "pen_retrieval_request_status_code")
    String penRequestStatusCode;

    @Column(name = "legal_first_name")
    String legalFirstName;

    @Column(name = "legal_middle_names")
    String legalMiddleNames;

    @NotNull(message = "legalLastName cannot be null")
    @Column(name = "legal_last_name")
    String legalLastName;

    @Column(name = "dob")
    Date dob;

    @Column(name = "gender_code")
    String genderCode;

    @Column(name = "data_source_code")
    String dataSourceCode;

    @Column(name = "usual_first_name")
    String usualFirstName;

    @Column(name = "usual_middle_names")
    String usualMiddleName;

    @Column(name = "usual_last_name")
    String usualLastName;

    @Column(name = "email")
    String email;

    @Column(name = "maiden_name")
    String maidenName;

    @Column(name = "past_names")
    String pastNames;

    @Column(name = "last_bc_school")
    String lastBCSchool;

    @Column(name = "last_bc_school_student_number")
    String lastBCSchoolStudentNumber;

    @Column(name = "current_school")
    String currentSchool;

    @Column(name = "reviewer")
    String reviewer;

    @Column(name = "failure_reason")
    String failureReason;

    @PastOrPresent
    @Column(name = "INITIAL_SUBMIT_DATE")
    Date initialSubmitDate;

    @PastOrPresent
    @Column(name = "STATUS_UPDATE_DATE")
    Date statusUpdateDate;

    @Column(name = "create_user", updatable = false)
    String createUser;

    @PastOrPresent
    @Column(name = "create_date", updatable = false)
    Date createDate;

    @Column(name = "update_user")
    String updateUser;

    @PastOrPresent
    @Column(name = "update_date")
    Date updateDate;

    @OneToMany(mappedBy = "penRequestEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = PenRequestCommentsEntity.class)
    private Set<PenRequestCommentsEntity> penRequestComments;

}
