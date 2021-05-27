package ca.bc.gov.educ.api.penrequest.mappers.v1;

import ca.bc.gov.educ.api.penrequest.mappers.LocalDateTimeMapper;
import ca.bc.gov.educ.api.penrequest.mappers.UUIDMapper;
import ca.bc.gov.educ.api.penrequest.model.v1.PenRequestMacroEntity;
import ca.bc.gov.educ.api.penrequest.struct.v1.PenRequestMacro;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UUIDMapper.class, LocalDateTimeMapper.class})
@SuppressWarnings("squid:S1214")
public interface PenRequestMacroMapper {

  PenRequestMacroMapper mapper = Mappers.getMapper(PenRequestMacroMapper.class);

  PenRequestMacro toStructure(PenRequestMacroEntity entity);

  PenRequestMacroEntity toModel(PenRequestMacro struct);
}
