package ca.bc.gov.educ.api.penrequest.mappers;

import ca.bc.gov.educ.api.penrequest.model.DocumentEntity;
import ca.bc.gov.educ.api.penrequest.struct.Document;
import ca.bc.gov.educ.api.penrequest.struct.DocumentMetadata;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { UUIDMapper.class, Base64Mapper.class })
public interface DocumentMapper {

    DocumentMapper mapper = Mappers.getMapper(DocumentMapper.class);

    Document toStructure(DocumentEntity entity);

    DocumentEntity toModel(Document struct);

    DocumentMetadata toMetadataStructure(DocumentEntity struct);
}
