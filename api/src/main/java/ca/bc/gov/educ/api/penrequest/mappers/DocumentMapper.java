package ca.bc.gov.educ.api.penrequest.mappers;

import ca.bc.gov.educ.api.penrequest.model.DocumentEntity;
import ca.bc.gov.educ.api.penrequest.struct.PenReqDocMetadata;
import ca.bc.gov.educ.api.penrequest.struct.PenReqDocument;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { UUIDMapper.class, Base64Mapper.class })
@SuppressWarnings("squid:S1214")
public interface DocumentMapper {

    DocumentMapper mapper = Mappers.getMapper(DocumentMapper.class);

    PenReqDocument toStructure(DocumentEntity entity);

    DocumentEntity toModel(PenReqDocument struct);

    PenReqDocMetadata toMetadataStructure(DocumentEntity struct);
}
