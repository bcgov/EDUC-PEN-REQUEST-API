package ca.bc.gov.educ.api.penrequest.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import ca.bc.gov.educ.api.penrequest.model.DocumentEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends CrudRepository<DocumentEntity, UUID> {
    List<DocumentEntity> findByPenRequestPenRequestID(UUID penRequestId);
}