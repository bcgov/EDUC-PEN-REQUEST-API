package ca.bc.gov.educ.api.penrequest.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;

@Repository
public interface PenRequestRepository extends CrudRepository<PenRequestEntity, UUID> {
  List<PenRequestEntity> findPenRequestEntitiesByDigitalIDAndPenRequestStatusCode(UUID digitalID, String statusCode);
}
