package ca.bc.gov.educ.api.penrequest.schedulers;

import ca.bc.gov.educ.api.penrequest.repository.DocumentRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
public class PenRequestScheduler {
  @Value("${remove.blob.contents.document.after.days}")
  @Setter
  @Getter
  Integer removeDocumentBlobContentsAfterDays;
  private final DocumentRepository documentRepository;

  public PenRequestScheduler(final DocumentRepository documentRepository) {
    this.documentRepository = documentRepository;
  }

  /**
   * run the job based on configured scheduler(a cron expression) and purge old records from DB.
   */
  @Scheduled(cron = "${scheduled.jobs.remove.blob.contents.document.cron}")
  @SchedulerLock(name = "RemoveBlobContentsFromUploadedDocuments",
      lockAtLeastFor = "PT4H", lockAtMostFor = "PT4H") //midnight job so lock for 4 hours
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void removeBlobContentsFromUploadedDocuments() {
    LockAssert.assertLocked();
    final LocalDateTime createDateToCompare = this.calculateCreateDateBasedOnRemoveDocumentBlobInDays();
    val records = this.documentRepository.findAllByCreateDateBefore(createDateToCompare);
    if (!records.isEmpty()) {
      for (val document : records) {
        document.setDocumentData(new byte[0]); // empty the document data.
      }
      this.documentRepository.saveAll(records);
    }

  }

  private LocalDateTime calculateCreateDateBasedOnRemoveDocumentBlobInDays() {
    final LocalDateTime currentTime = LocalDateTime.now();
    return currentTime.minusDays(this.getRemoveDocumentBlobContentsAfterDays());
  }
}