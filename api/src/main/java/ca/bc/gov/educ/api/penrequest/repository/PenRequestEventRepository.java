package ca.bc.gov.educ.api.penrequest.repository;

import ca.bc.gov.educ.api.penrequest.model.v1.PenRequestEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PenRequestEventRepository extends JpaRepository<PenRequestEvent, UUID> {
  Optional<PenRequestEvent> findBySagaIdAndEventType(UUID sagaId, String eventType);

  @Transactional
  @Modifying
  @Query("delete from PenRequestEvent where createDate <= :createDate")
  void deleteByCreateDateBefore(LocalDateTime createDate);
}
