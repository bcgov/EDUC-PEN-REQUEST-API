package ca.bc.gov.educ.api.penRequest.repository;

import org.springframework.data.repository.CrudRepository;
import ca.bc.gov.educ.api.penRequest.model.PenRequestEntity;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PenRequestRepository extends CrudRepository<PenRequestEntity, UUID> {
}
