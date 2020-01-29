package ca.bc.gov.educ.api.penrequest.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ca.bc.gov.educ.api.penrequest.model.DocumentTypeCodeEntity;

/**
 * Document Type Code Table Repository
 * 
 */
@Repository
public interface DocumentTypeCodeTableRepository extends CrudRepository<DocumentTypeCodeEntity, String> {
    List<DocumentTypeCodeEntity> findAll();
}
