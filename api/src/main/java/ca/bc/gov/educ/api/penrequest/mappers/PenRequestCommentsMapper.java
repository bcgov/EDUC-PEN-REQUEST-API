package ca.bc.gov.educ.api.penrequest.mappers;

import ca.bc.gov.educ.api.penrequest.model.PenRequestCommentsEntity;
import ca.bc.gov.educ.api.penrequest.struct.PenRequestComments;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UUIDMapper.class)
@SuppressWarnings("squid:S1214")
public interface PenRequestCommentsMapper {
    PenRequestCommentsMapper mapper = Mappers.getMapper(PenRequestCommentsMapper.class);

    PenRequestComments toStructure(PenRequestCommentsEntity entity);

    PenRequestCommentsEntity toModel(PenRequestComments structure);
}
