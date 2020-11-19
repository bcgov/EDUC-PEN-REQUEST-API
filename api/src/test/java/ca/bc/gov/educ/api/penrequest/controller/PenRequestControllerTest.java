package ca.bc.gov.educ.api.penrequest.controller;

import ca.bc.gov.educ.api.penrequest.exception.RestExceptionHandler;
import ca.bc.gov.educ.api.penrequest.filter.FilterOperation;
import ca.bc.gov.educ.api.penrequest.mappers.PenRequestEntityMapper;
import ca.bc.gov.educ.api.penrequest.model.*;
import ca.bc.gov.educ.api.penrequest.repository.DocumentRepository;
import ca.bc.gov.educ.api.penrequest.repository.GenderCodeTableRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestStatusCodeTableRepository;
import ca.bc.gov.educ.api.penrequest.struct.PenRequest;
import ca.bc.gov.educ.api.penrequest.struct.SearchCriteria;
import ca.bc.gov.educ.api.penrequest.struct.ValueType;
import ca.bc.gov.educ.api.penrequest.support.DocumentBuilder;
import ca.bc.gov.educ.api.penrequest.support.WithMockOAuth2Scope;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
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
    MockitoAnnotations.openMocks(this);
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
    this.mockMvc.perform(get("/?pen" + entity.getPen())).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1))).andExpect(MockMvcResultMatchers.jsonPath("$[0].pen").value(entity.getPen()));
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testRetrievePenRequest_GivenRandomDigitalIdAndStatusCode_ShouldReturnOkStatus() throws Exception {
    this.mockMvc.perform(get("/?digitalID=" + UUID.randomUUID() + "&status=" + "INT")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQUEST")
  public void testCreatePenRequest_GivenValidPayload_ShouldReturnStatusCreated() throws Exception {
    this.mockMvc.perform(post("/").contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON).content(dummyPenRequestJson())).andDo(print()).andExpect(status().isCreated());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQUEST")
  public void testCreatePenRequest_GivenInitialSubmitDateInPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON).content(dummyPenRequestJsonWithInitialSubmitDate())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQUEST")
  public void testCreatePenRequest_GivenPenReqIdInPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON).content(dummyPenRequestJsonWithInvalidPenReqID())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQUEST")
  public void testCreatePenRequest_LowercaseEmailVerifiedFlag_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON).content(dummyPenRequestJsonWithInvalidEmailVerifiedFlag())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQUEST")
  public void testUpdatePenRequest_GivenInvalidPenReqIDInPayload_ShouldReturnStatusNotFound() throws Exception {
    this.mockMvc.perform(put("/").contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON).content(dummyPenRequestJsonWithInvalidPenReqID())).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQUEST")
  public void testUpdatePenRequest_GivenValidPenReqIDInPayload_ShouldReturnStatusOk() throws Exception {
    PenRequestEntity entity = repository.save(mapper.toModel(getPenRequestEntityFromJsonString()));
    String penReqId = entity.getPenRequestID().toString();
    this.mockMvc.perform(put("/").contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON).content(dummyPenRequestJsonWithValidPenReqID(penReqId))).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_PEN_REQUEST")
  public void testUpdatePenRequest_GivenInvalidDemogChangedInPayload_ShouldReturnBadRequest() throws Exception {
    PenRequestEntity entity = repository.save(mapper.toModel(getPenRequestEntityFromJsonString()));
    String penReqId = entity.getPenRequestID().toString();
    this.mockMvc.perform(put("/").contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON).content(dummyPenRequestJsonWithInvalidDemogChanged(penReqId))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "DELETE_PEN_REQUEST")
  public void testDeletePenRequest_GivenInvalidId_ShouldReturn404() throws Exception {
    this.mockMvc.perform(delete("/" + UUID.randomUUID().toString()).contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  @WithMockOAuth2Scope(scope = "DELETE_PEN_REQUEST")
  public void testDeletePenRequest_GivenValidId_ShouldReturn204() throws Exception {
    PenRequestEntity entity = repository.save(mapper.toModel(getPenRequestEntityFromJsonString()));
    String penReqId = entity.getPenRequestID().toString();
    this.mockMvc.perform(delete("/" + penReqId).contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  @WithMockOAuth2Scope(scope = "DELETE_PEN_REQUEST")
  public void testDeletePenRequest_GivenValidIdWithAssociations_ShouldReturn204() throws Exception {
    PenRequestEntity penRequestEntity = mapper.toModel(getPenRequestEntityFromJsonString());
    penRequestEntity.setPenRequestComments(createPenRequestComments(penRequestEntity));
    PenRequestEntity entity = repository.save(penRequestEntity);
    DocumentEntity document = new DocumentBuilder()
            .withoutDocumentID()
            //.withoutCreateAndUpdateUser()
            .withPenRequest(entity)
            .withTypeCode("CAPASSPORT")
            .build();
    this.documentRepository.save(document);
    String penReqId = entity.getPenRequestID().toString();
    this.mockMvc.perform(delete("/" + penReqId).contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)).andDo(print()).andExpect(status().isNoContent());
  }

  private Set<PenRequestCommentsEntity> createPenRequestComments(PenRequestEntity penRequestEntity) {
    Set<PenRequestCommentsEntity> commentsEntitySet = new HashSet<>();
    PenRequestCommentsEntity penRequestCommentsEntity = new PenRequestCommentsEntity();
    penRequestCommentsEntity.setPenRequestEntity(penRequestEntity);
    penRequestCommentsEntity.setCommentContent("hi");
    penRequestCommentsEntity.setCommentTimestamp(LocalDateTime.now());
    commentsEntitySet.add(penRequestCommentsEntity);
    return commentsEntitySet;
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST_STATUSES")
  public void testReadPenRequestStatus_Always_ShouldReturnStatusOkAndAllDataFromDB() throws Exception {
    penRequestStatusCodeTableRepo.save(createPenReqStatus());
    this.mockMvc.perform(get("/statuses")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testReadPenRequestPaginated_Always_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    MvcResult result = mockMvc
            .perform(get("/paginated?pageSize=2")
                    .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2)));
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testReadPenRequestPaginated_whenNoDataInDB_ShouldReturnStatusOk() throws Exception {
    MvcResult result = mockMvc
            .perform(get("/paginated")
                    .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(0)));
  }
  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testReadPenRequestPaginatedWithSorting_Always_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    Map<String, String> sortMap = new HashMap<>();
    sortMap.put("legalLastName", "ASC");
    sortMap.put("legalFirstName", "DESC");
    String sort = new ObjectMapper().writeValueAsString(sortMap);
    MvcResult result = mockMvc
            .perform(get("/paginated").param("pageNumber","1").param("pageSize", "5").param("sort", sort)
                    .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(5)));
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testReadPenRequestPaginated_GivenFirstNameFilter_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    SearchCriteria criteria = SearchCriteria.builder().key("legalFirstName").operation(FilterOperation.EQUAL).value("Katina").valueType(ValueType.STRING).build();
    List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    ObjectMapper objectMapper = new ObjectMapper();
    String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    System.out.println(criteriaJSON);
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    MvcResult result = mockMvc
            .perform(get("/paginated").param("searchCriteriaList", criteriaJSON)
                    .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1)));
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testReadPenRequestPaginated_GivenLastNameFilter_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    SearchCriteria criteria = SearchCriteria.builder().key("legalLastName").operation(FilterOperation.EQUAL).value("Medling").valueType(ValueType.STRING).build();
    List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    ObjectMapper objectMapper = new ObjectMapper();
    String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    System.out.println(criteriaJSON);
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    MvcResult result = mockMvc
            .perform(get("/paginated").param("searchCriteriaList", criteriaJSON)
                    .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1)));
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testReadPenRequestPaginated_GivenSubmitDateBetween_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    String fromDate = "2020-04-01T00:00:01";
    String toDate = "2020-04-15T00:00:01";
    SearchCriteria criteria = SearchCriteria.builder().key("initialSubmitDate").operation(FilterOperation.BETWEEN).value(fromDate + "," + toDate).valueType(ValueType.DATE_TIME).build();
    List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    ObjectMapper objectMapper = new ObjectMapper();
    String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    System.out.println(criteriaJSON);
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    MvcResult result = mockMvc
            .perform(get("/paginated").param("searchCriteriaList", criteriaJSON)
                    .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2)));
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testReadPenRequestPaginated_GivenFirstAndLast_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    String fromDate = "2020-04-01T00:00:01";
    String toDate = "2020-04-15T00:00:01";
    SearchCriteria criteria = SearchCriteria.builder().key("initialSubmitDate").operation(FilterOperation.BETWEEN).value(fromDate + "," + toDate).valueType(ValueType.DATE_TIME).build();
    SearchCriteria criteriaFirstName = SearchCriteria.builder().key("legalFirstName").operation(FilterOperation.CONTAINS).value("a").valueType(ValueType.STRING).build();
    SearchCriteria criteriaLastName = SearchCriteria.builder().key("legalLastName").operation(FilterOperation.CONTAINS).value("o").valueType(ValueType.STRING).build();
    List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    criteriaList.add(criteriaFirstName);
    criteriaList.add(criteriaLastName);
    ObjectMapper objectMapper = new ObjectMapper();
    String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    System.out.println(criteriaJSON);
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    MvcResult result = mockMvc
            .perform(get("/paginated").param("searchCriteriaList", criteriaJSON)
                    .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1)));
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testReadPenRequestPaginated_LegalLastNameFilterIgnoreCase_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    SearchCriteria criteria = SearchCriteria.builder().key("legalLastName").operation(FilterOperation.CONTAINS_IGNORE_CASE).value("j").valueType(ValueType.STRING).build();
    List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    ObjectMapper objectMapper = new ObjectMapper();
    String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    System.out.println(criteriaJSON);
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    MvcResult result = mockMvc
            .perform(get("/paginated").param("searchCriteriaList", criteriaJSON)
                    .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1)));
  }
  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testReadPenRequestPaginated_digitalID_ShouldReturnStatusOk() throws Exception {
    var file = new File(
        Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    SearchCriteria criteria = SearchCriteria.builder().key("digitalID").operation(FilterOperation.EQUAL).value("fdf94a22-51e3-4816-8665-9f8571af1be4").valueType(ValueType.UUID).build();
    List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    var objectMapper = new ObjectMapper();
    String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    MvcResult result = mockMvc
        .perform(get("/paginated").param("searchCriteriaList", criteriaJSON)
            .contentType(APPLICATION_JSON))
        .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1)));
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
  public void testReadPenRequestPaginated_givenOperationTypeNull_ShouldReturnStatusOk() throws Exception {
    var file = new File(
        Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    SearchCriteria criteria = SearchCriteria.builder().key("digitalID").operation(null).value("fdf94a22-51e3-4816-8665-9f8571af1be4").valueType(ValueType.UUID).build();
    List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    var objectMapper = new ObjectMapper();
    String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    this.mockMvc.perform(get("/paginated").param("searchCriteriaList", criteriaJSON)
        .contentType(APPLICATION_JSON)).andDo(print()).andExpect(status().isBadRequest());
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

  private String dummyPenRequestCommentsJsonWithValidPenReqID(String penReqId) {
    return "{\n" +
            "  \"penRetrievalRequestID\": \"" + penReqId + "\",\n" +
            "  \"commentContent\": \"" + "comment1" + "\",\n" +
            "  \"commentTimestamp\": \"2020-02-09T00:00:00\"\n" +
            "}";
  }

}
