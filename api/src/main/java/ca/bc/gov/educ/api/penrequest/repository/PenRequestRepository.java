package ca.bc.gov.educ.api.penrequest.repository;

import ca.bc.gov.educ.api.penrequest.model.v1.PenRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PenRequestRepository extends JpaRepository<PenRequestEntity, UUID>, PenRequestRepositoryCustom, JpaSpecificationExecutor<PenRequestEntity> {
}
