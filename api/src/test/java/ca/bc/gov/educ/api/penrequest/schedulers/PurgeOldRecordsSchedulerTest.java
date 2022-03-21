package ca.bc.gov.educ.api.penrequest.schedulers;

import ca.bc.gov.educ.api.penrequest.BasePenRequestAPITest;
import ca.bc.gov.educ.api.penrequest.constants.EventStatus;
import ca.bc.gov.educ.api.penrequest.model.v1.PenRequestEvent;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestEventRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


public class PurgeOldRecordsSchedulerTest extends BasePenRequestAPITest {

  @Autowired
  PenRequestEventRepository penRequestEventRepository;

  @Autowired
  PurgeOldRecordsScheduler purgeOldRecordsScheduler;


  @Test
  public void testPurgeOldRecords_givenOldRecordsPresent_shouldBeDeleted() {
    final var payload = " {\n" +
        "    \"createUser\": \"test\",\n" +
        "    \"updateUser\": \"test\",\n" +
        "    \"legalFirstName\": \"Jack\"\n" +
        "  }";

    final var yesterday = LocalDateTime.now().minusDays(1);

    this.penRequestEventRepository.save(this.getPenRequestEvent(payload, LocalDateTime.now()));

    this.penRequestEventRepository.save(this.getPenRequestEvent(payload, yesterday));

    this.purgeOldRecordsScheduler.setEventRecordStaleInDays(1);
    this.purgeOldRecordsScheduler.purgeOldRecords();

    final var servicesEvents = this.penRequestEventRepository.findAll();
    assertThat(servicesEvents).hasSize(1);
  }


  private PenRequestEvent getPenRequestEvent(final String payload, final LocalDateTime createDateTime) {
    return PenRequestEvent
      .builder()
      .eventPayloadBytes(payload.getBytes())
      .eventStatus(EventStatus.MESSAGE_PUBLISHED.toString())
      .eventType("UPDATE_PEN_REQUEST")
      .sagaId(UUID.randomUUID())
      .eventOutcome("PEN_REQUEST_UPDATED")
      .replyChannel("TEST_CHANNEL")
      .createDate(createDateTime)
      .createUser("PEN_REQUEST_BATCH_API")
      .updateUser("PEN_REQUEST_BATCH_API")
      .updateDate(createDateTime)
      .build();
  }
}
