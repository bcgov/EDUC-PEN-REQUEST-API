package ca.bc.gov.educ.api.support;

import java.util.Date;

import ca.bc.gov.educ.api.penrequest.model.DocumentTypeCodeEntity;
import ca.bc.gov.educ.api.penrequest.repository.DocumentTypeCodeTableRepository;

public class DocumentTypeCodeBuilder {

    String documentTypeCode;
    
    String label = "label";
    
    String description = "description";

    Integer displayOrder = 1;

    Date effectiveDate = new Date();
    
    Date expiryDate = new Date();

    String createUser = "API";

    Date createDate = new Date();

    String updateUser = "API";

    Date updateDate = new Date();

    public DocumentTypeCodeBuilder withDocumentTypeCode(String documentTypeCode) {
        this.documentTypeCode = documentTypeCode;
        return this;
    }

    public DocumentTypeCodeBuilder withLabel(String label) {
        this.label = label;
        return this;
    }

    public DocumentTypeCodeBuilder withoutCreateAndUpdateUser() {
        this.createUser = null;
        this.createDate = null;
        this.updateUser = null;
        this.updateDate = null;
        return this;
    }

    public DocumentTypeCodeEntity build() {
        DocumentTypeCodeEntity typeCode = new DocumentTypeCodeEntity();
        typeCode.setCreateUser(this.createUser);
        typeCode.setCreateDate(this.createDate);
        typeCode.setUpdateUser(this.updateUser);
        typeCode.setUpdateDate(this.updateDate);

        typeCode.setDocumentTypeCode(this.documentTypeCode);
        typeCode.setLabel(this.label);
        typeCode.setDescription(this.description);
        typeCode.setDisplayOrder(this.displayOrder);
        typeCode.setEffectiveDate(this.effectiveDate);
        typeCode.setExpiryDate(this.expiryDate);

        return typeCode;
    }

    public static void setUpDocumentTypeCodes(DocumentTypeCodeTableRepository documentTypeCodeRepository) {
        DocumentTypeCodeEntity passport = new DocumentTypeCodeBuilder()
                                            .withDocumentTypeCode("CAPASSPORT").build();
        DocumentTypeCodeEntity bcsc = new DocumentTypeCodeBuilder()
                                        .withDocumentTypeCode("BCSCPHOTO").build();
        documentTypeCodeRepository.save(passport);
        documentTypeCodeRepository.save(bcsc);
    }
}