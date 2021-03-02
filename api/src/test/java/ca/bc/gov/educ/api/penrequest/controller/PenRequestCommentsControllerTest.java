package ca.bc.gov.educ.api.penrequest.controller;

import ca.bc.gov.educ.api.penrequest.exception.RestExceptionHandler;
import ca.bc.gov.educ.api.penrequest.mappers.PenRequestEntityMapper;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestCommentRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class PenRequestCommentsControllerTest extends BasePenReqControllerTest {
    private static final PenRequestEntityMapper mapper = PenRequestEntityMapper.mapper;
    @Autowired
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
        MockitoAnnotations.openMocks(this);
    }

    @After
    public void after() {
        repository.deleteAll();
    }

    @Test
    public void testRetrievePenRequestComments_GivenInvalidPenReqID_ShouldReturnStatusNotFound() throws Exception {
        this.mockMvc.perform(get("/" + UUID.randomUUID().toString() + "/comments")
                .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST"))))
                .andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void testRetrievePenRequestComments_GivenValidPenReqID_ShouldReturnStatusOk() throws Exception {
        PenRequestEntity entity = penRequestRepository.save(mapper.toModel(getPenRequestEntityFromJsonString()));
        String penReqId = entity.getPenRequestID().toString();
        this.mockMvc.perform(get("/" + penReqId + "/comments")
                .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST"))))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void testCreatePenRequestComments_GivenValidPayload_ShouldReturnStatusCreated() throws Exception {
        PenRequestEntity entity = penRequestRepository.save(mapper.toModel(getPenRequestEntityFromJsonString()));
        String penReqId = entity.getPenRequestID().toString();
        this.mockMvc.perform(post("/" + penReqId + "/comments")
                .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_PEN_REQUEST")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(dummyPenRequestCommentsJsonWithValidPenReqID(penReqId))).andDo(print()).andExpect(status().isCreated());
    }

    @Test
    public void testCreatePenRequestComments_GivenInvalidPenReqId_ShouldReturnStatusNotFound() throws Exception {
        String penReqId = UUID.randomUUID().toString();
        this.mockMvc.perform(post("/" + penReqId + "/comments")
                .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_PEN_REQUEST")))
                .contentType(MediaType.APPLICATION_JSON)
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
