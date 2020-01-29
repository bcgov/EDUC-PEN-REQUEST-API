package ca.bc.gov.educ.api.penrequest.mappers;

import ca.bc.gov.educ.api.penrequest.model.DocumentTypeCodeEntity;
import ca.bc.gov.educ.api.penrequest.struct.DocumentTypeCode;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UUIDMapper.class)
public interface DocumentTypeCodeMapper {

    DocumentTypeCodeMapper mapper = Mappers.getMapper(DocumentTypeCodeMapper.class);

    DocumentTypeCode toStructure(DocumentTypeCodeEntity entity);

    DocumentTypeCodeEntity toModel(DocumentTypeCode struct);

}
