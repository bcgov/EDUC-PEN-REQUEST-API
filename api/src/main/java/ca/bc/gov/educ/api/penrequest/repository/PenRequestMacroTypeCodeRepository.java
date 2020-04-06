package ca.bc.gov.educ.api.penrequest.repository;

import ca.bc.gov.educ.api.penrequest.model.PenRequestMacroTypeCodeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PenRequestMacroTypeCodeRepository extends CrudRepository<PenRequestMacroTypeCodeEntity, String> {
  List<PenRequestMacroTypeCodeEntity> findAll();
}
