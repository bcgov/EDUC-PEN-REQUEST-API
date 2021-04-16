package ca.bc.gov.educ.api.penrequest.repository;

import ca.bc.gov.educ.api.penrequest.model.PenRequestStatusCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Pen Request Status Code Table Repository
 *
 * @author Marco Villeneuve
 */
@Repository
public interface PenRequestStatusCodeTableRepository extends JpaRepository<PenRequestStatusCodeEntity, Long> {
}
