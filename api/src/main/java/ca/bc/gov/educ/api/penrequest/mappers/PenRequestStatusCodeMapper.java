package ca.bc.gov.educ.api.penrequest.mappers;

import ca.bc.gov.educ.api.penrequest.model.PenRequestStatusCodeEntity;
import ca.bc.gov.educ.api.penrequest.struct.PenRequestStatusCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UUIDMapper.class)
@SuppressWarnings("squid:S1214")
public interface PenRequestStatusCodeMapper {


    PenRequestStatusCodeMapper mapper = Mappers.getMapper(PenRequestStatusCodeMapper.class);

    PenRequestStatusCode toStructure(PenRequestStatusCodeEntity entity);

    @Mapping(target = "updateUser", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "createUser", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    PenRequestStatusCodeEntity toModel(PenRequestStatusCode struct);
}
