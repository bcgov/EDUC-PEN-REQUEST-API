package ca.bc.gov.educ.api.penrequest.repository;

import ca.bc.gov.educ.api.penrequest.model.PenRequestEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PenRequestEventRepository extends CrudRepository<PenRequestEvent, UUID> {
  Optional<PenRequestEvent> findBySagaIdAndEventType(UUID sagaId, String eventType);

  List<PenRequestEvent> findByEventStatus(String toString);
}
