package ca.bc.gov.educ.api.penrequest.repository;

import ca.bc.gov.educ.api.penrequest.model.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> {
  List<DocumentEntity> findByPenRequestPenRequestID(UUID penRequestId);

  List<DocumentEntity> findTop10000ByCreateDateBefore(LocalDateTime createDate);
}
