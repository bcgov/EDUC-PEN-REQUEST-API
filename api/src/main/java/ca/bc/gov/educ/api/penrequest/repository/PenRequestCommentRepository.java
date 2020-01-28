package ca.bc.gov.educ.api.penrequest.repository;

import ca.bc.gov.educ.api.penrequest.model.PenRequestCommentsEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PenRequestCommentRepository extends PagingAndSortingRepository<PenRequestCommentsEntity, UUID> {
}
