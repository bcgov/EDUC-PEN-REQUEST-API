package ca.bc.gov.educ.api.penrequest.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ca.bc.gov.educ.api.penrequest.model.GenderCodeEntity;

/**
 * Gender Code Table Repository
 *
 * @author Marco Villeneuve
 * 
 */
@Repository
public interface GenderCodeTableRepository extends CrudRepository<GenderCodeEntity, Long> {
    List<GenderCodeEntity> findAll();
}
