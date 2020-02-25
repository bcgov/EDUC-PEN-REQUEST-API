package ca.bc.gov.educ.api.penrequest.mappers;

import ca.bc.gov.educ.api.penrequest.model.DocumentTypeCodeEntity;
import ca.bc.gov.educ.api.penrequest.struct.PenReqDocTypeCode;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UUIDMapper.class)
@SuppressWarnings("squid:S1214")
public interface DocumentTypeCodeMapper {

    DocumentTypeCodeMapper mapper = Mappers.getMapper(DocumentTypeCodeMapper.class);

    PenReqDocTypeCode toStructure(DocumentTypeCodeEntity entity);

    @Mapping(target = "updateUser", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "createUser", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    DocumentTypeCodeEntity toModel(PenReqDocTypeCode struct);

}
