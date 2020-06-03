package ca.bc.gov.educ.api.penrequest.schedulers;

import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.props.ApplicationProperties;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import lombok.Getter;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static ca.bc.gov.educ.api.penrequest.constants.PenRequestStatusCode.DRAFT;
import static ca.bc.gov.educ.api.penrequest.constants.PenRequestStatusCode.STALE;
import static lombok.AccessLevel.PRIVATE;

/**
 * This class is responsible for handling all the scheduled jobs for pen request api.
 *
 * @author om
 */
@Component
public class PenRequestScheduler {

  @Getter(PRIVATE)
  private final ApplicationProperties applicationProperties;

  @Getter(PRIVATE)
  private final PenRequestRepository penRequestRepository;

  @Autowired
  public PenRequestScheduler(ApplicationProperties applicationProperties, PenRequestRepository penRequestRepository) {
    this.applicationProperties = applicationProperties;
    this.penRequestRepository = penRequestRepository;
  }

  /**
   * This method is responsible to find all the pen requests that are in draft status and change their status to STALE.
   * the cron will be set in property file so that it can be changed easily.
   * system is going to lock the row for 5 minutes, assuming the frequency will always be higher than 5 minutes.
   * <b> if cron job is set to lower than 5 minutes in property then the lock at least and at most needs to be revisited.</b>
   */
  @Scheduled(cron = "${scheduler.cron.penrequest.draft}")
  @SchedulerLock(name = "pen_request_table_draft_requests",
      lockAtLeastFor = "600s", lockAtMostFor = "600s")
  @Transactional
  public void findAndUpdateDraftPenRequests() {
    var daysBeforeStale = getApplicationProperties().getNumOfDaysInDraftStatusForStale();
    var penRequests = getPenRequestRepository().findByPenRequestStatusCode(DRAFT.toString());
    var updatedPenRequests = new ArrayList<PenRequestEntity>();
    if (!penRequests.isEmpty()) {
      for (var penRequest : penRequests) {
        if (penRequest.getUpdateDate().isBefore(LocalDateTime.now().minusDays(daysBeforeStale))) {
          penRequest.setPenRequestStatusCode(STALE.toString());
          penRequest.setUpdateDate(LocalDateTime.now());
          updatedPenRequests.add(penRequest);
        }
      }
    }
    if (!updatedPenRequests.isEmpty()) {
      getPenRequestRepository().saveAll(updatedPenRequests);
    }
  }
}
