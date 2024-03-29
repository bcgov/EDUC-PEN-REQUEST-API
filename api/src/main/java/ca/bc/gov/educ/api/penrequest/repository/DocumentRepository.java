package ca.bc.gov.educ.api.penrequest.repository;

import ca.bc.gov.educ.api.penrequest.model.v1.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> {
  List<DocumentEntity> findByPenRequestPenRequestID(UUID penRequestId);

  // this query will only filter where document data is not null and file size greater than zero, so that system is not pulling a lot of records from db.
  List<DocumentEntity> findAllByPenRequestPenRequestStatusCodeInAndFileSizeGreaterThanAndDocumentDataIsNotNull(List<String> penRequestStatusCodes, int fileSize);
}
