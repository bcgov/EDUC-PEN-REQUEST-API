package ca.bc.gov.educ.api.penrequest.controller;

import ca.bc.gov.educ.api.penrequest.exception.RestExceptionHandler;
import ca.bc.gov.educ.api.penrequest.mappers.PenRequestEntityMapper;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestCommentRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.support.WithMockOAuth2Scope;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PenRequestCommentsControllerTest extends BasePenReqControllerTest {
  private static final PenRequestEntityMapper mapper = PenRequestEntityMapper.mapper;
  private MockMvc mockMvc;
  @Autowired
  PenRequestCommentsController controller;
  @Autowired
  PenRequestRepository penRequestRepository;
  @Autowired
  PenRequestCommentRepository repository;

  @BeforeClass
  public static void beforeClass() {

  }

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new RestExceptionHandler()).build();
  }

  @After
  public void after() {
    repository.deleteAll();
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testRetrievePenRequestComments_GivenInvalidPenReqID_ShouldReturnStatusNotFound() throws Exception {
    this.mockMvc.perform(get("/" + UUID.randomUUID().toString() + "/comments")).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testRetrievePenRequestComments_GivenValidPenReqID_ShouldReturnStatusOk() throws Exception {
    PenRequestEntity entity = penRequestRepository.save(mapper.toModel(getPenRequestEntityFromJsonString()));
    String penReqId = entity.getPenRequestID().toString();
    this.mockMvc.perform(get("/" + penReqId + "/comments")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQUEST")
  public void testCreatePenRequestComments_GivenValidPayload_ShouldReturnStatusCreated() throws Exception {
    PenRequestEntity entity = penRequestRepository.save(mapper.toModel(getPenRequestEntityFromJsonString()));
    String penReqId = entity.getPenRequestID().toString();
    this.mockMvc.perform(post("/" + penReqId + "/comments").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyPenRequestCommentsJsonWithValidPenReqID(penReqId))).andDo(print()).andExpect(status().isCreated());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQUEST")
  public void testCreatePenRequestComments_GivenInvalidPenReqId_ShouldReturnStatusNotFound() throws Exception {
    String penReqId = UUID.randomUUID().toString();
    this.mockMvc.perform(post("/" + penReqId + "/comments").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyPenRequestCommentsJsonWithValidPenReqID(penReqId))).andDo(print()).andExpect(status().isNotFound());
  }

  private String dummyPenRequestCommentsJsonWithValidPenReqID(String penReqId) {
    return "{\n" +
            "  \"penRetrievalRequestID\": \"" + penReqId + "\",\n" +
            "  \"commentContent\": \"" + "comment1" + "\",\n" +
            "  \"commentTimestamp\": \"2020-02-09T00:00:00\"\n" +
            "}";
  }
}
