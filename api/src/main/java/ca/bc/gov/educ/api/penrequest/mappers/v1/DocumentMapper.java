package ca.bc.gov.educ.api.penrequest.mappers.v1;

import ca.bc.gov.educ.api.penrequest.mappers.Base64Mapper;
import ca.bc.gov.educ.api.penrequest.mappers.LocalDateTimeMapper;
import ca.bc.gov.educ.api.penrequest.mappers.UUIDMapper;
import ca.bc.gov.educ.api.penrequest.model.v1.DocumentEntity;
import ca.bc.gov.educ.api.penrequest.struct.v1.PenReqDocMetadata;
import ca.bc.gov.educ.api.penrequest.struct.v1.PenReqDocument;
import ca.bc.gov.educ.api.penrequest.struct.v1.PenReqDocumentMetadata;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UUIDMapper.class, Base64Mapper.class, LocalDateTimeMapper.class})
@SuppressWarnings("squid:S1214")
public interface DocumentMapper {

  DocumentMapper mapper = Mappers.getMapper(DocumentMapper.class);

  PenReqDocument toStructure(DocumentEntity entity);

  @Mapping(target = "penRequest", ignore = true)
  DocumentEntity toModel(PenReqDocument struct);

  PenReqDocMetadata toMetadataStructure(DocumentEntity entity);

  @Mapping(target = "digitalID", source = "penRequest.digitalID")
  @Mapping(target = "penRequestID", source = "penRequest.penRequestID")
  PenReqDocumentMetadata toMetaData(DocumentEntity entity);
}
