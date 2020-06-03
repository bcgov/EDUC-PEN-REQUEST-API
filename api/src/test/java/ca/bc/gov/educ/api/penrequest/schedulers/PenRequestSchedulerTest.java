package ca.bc.gov.educ.api.penrequest.schedulers;

import ca.bc.gov.educ.api.penrequest.mappers.PenRequestEntityMapper;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.props.ApplicationProperties;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.struct.PenRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static ca.bc.gov.educ.api.penrequest.constants.PenRequestStatusCode.DRAFT;
import static ca.bc.gov.educ.api.penrequest.constants.PenRequestStatusCode.STALE;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PenRequestSchedulerTest {
  private static final PenRequestEntityMapper mapper = PenRequestEntityMapper.mapper;
  @Autowired
  PenRequestRepository penRequestRepository;
  @Autowired
  PenRequestScheduler penRequestScheduler;
  @Autowired
  ApplicationProperties applicationProperties;
  @After
  public void after() {
    penRequestRepository.deleteAll();
  }

  @Test
  public void testFindAndUpdateDraftPenRequests_givenDRAFTPenRequestsInDBMoreThanConfiguredDaysOld_shouldBeUpdatedToSTALEStatus() throws IOException {
    UUID penRequestId = null;
    final File file = new File(
        Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    penRequestRepository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    var entity = penRequestRepository.findPenRequests(UUID.fromString("fdf94a22-51e3-4816-8665-9f8571af1be4"), null, null);
    if (!entity.isEmpty()) {
      var penReq = entity.get(0);
      penRequestId = penReq.getPenRequestID();
      penReq.setUpdateDate(LocalDateTime.now().minusDays(applicationProperties.getNumOfDaysInDraftStatusForStale()));
      penReq.setPenRequestStatusCode(DRAFT.toString());
      penRequestRepository.save(penReq);
    }
    penRequestScheduler.findAndUpdateDraftPenRequests();
    assert penRequestId != null;
    var penRequest = penRequestRepository.findById(penRequestId);
    assertThat(penRequest.isPresent()).isTrue();
    assertThat(penRequest.get().getPenRequestStatusCode()).isEqualTo(STALE.toString());
  }

  @Test
  public void testFindAndUpdateDraftPenRequests_givenNoDRAFTPenRequestsInDB_shouldDoNothing() throws IOException {
    final File file = new File(
        Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    penRequestRepository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    penRequestScheduler.findAndUpdateDraftPenRequests();
    var penRequest = penRequestRepository.findByPenRequestStatusCode(STALE.toString());
    assertThat(penRequest.size() == 0 ).isTrue();
  }
}