package ca.bc.gov.educ.api.penrequest.repository;

import ca.bc.gov.educ.api.penrequest.model.PenRequestMacroEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PenRequestMacroRepository extends CrudRepository<PenRequestMacroEntity, UUID> {
  List<PenRequestMacroEntity> findAll();

  List<PenRequestMacroEntity> findAllByMacroTypeCode(String macroTypeCode);
}
