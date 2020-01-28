package ca.bc.gov.educ.api.penrequest.mappers;

import ca.bc.gov.educ.api.penrequest.model.PenRequestStatusCodeEntity;
import ca.bc.gov.educ.api.penrequest.struct.PenRequestStatusCode;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UUIDMapper.class)
public interface PenRequestStatusCodeMapper {


    PenRequestStatusCodeMapper mapper = Mappers.getMapper(PenRequestStatusCodeMapper.class);

    PenRequestStatusCode toStructure(PenRequestStatusCodeEntity entity);

    PenRequestStatusCodeEntity toModel(PenRequestStatusCode struct);
}
