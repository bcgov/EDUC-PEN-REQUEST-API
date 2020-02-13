package ca.bc.gov.educ.api.penrequest.controller;

import ca.bc.gov.educ.api.penrequest.exception.RestExceptionHandler;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestStatusCodeEntity;
import ca.bc.gov.educ.api.penrequest.repository.DocumentRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestStatusCodeTableRepository;
import ca.bc.gov.educ.api.penrequest.support.WithMockOAuth2Scope;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PenRequestControllerTest extends BasePenReqControllerTest {

  private MockMvc mockMvc;
  @Autowired
  PenRequestController controller;

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
  }

  @After
  public void after() {
    documentRepository.deleteAll();
    repository.deleteAll();
  }


  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testRetrievePenRequest_GivenRandomID_ShouldThrowEntityNotFoundException() throws Exception {
    this.mockMvc.perform(get("/" + UUID.randomUUID())).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testRetrievePenRequest_GivenValidID_ShouldReturnOkStatus() throws Exception {
    PenRequestEntity entity = repository.save(getPenRequestEntityFromJsonString());
    this.mockMvc.perform(get("/" + entity.getPenRequestID())).andDo(print()).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.penRequestID").value(entity.getPenRequestID().toString()));
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
    PenRequestEntity entity = repository.save(getPenRequestEntityFromJsonString());
    String penReqId = entity.getPenRequestID().toString();
    this.mockMvc.perform(put("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyPenRequestJsonWithValidPenReqID(penReqId))).andDo(print()).andExpect(status().isOk());
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
    entity.setEffectiveDate(new Date());
    entity.setLabel("Initial Review");
    entity.setCreateDate(new Date());
    entity.setCreateUser("TEST");
    entity.setUpdateUser("TEST");
    entity.setUpdateDate(new Date());
    entity.setExpiryDate(new GregorianCalendar(2099, Calendar.FEBRUARY, 1).getTime());
    return entity;
  }


}
