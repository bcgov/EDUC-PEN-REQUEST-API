package ca.bc.gov.educ.api.penrequest.repository;

import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PenRequestRepository extends CrudRepository<PenRequestEntity, UUID>, PenRequestRepositoryCustom {
}
