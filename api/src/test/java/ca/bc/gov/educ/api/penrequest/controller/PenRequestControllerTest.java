package ca.bc.gov.educ.api.penrequest.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.bc.gov.educ.api.penrequest.exception.RestExceptionHandler;
import ca.bc.gov.educ.api.penrequest.mappers.PenRequestEntityMapper;
import ca.bc.gov.educ.api.penrequest.model.GenderCodeEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestStatusCodeEntity;
import ca.bc.gov.educ.api.penrequest.repository.DocumentRepository;
import ca.bc.gov.educ.api.penrequest.repository.GenderCodeTableRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestStatusCodeTableRepository;
import ca.bc.gov.educ.api.penrequest.support.WithMockOAuth2Scope;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PenRequestControllerTest extends BasePenReqControllerTest {

  private static final PenRequestEntityMapper mapper = PenRequestEntityMapper.mapper;
  private MockMvc mockMvc;
  @Autowired
  PenRequestController controller;

  @Autowired
  GenderCodeTableRepository genderRepo;
  
  @Autowired
  PenRequestRepository repository;

  @Autowired
  DocumentRepository documentRepository;

  @Autowired
  PenRequestStatusCodeTableRepository penRequestStatusCodeTableRepo;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new RestExceptionHandler()).build();
    genderRepo.save(createGenderCodeData());
  }

  @After
  public void after() {
    documentRepository.deleteAll();
    repository.deleteAll();
    genderRepo.deleteAll();
  }

  private GenderCodeEntity createGenderCodeData() {
    return GenderCodeEntity.builder().genderCode("M").description("Male")
            .effectiveDate(LocalDateTime.now()).expiryDate(LocalDateTime.MAX).displayOrder(1).label("label").createDate(LocalDateTime.now())
            .updateDate(LocalDateTime.now()).createUser("TEST").updateUser("TEST").build();
  }
  

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testRetrievePenRequest_GivenRandomID_ShouldThrowEntityNotFoundException() throws Exception {
    this.mockMvc.perform(get("/" + UUID.randomUUID())).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testRetrievePenRequest_GivenValidID_ShouldReturnOkStatus() throws Exception {
    PenRequestEntity entity = repository.save(mapper.toModel(getPenRequestEntityFromJsonString()));
    this.mockMvc.perform(get("/" + entity.getPenRequestID())).andDo(print()).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.penRequestID").value(entity.getPenRequestID().toString()));
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testFindPenRequest_GivenOnlyPenInQueryParam_ShouldReturnOkStatusAndEntities() throws Exception {
    PenRequestEntity entity = repository.save(mapper.toModel(getPenRequestEntityFromJsonString()));
    this.mockMvc.perform(get("?pen" + entity.getPen())).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1))).andExpect(MockMvcResultMatchers.jsonPath("$[0].pen").value(entity.getPen()));
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testRetrievePenRequest_GivenRandomDigitalIdAndStatusCode_ShouldReturnOkStatus() throws Exception {
    this.mockMvc.perform(get("?digitalID=" + UUID.randomUUID() + "&status=" + "INT")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQUEST")
  public void testCreatePenRequest_GivenValidPayload_ShouldReturnStatusCreated() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyPenRequestJson())).andDo(print()).andExpect(status().isCreated());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQUEST")
  public void testCreatePenRequest_GivenInitialSubmitDateInPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyPenRequestJsonWithInitialSubmitDate())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQUEST")
  public void testCreatePenRequest_GivenPenReqIdInPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyPenRequestJsonWithInvalidPenReqID())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQUEST")
  public void testCreatePenRequest_LowercaseEmailVerifiedFlag_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyPenRequestJsonWithInvalidEmailVerifiedFlag())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQUEST")
  public void testUpdatePenRequest_GivenInvalidPenReqIDInPayload_ShouldReturnStatusNotFound() throws Exception {
    this.mockMvc.perform(put("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyPenRequestJsonWithInvalidPenReqID())).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQUEST")
  public void testUpdatePenRequest_GivenValidPenReqIDInPayload_ShouldReturnStatusOk() throws Exception {
    PenRequestEntity entity = repository.save(mapper.toModel(getPenRequestEntityFromJsonString()));
    String penReqId = entity.getPenRequestID().toString();
    this.mockMvc.perform(put("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyPenRequestJsonWithValidPenReqID(penReqId))).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQUEST")
  public void testUpdatePenRequest_GivenInvalidDemogChangedInPayload_ShouldReturnBadRequest() throws Exception {
    PenRequestEntity entity = repository.save(mapper.toModel(getPenRequestEntityFromJsonString()));
    String penReqId = entity.getPenRequestID().toString();
    this.mockMvc.perform(put("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyPenRequestJsonWithInvalidDemogChanged(penReqId))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST_STATUSES")
  public void testReadPenRequestStatus_Always_ShouldReturnStatusOkAndAllDataFromDB() throws Exception {
    penRequestStatusCodeTableRepo.save(createPenReqStatus());
    this.mockMvc.perform(get("/statuses")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
  }


  @Test
  public void testHealth_GivenServerIsRunning_ShouldReturnOK() throws Exception {
    this.mockMvc.perform(get("/health")).andDo(print()).andExpect(status().isOk())
            .andExpect(content().string(containsString("OK")));
  }

  private PenRequestStatusCodeEntity createPenReqStatus() {
    PenRequestStatusCodeEntity entity = new PenRequestStatusCodeEntity();
    entity.setPenRequestStatusCode("INITREV");
    entity.setDescription("Initial Review");
    entity.setDisplayOrder(1);
    entity.setEffectiveDate(LocalDateTime.now());
    entity.setLabel("Initial Review");
    entity.setCreateDate(LocalDateTime.now());
    entity.setCreateUser("TEST");
    entity.setUpdateUser("TEST");
    entity.setUpdateDate(LocalDateTime.now());
    entity.setExpiryDate(LocalDateTime.from(new GregorianCalendar(2099, Calendar.FEBRUARY, 1).toZonedDateTime()));
    return entity;
  }


}
