package ca.bc.gov.educ.api.penRequest.repository;

import org.springframework.data.repository.CrudRepository;
import ca.bc.gov.educ.api.penRequest.model.PenRequestEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface PenRequestRepository extends CrudRepository<PenRequestEntity, Integer> {
}
