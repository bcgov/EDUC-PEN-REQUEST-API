package ca.bc.gov.educ.api.penrequest.repository.impl;

import ca.bc.gov.educ.api.penrequest.model.v1.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepositoryCustom;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class PenRequestRepositoryCustomImpl implements PenRequestRepositoryCustom {

  @Getter(AccessLevel.PRIVATE)
  private final EntityManager entityManager;

  @Autowired
  PenRequestRepositoryCustomImpl(final EntityManager em) {
    this.entityManager = em;
  }

  @Override
  public List<PenRequestEntity> findPenRequests(UUID digitalID, String status, String pen) {
    final List<Predicate> predicates = new ArrayList<>();
    final CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
    final CriteriaQuery<PenRequestEntity> criteriaQuery = criteriaBuilder.createQuery(PenRequestEntity.class);
    Root<PenRequestEntity> penRequestEntityRoot = criteriaQuery.from(PenRequestEntity.class);
    if (StringUtils.isNotBlank(status)) {
      predicates.add(criteriaBuilder.equal(penRequestEntityRoot.get("penRequestStatusCode"), status));
    }
    if (digitalID != null) {
      predicates.add(criteriaBuilder.equal(penRequestEntityRoot.get("digitalID"), digitalID));
    }
    if (StringUtils.isNotBlank(pen)) {
      predicates.add(criteriaBuilder.equal(penRequestEntityRoot.get("pen"), pen));
    }
    criteriaQuery.where(predicates.toArray(new Predicate[0]));

    return entityManager.createQuery(criteriaQuery).getResultList();
  }
}
