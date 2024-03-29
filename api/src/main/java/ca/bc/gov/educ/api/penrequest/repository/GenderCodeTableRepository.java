package ca.bc.gov.educ.api.penrequest.repository;

import ca.bc.gov.educ.api.penrequest.model.v1.GenderCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Gender Code Table Repository
 *
 * @author Marco Villeneuve
 */
@Repository
public interface GenderCodeTableRepository extends JpaRepository<GenderCodeEntity, Long> {
}
