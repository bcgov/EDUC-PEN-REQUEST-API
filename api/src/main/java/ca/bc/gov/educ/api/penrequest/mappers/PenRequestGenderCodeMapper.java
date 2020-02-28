package ca.bc.gov.educ.api.penrequest.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import ca.bc.gov.educ.api.penrequest.model.GenderCodeEntity;
import ca.bc.gov.educ.api.penrequest.struct.GenderCode;

@Mapper(uses = UUIDMapper.class)
@SuppressWarnings("squid:S1214")
public interface PenRequestGenderCodeMapper {


    PenRequestGenderCodeMapper mapper = Mappers.getMapper(PenRequestGenderCodeMapper.class);

    GenderCode toStructure(GenderCodeEntity entity);

    @Mapping(target = "updateUser", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "createUser", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    GenderCodeEntity toModel(GenderCode struct);
}
