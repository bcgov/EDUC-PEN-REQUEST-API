package ca.bc.gov.educ.api.penrequest.mappers;

import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.struct.PenRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UUIDMapper.class)
@SuppressWarnings("squid:S1214")
public interface PenRequestEntityMapper {

    PenRequestEntityMapper mapper = Mappers.getMapper(PenRequestEntityMapper.class);

    PenRequest toStructure(PenRequestEntity entity);

    PenRequestEntity toModel(PenRequest struct);
}
