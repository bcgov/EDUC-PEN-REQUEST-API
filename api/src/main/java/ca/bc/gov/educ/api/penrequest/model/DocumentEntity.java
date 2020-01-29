package ca.bc.gov.educ.api.penrequest.model;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.PastOrPresent;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "pen_retrieval_request_document")
public class DocumentEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID", 
        strategy = "org.hibernate.id.UUIDGenerator", 
        parameters = {
            @Parameter(
                name = "uuid_gen_strategy_class", 
                value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
            ) 
        }
    )
    @Column(name = "pen_retrieval_request_document_id", unique = true, updatable = false, columnDefinition = "BINARY(16)")
    UUID documentID;

    @ManyToOne
    @JoinColumn(name = "pen_retrieval_request_id", updatable = false, columnDefinition = "BINARY(16)")
    PenRequestEntity penRequest;

    @Column(name = "pen_retrieval_request_document_type_code")
    String documentTypeCode;
    
    @Column(name = "file_name")
    String fileName;

    @Column(name = "file_extension")
    String fileExtension;

    @Column(name = "file_size")
    Integer fileSize;

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

    @Basic(fetch = FetchType.LAZY)
    @Lob
    @Column(name = "document_data")
    byte[] documentData;
}
