package ca.bc.gov.educ.api.penrequest.mappers.v1;

import ca.bc.gov.educ.api.penrequest.mappers.LocalDateTimeMapper;
import ca.bc.gov.educ.api.penrequest.mappers.UUIDMapper;
import ca.bc.gov.educ.api.penrequest.model.v1.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.struct.v1.PenRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UUIDMapper.class, LocalDateTimeMapper.class})
@SuppressWarnings("squid:S1214")
public interface PenRequestEntityMapper {

  PenRequestEntityMapper mapper = Mappers.getMapper(PenRequestEntityMapper.class);

  @Mapping(target = "createDate", ignore = true)
  @Mapping(target = "updateDate", ignore = true)
  PenRequest toStructure(PenRequestEntity entity);

  @Mapping(target = "penRequestComments", ignore = true)
  PenRequestEntity toModel(PenRequest struct);
}
