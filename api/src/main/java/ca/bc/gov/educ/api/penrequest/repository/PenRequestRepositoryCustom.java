package ca.bc.gov.educ.api.penrequest.repository;

import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;

import java.util.List;
import java.util.UUID;

public interface PenRequestRepositoryCustom {
  /**
   * These parameters are optional, so if these values are not passed it will return all the pen requests.
   *
   * @param digitalID the digitalID for the rows to be filtered from DB. <b>OPTIONAL</b>
   * @param status    the status for the rows to be filtered.<b>OPTIONAL</b>
   * @return List of {@link PenRequestEntity}
   */
  List<PenRequestEntity> findPenRequests(UUID digitalID, String status);
}
