package ca.bc.gov.educ.api.penrequest.repository;

import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PenRequestRepository extends CrudRepository<PenRequestEntity, UUID>, PenRequestRepositoryCustom, JpaSpecificationExecutor<PenRequestEntity> {
  List<PenRequestEntity> findAll();
  List<PenRequestEntity> findByPenRequestStatusCode(String penRequestStatusCode);
}
