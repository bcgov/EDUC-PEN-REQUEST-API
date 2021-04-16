package ca.bc.gov.educ.api.penrequest.repository;

import ca.bc.gov.educ.api.penrequest.model.PenRequestMacroTypeCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PenRequestMacroTypeCodeRepository extends JpaRepository<PenRequestMacroTypeCodeEntity, String> {
}
