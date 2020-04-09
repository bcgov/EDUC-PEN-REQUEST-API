package ca.bc.gov.educ.api.penrequest.mappers;

import ca.bc.gov.educ.api.penrequest.model.PenRequestMacroEntity;
import ca.bc.gov.educ.api.penrequest.struct.PenRequestMacro;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UUIDMapper.class, LocalDateTimeMapper.class})
@SuppressWarnings("squid:S1214")
public interface PenRequestMacroMapper {

  PenRequestMacroMapper mapper = Mappers.getMapper(PenRequestMacroMapper.class);

  PenRequestMacro toStructure(PenRequestMacroEntity entity);

  PenRequestMacroEntity toModel(PenRequestMacro struct);
}
