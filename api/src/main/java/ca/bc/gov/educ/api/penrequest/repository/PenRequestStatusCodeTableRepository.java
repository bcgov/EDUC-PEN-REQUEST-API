package ca.bc.gov.educ.api.penrequest.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ca.bc.gov.educ.api.penrequest.model.PenRequestStatusCodeEntity;

/**
 * Pen Request Status Code Table Repository
 *
 * @author Marco Villeneuve
 * 
 */
@Repository
public interface PenRequestStatusCodeTableRepository extends CrudRepository<PenRequestStatusCodeEntity, Long> {
    List<PenRequestStatusCodeEntity> findAll();
}
