package ca.bc.gov.educ.api.penrequest.controller;

import ca.bc.gov.educ.api.penrequest.exception.RestExceptionHandler;
import ca.bc.gov.educ.api.penrequest.mappers.PenRequestMacroMapper;
import ca.bc.gov.educ.api.penrequest.model.PenRequestMacroTypeCodeEntity;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestMacroRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestMacroTypeCodeRepository;
import ca.bc.gov.educ.api.penrequest.service.PenRequestMacroService;
import ca.bc.gov.educ.api.penrequest.struct.PenRequestMacro;
import ca.bc.gov.educ.api.penrequest.support.WithMockOAuth2Scope;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class PenReqMacroControllerTest extends BasePenReqControllerTest {

  private static final PenRequestMacroMapper mapper = PenRequestMacroMapper.mapper;
  @Autowired
  PenRequestMacroController controller;

  @Autowired
  PenRequestMacroService service;

  private MockMvc mockMvc;

  @Autowired
  PenRequestMacroTypeCodeRepository penRequestMacroTypeCodeRepository;

  @Autowired
  PenRequestMacroRepository penRequestMacroRepository;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new RestExceptionHandler()).build();
    penRequestMacroTypeCodeRepository.save(createPenReqMacroTypeCode());
  }

  @After
  public void after() {
    penRequestMacroTypeCodeRepository.deleteAll();
    penRequestMacroRepository.deleteAll();
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQ_MACRO")
  public void testRetrievePenRequestMacros_ShouldReturnStatusOK() throws Exception {
    this.mockMvc.perform(get("/pen-request-macro")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQ_MACRO")
  public void testRetrievePenRequestMacros_GivenInvalidMacroID_ShouldReturnStatusNotFound() throws Exception {
    this.mockMvc.perform(get("/pen-request-macro/" + UUID.randomUUID().toString())).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQ_MACRO")
  public void testRetrievePenRequestMacros_GivenValidMacroID_ShouldReturnStatusOK() throws Exception {
    val entity = mapper.toModel(getPenRequestMacroEntityFromJsonString());
    entity.setMacroId(null);
    entity.setCreateDate(LocalDateTime.now());
    entity.setUpdateDate(LocalDateTime.now());
    val savedEntity = service.createMacro(entity);
    var result = this.mockMvc.perform(get("/pen-request-macro/" + savedEntity.getMacroId().toString())).andDo(print()).andExpect(MockMvcResultMatchers.jsonPath("$.macroId").value(entity.getMacroId().toString())).andExpect(status().isOk()).andReturn();
    assertThat(result).isNotNull();
  }
  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQ_MACRO")
  public void testRetrievePenRequestMacros_GivenInValidMacroID_ShouldReturnStatusNotFound() throws Exception {
    var result = this.mockMvc.perform(get("/pen-request-macro/" + UUID.randomUUID().toString())).andDo(print()).andExpect(status().isNotFound()).andReturn();
    assertThat(result).isNotNull();
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQ_MACRO")
  public void testRetrievePenRequestMacros_GivenValidMacroTypeCode_ShouldReturnStatusOK() throws Exception {
    val entity = mapper.toModel(getPenRequestMacroEntityFromJsonString());
    entity.setMacroId(null);
    entity.setCreateDate(LocalDateTime.now());
    entity.setUpdateDate(LocalDateTime.now());
    val savedEntity = service.createMacro(entity);
    var result = this.mockMvc.perform(get("/pen-request-macro/?macroTypeCode=" + savedEntity.getMacroTypeCode())).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
    assertThat(result).isNotNull();
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQ_MACRO")
  public void testCreatePenRequestMacros_GivenValidPayload_ShouldReturnStatusCreated() throws Exception {
    this.mockMvc.perform(post("/pen-request-macro").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyPenRequestMacroJson())).andDo(print()).andExpect(status().isCreated());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQ_MACRO")
  public void testCreatePenRequestMacros_GivenInValidPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/pen-request-macro").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyPenRequestMacroJsonWithId())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQ_MACRO")
  public void testUpdatePenRequestMacros_GivenValidPayload_ShouldReturnStatusOK() throws Exception {
    val entity = mapper.toModel(getPenRequestMacroEntityFromJsonString());
    entity.setMacroId(null);
    entity.setCreateDate(LocalDateTime.now());
    entity.setUpdateDate(LocalDateTime.now());
    val savedEntity = service.createMacro(entity);
    savedEntity.setCreateDate(null);
    savedEntity.setUpdateDate(null);
    savedEntity.setMacroText("updated text");
    String jsonString = new ObjectMapper().writeValueAsString(mapper.toStructure(savedEntity));
    var result = this.mockMvc.perform(put("/pen-request-macro/" + savedEntity.getMacroId().toString()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print()).andExpect(status().isOk());
    assertThat(result).isNotNull();

  }
  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQ_MACRO")
  public void testUpdatePenRequestMacros_GivenInValidPayload_ShouldReturnStatusNotFound() throws Exception {
    val entity = mapper.toModel(getPenRequestMacroEntityFromJsonString());
    entity.setMacroId(null);
    entity.setCreateDate(LocalDateTime.now());
    entity.setUpdateDate(LocalDateTime.now());
    val savedEntity = service.createMacro(entity);
    savedEntity.setCreateDate(null);
    savedEntity.setUpdateDate(null);
    savedEntity.setMacroText("updated text");
    String jsonString = new ObjectMapper().writeValueAsString(mapper.toStructure(savedEntity));
    var result = this.mockMvc.perform(put("/pen-request-macro/" + UUID.randomUUID().toString()).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print()).andExpect(status().isNotFound());
    assertThat(result).isNotNull();

  }

  private PenRequestMacroTypeCodeEntity createPenReqMacroTypeCode() {
    return PenRequestMacroTypeCodeEntity.builder()
            .createDate(LocalDateTime.now())
            .updateDate(LocalDateTime.now())
            .createUser("TEST")
            .updateUser("TEST")
            .description("TEST")
            .displayOrder(1)
            .effectiveDate(LocalDate.now().minusDays(2))
            .expiryDate(LocalDate.now().plusDays(2))
            .label("TEST")
            .macroTypeCode("REJECT")
            .build();
  }

  protected String dummyPenRequestMacroJson() {
    return " {\n" +
            "    \"createUser\": \"om\",\n" +
            "    \"updateUser\": \"om\",\n" +
            "    \"macroCode\": \"hi\",\n" +
            "    \"macroTypeCode\": \"REJECT\",\n" +
            "    \"macroText\": \"hello\"\n" +
            "  }";
  }

  protected String dummyPenRequestMacroJsonWithId() {
    return " {\n" +
            "    \"createUser\": \"om\",\n" +
            "    \"updateUser\": \"om\",\n" +
            "    \"macroCode\": \"hi\",\n" +
            "    \"macroId\": \"7f000101-7151-1d84-8171-5187006c0000\",\n" +
            "    \"macroTypeCode\": \"REJECT\",\n" +
            "    \"macroText\": \"hello\"\n" +
            "  }";
  }

  protected PenRequestMacro getPenRequestMacroEntityFromJsonString() {
    try {
      return new ObjectMapper().readValue(dummyPenRequestMacroJson(), PenRequestMacro.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
