package ca.bc.gov.educ.api.penrequest.controller;

import ca.bc.gov.educ.api.penrequest.constants.PenRequestStatusCode;
import ca.bc.gov.educ.api.penrequest.constants.v1.URL;
import ca.bc.gov.educ.api.penrequest.controller.v1.PenRequestController;
import ca.bc.gov.educ.api.penrequest.filter.FilterOperation;
import ca.bc.gov.educ.api.penrequest.mappers.v1.PenRequestEntityMapper;
import ca.bc.gov.educ.api.penrequest.model.v1.*;
import ca.bc.gov.educ.api.penrequest.repository.DocumentRepository;
import ca.bc.gov.educ.api.penrequest.repository.GenderCodeTableRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestStatusCodeTableRepository;
import ca.bc.gov.educ.api.penrequest.struct.v1.PenRequest;
import ca.bc.gov.educ.api.penrequest.struct.v1.SearchCriteria;
import ca.bc.gov.educ.api.penrequest.struct.v1.ValueType;
import ca.bc.gov.educ.api.penrequest.support.DocumentBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PenRequestControllerTest extends BasePenReqControllerTest {

  private static final PenRequestEntityMapper mapper = PenRequestEntityMapper.mapper;
  @Autowired
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
    this.genderRepo.save(this.createGenderCodeData());
  }

  @After
  public void after() {
    this.documentRepository.deleteAll();
    this.repository.deleteAll();
    this.genderRepo.deleteAll();
  }

  private GenderCodeEntity createGenderCodeData() {
    return GenderCodeEntity.builder().genderCode("M").description("Male")
            .effectiveDate(LocalDateTime.now()).expiryDate(LocalDateTime.MAX).displayOrder(1).label("label").createDate(LocalDateTime.now())
            .updateDate(LocalDateTime.now()).createUser("TEST").updateUser("TEST").build();
  }


  @Test
  public void testRetrievePenRequest_GivenRandomID_ShouldThrowEntityNotFoundException() throws Exception {
    this.mockMvc.perform(get(URL.BASE_URL+"/" + UUID.randomUUID())
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST"))))
            .andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  public void testRetrievePenRequest_GivenValidID_ShouldReturnOkStatus() throws Exception {
    final PenRequestEntity entity = this.repository.save(mapper.toModel(this.getPenRequestEntityFromJsonString()));
    this.mockMvc.perform(get(URL.BASE_URL+"/" + entity.getPenRequestID())
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST"))))
            .andDo(print()).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.penRequestID").value(entity.getPenRequestID().toString()));
  }

  @Test
  public void testRetrievePenRequest_WithWrongScope_ShouldReturnStatusForbidden() throws Exception {
    final PenRequestEntity entity = this.repository.save(mapper.toModel(this.getPenRequestEntityFromJsonString()));
    this.mockMvc.perform(get(URL.BASE_URL+"/" + entity.getPenRequestID())
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRONG_SCOPE"))))
        .andDo(print()).andExpect(status().isForbidden());
  }

  @Test
  public void testFindPenRequest_GivenOnlyPenInQueryParam_ShouldReturnOkStatusAndEntities() throws Exception {
    final PenRequestEntity entity = this.repository.save(mapper.toModel(this.getPenRequestEntityFromJsonString()));
    this.mockMvc.perform(get(URL.BASE_URL+"?pen=" + entity.getPen())
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST"))))
            .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1))).andExpect(MockMvcResultMatchers.jsonPath("$[0].pen").value(entity.getPen()));
  }

  @Test
  public void testRetrievePenRequest_GivenRandomDigitalIdAndStatusCode_ShouldReturnOkStatus() throws Exception {
    this.mockMvc.perform(get(URL.BASE_URL+"?digitalID=" + UUID.randomUUID() + "&status=" + "INT")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST"))))
            .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  public void testCreatePenRequest_GivenValidPayload_ShouldReturnStatusCreated() throws Exception {
    this.mockMvc.perform(post(URL.BASE_URL)
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_PEN_REQUEST")))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON).content(this.dummyPenRequestJson())).andDo(print()).andExpect(status().isCreated());
  }

  @Test
  public void testCreatePenRequest_GivenValidPayloadWithoutGenderCode_ShouldReturnStatusCreated() throws Exception {
    this.mockMvc.perform(post(URL.BASE_URL)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_PEN_REQUEST")))
      .contentType(APPLICATION_JSON)
      .accept(APPLICATION_JSON).content(this.dummyPenRequestJsonWithoutGenderCode())).andDo(print()).andExpect(status().isCreated());
  }

  @Test
  public void testCreatePenRequest_GivenInitialSubmitDateInPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post(URL.BASE_URL)
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_PEN_REQUEST")))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON).content(this.dummyPenRequestJsonWithInitialSubmitDate())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void testCreatePenRequest_GivenPenReqIdInPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post(URL.BASE_URL)
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_PEN_REQUEST")))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON).content(this.dummyPenRequestJsonWithInvalidPenReqID())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void testCreatePenRequest_LowercaseEmailVerifiedFlag_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post(URL.BASE_URL)
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_PEN_REQUEST")))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON).content(this.dummyPenRequestJsonWithInvalidEmailVerifiedFlag())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void testUpdatePenRequest_GivenInvalidPenReqIDInPayload_ShouldReturnStatusNotFound() throws Exception {
    this.mockMvc.perform(put(URL.BASE_URL)
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_PEN_REQUEST")))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON).content(this.dummyPenRequestJsonWithInvalidPenReqID())).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  public void testUpdatePenRequest_GivenValidPenReqIDInPayload_ShouldReturnStatusOk() throws Exception {
    final PenRequestEntity entity = this.repository.save(mapper.toModel(this.getPenRequestEntityFromJsonString()));
    final String penReqId = entity.getPenRequestID().toString();
    this.mockMvc.perform(put(URL.BASE_URL)
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_PEN_REQUEST")))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON).content(this.dummyPenRequestJsonWithValidPenReqID(penReqId))).andDo(print()).andExpect(status().isOk());
  }

  @Test
  public void testUpdatePenRequest_GivenInvalidDemogChangedInPayload_ShouldReturnBadRequest() throws Exception {
    final PenRequestEntity entity = this.repository.save(mapper.toModel(this.getPenRequestEntityFromJsonString()));
    final String penReqId = entity.getPenRequestID().toString();
    this.mockMvc.perform(put(URL.BASE_URL)
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_PEN_REQUEST")))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON).content(this.dummyPenRequestJsonWithInvalidDemogChanged(penReqId))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void testDeletePenRequest_GivenInvalidId_ShouldReturn404() throws Exception {
    this.mockMvc.perform(delete(URL.BASE_URL+"/" + UUID.randomUUID())
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "DELETE_PEN_REQUEST")))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  public void testDeletePenRequest_GivenValidId_ShouldReturn204() throws Exception {
    final PenRequestEntity entity = this.repository.save(mapper.toModel(this.getPenRequestEntityFromJsonString()));
    final String penReqId = entity.getPenRequestID().toString();
    this.mockMvc.perform(delete(URL.BASE_URL+"/" + penReqId)
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "DELETE_PEN_REQUEST")))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  public void testDeletePenRequest_GivenValidIdWithAssociations_ShouldReturn204() throws Exception {
    final PenRequestEntity penRequestEntity = mapper.toModel(this.getPenRequestEntityFromJsonString());
    penRequestEntity.setPenRequestComments(this.createPenRequestComments(penRequestEntity));
    final PenRequestEntity entity = this.repository.save(penRequestEntity);
    final DocumentEntity document = new DocumentBuilder()
            .withoutDocumentID()
            //.withoutCreateAndUpdateUser()
            .withPenRequest(entity)
            .withTypeCode("CAPASSPORT")
            .build();
    this.documentRepository.save(document);
    final String penReqId = entity.getPenRequestID().toString();
    this.mockMvc.perform(delete(URL.BASE_URL+"/" + penReqId)
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "DELETE_PEN_REQUEST")))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)).andDo(print()).andExpect(status().isNoContent());
  }

  private Set<PenRequestCommentsEntity> createPenRequestComments(final PenRequestEntity penRequestEntity) {
    final Set<PenRequestCommentsEntity> commentsEntitySet = new HashSet<>();
    final PenRequestCommentsEntity penRequestCommentsEntity = new PenRequestCommentsEntity();
    penRequestCommentsEntity.setPenRequestEntity(penRequestEntity);
    penRequestCommentsEntity.setCommentContent("hi");
    penRequestCommentsEntity.setCommentTimestamp(LocalDateTime.now());
    commentsEntitySet.add(penRequestCommentsEntity);
    return commentsEntitySet;
  }

  @Test
  public void testReadPenRequestStatus_Always_ShouldReturnStatusOkAndAllDataFromDB() throws Exception {
    this.penRequestStatusCodeTableRepo.save(this.createPenReqStatus());
    this.mockMvc.perform(get(URL.BASE_URL+URL.STATUSES)
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST_STATUSES"))))
            .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  public void testReadPenRequestPaginated_Always_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(this.getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    final List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    this.repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    final MvcResult result = this.mockMvc
            .perform(get(URL.BASE_URL+URL.PAGINATED+"?pageSize=2")
                    .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST")))
                    .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2)));
  }

  @Test
  public void testReadPenRequestPaginated_whenNoDataInDB_ShouldReturnStatusOk() throws Exception {
    final MvcResult result = this.mockMvc
            .perform(get(URL.BASE_URL+URL.PAGINATED)
                    .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST")))
                    .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(0)));
  }
  @Test
  public void testReadPenRequestPaginatedWithSorting_Always_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(this.getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    final List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    this.repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    final Map<String, String> sortMap = new HashMap<>();
    sortMap.put("legalLastName", "ASC");
    sortMap.put("legalFirstName", "DESC");
    final String sort = new ObjectMapper().writeValueAsString(sortMap);
    final MvcResult result = this.mockMvc
            .perform(get(URL.BASE_URL+URL.PAGINATED)
                    .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST")))
                    .param("pageNumber","1").param("pageSize", "5").param("sort", sort)
                    .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(5)));
  }

  @Test
  public void testReadPenRequestPaginated_GivenFirstNameFilter_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(this.getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    final List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    final SearchCriteria criteria = SearchCriteria.builder().key("legalFirstName").operation(FilterOperation.EQUAL).value("Katina").valueType(ValueType.STRING).build();
    final List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    final ObjectMapper objectMapper = new ObjectMapper();
    final String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    System.out.println(criteriaJSON);
    this.repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    final MvcResult result = this.mockMvc
            .perform(get(URL.BASE_URL+URL.PAGINATED)
                    .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST")))
                    .param("searchCriteriaList", criteriaJSON)
                    .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1)));
  }

  @Test
  public void testReadPenRequestPaginated_GivenLastNameFilter_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(this.getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    final List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    final SearchCriteria criteria = SearchCriteria.builder().key("legalLastName").operation(FilterOperation.EQUAL).value("Medling").valueType(ValueType.STRING).build();
    final List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    final ObjectMapper objectMapper = new ObjectMapper();
    final String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    System.out.println(criteriaJSON);
    this.repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    final MvcResult result = this.mockMvc
            .perform(get(URL.BASE_URL+URL.PAGINATED)
                    .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST")))
                    .param("searchCriteriaList", criteriaJSON)
                    .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1)));
  }

  @Test
  public void testReadPenRequestPaginated_GivenSubmitDateBetween_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(this.getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    final List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    final String fromDate = "2020-04-01T00:00:01";
    final String toDate = "2020-04-15T00:00:01";
    final SearchCriteria criteria = SearchCriteria.builder().key("initialSubmitDate").operation(FilterOperation.BETWEEN).value(fromDate + "," + toDate).valueType(ValueType.DATE_TIME).build();
    final List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    final ObjectMapper objectMapper = new ObjectMapper();
    final String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    System.out.println(criteriaJSON);
    this.repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    final MvcResult result = this.mockMvc
            .perform(get(URL.BASE_URL+URL.PAGINATED)
                    .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST")))
                    .param("searchCriteriaList", criteriaJSON)
                    .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2)));
  }

  @Test
  public void testReadPenRequestPaginated_GivenFirstAndLast_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(this.getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    final List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    final String fromDate = "2020-04-01T00:00:01";
    final String toDate = "2020-04-15T00:00:01";
    final SearchCriteria criteria = SearchCriteria.builder().key("initialSubmitDate").operation(FilterOperation.BETWEEN).value(fromDate + "," + toDate).valueType(ValueType.DATE_TIME).build();
    final SearchCriteria criteriaFirstName = SearchCriteria.builder().key("legalFirstName").operation(FilterOperation.CONTAINS).value("a").valueType(ValueType.STRING).build();
    final SearchCriteria criteriaLastName = SearchCriteria.builder().key("legalLastName").operation(FilterOperation.CONTAINS).value("o").valueType(ValueType.STRING).build();
    final List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    criteriaList.add(criteriaFirstName);
    criteriaList.add(criteriaLastName);
    final ObjectMapper objectMapper = new ObjectMapper();
    final String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    System.out.println(criteriaJSON);
    this.repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    final MvcResult result = this.mockMvc
            .perform(get(URL.BASE_URL+URL.PAGINATED)
                    .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST")))
                    .param("searchCriteriaList", criteriaJSON)
                    .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1)));
  }

  @Test
  public void testReadPenRequestPaginated_LegalLastNameFilterIgnoreCase_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(this.getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    final List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    final SearchCriteria criteria = SearchCriteria.builder().key("legalLastName").operation(FilterOperation.CONTAINS_IGNORE_CASE).value("j").valueType(ValueType.STRING).build();
    final List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    final ObjectMapper objectMapper = new ObjectMapper();
    final String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    System.out.println(criteriaJSON);
    this.repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    final MvcResult result = this.mockMvc
            .perform(get(URL.BASE_URL+URL.PAGINATED)
                    .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST")))
                    .param("searchCriteriaList", criteriaJSON)
                    .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1)));
  }
  @Test
  public void testReadPenRequestPaginated_digitalID_ShouldReturnStatusOk() throws Exception {
    final var file = new File(
        Objects.requireNonNull(this.getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    final List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    final SearchCriteria criteria = SearchCriteria.builder().key("digitalID").operation(FilterOperation.EQUAL).value("fdf94a22-51e3-4816-8665-9f8571af1be4").valueType(ValueType.UUID).build();
    final List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    final var objectMapper = new ObjectMapper();
    final String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    this.repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    final MvcResult result = this.mockMvc
        .perform(get(URL.BASE_URL+URL.PAGINATED)
                .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST")))
                .param("searchCriteriaList", criteriaJSON)
            .contentType(APPLICATION_JSON))
        .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1)));
  }

  @Test
  public void testReadPenRequestPaginated_givenOperationTypeNull_ShouldReturnStatusOk() throws Exception {
    final var file = new File(
        Objects.requireNonNull(this.getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    final List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    final SearchCriteria criteria = SearchCriteria.builder().key("digitalID").operation(null).value("fdf94a22-51e3-4816-8665-9f8571af1be4").valueType(ValueType.UUID).build();
    final List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    final var objectMapper = new ObjectMapper();
    final String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    this.repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    this.mockMvc.perform(get(URL.BASE_URL+URL.PAGINATED)
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST")))
            .param("searchCriteriaList", criteriaJSON)
        .contentType(APPLICATION_JSON)).andDo(print()).andExpect(status().isBadRequest());
  }


  @Test
  public void testGetStats_COMPLETIONS_LAST_WEEK_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setPenRequestStatusCode(PenRequestStatusCode.MANUAL.toString());
    var updateDate = LocalDateTime.now().minusDays(6);
    var dayName1 = updateDate.getDayOfWeek().name();
    entities.get(0).setStatusUpdateDate(updateDate.toString());
    entities.get(1).setPenRequestStatusCode(PenRequestStatusCode.AUTO.toString());
    updateDate = LocalDateTime.now();
    var dayName2 = updateDate.getDayOfWeek().name();
    entities.get(1).setStatusUpdateDate(updateDate.toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST_STATS")))
      .param("statsType", "COMPLETIONS_LAST_WEEK")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.completionsInLastWeek." + dayName1, is(1)))
      .andExpect(jsonPath("$.completionsInLastWeek." + dayName2, is(1)));
  }

  @Test
  public void testGetStats_COMPLETIONS_LAST_13_MONTH_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setPenRequestStatusCode(PenRequestStatusCode.MANUAL.toString());
    var updateDate = LocalDateTime.now();
    entities.get(0).setStatusUpdateDate(updateDate.toString());
    entities.get(1).setPenRequestStatusCode(PenRequestStatusCode.AUTO.toString());
    updateDate = LocalDateTime.now().withDayOfMonth(1).minusMonths(11);
    var month2 = updateDate.getMonth().toString();
    entities.get(1).setStatusUpdateDate(updateDate.toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST_STATS")))
      .param("statsType", "COMPLETIONS_LAST_13_MONTH")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.completionsInLastMonths.CURRENT" , is(1)))
      .andExpect(jsonPath("$.completionsInLastMonths." + month2, is(1)));
  }

  @Test
  public void testGetStats_PERCENT_GMP_REJECTED_TO_LAST_MONTH_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setPenRequestStatusCode(PenRequestStatusCode.REJECTED.toString());
    entities.get(0).setStatusUpdateDate(LocalDateTime.now().minusDays(30).toString());
    entities.get(1).setPenRequestStatusCode(PenRequestStatusCode.REJECTED.toString());
    entities.get(1).setStatusUpdateDate(LocalDateTime.now().toString());
    entities.get(2).setPenRequestStatusCode(PenRequestStatusCode.REJECTED.toString());
    entities.get(2).setStatusUpdateDate(LocalDateTime.now().toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST_STATS")))
      .param("statsType", "PERCENT_GMP_REJECTED_TO_LAST_MONTH")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.percentRejectedGmpToLastMonth", closeTo(100, 0.001)))
      .andExpect(jsonPath("$.gmpRejectedInCurrentMonth", is(2)));
  }

  @Test
  public void testGetStats_PERCENT_GMP_ABANDONED_TO_LAST_MONTH_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setPenRequestStatusCode(PenRequestStatusCode.ABANDONED.toString());
    entities.get(0).setStatusUpdateDate(LocalDateTime.now().minusDays(30).toString());
    entities.get(1).setPenRequestStatusCode(PenRequestStatusCode.ABANDONED.toString());
    entities.get(1).setStatusUpdateDate(LocalDateTime.now().toString());
    entities.get(2).setPenRequestStatusCode(PenRequestStatusCode.ABANDONED.toString());
    entities.get(2).setStatusUpdateDate(LocalDateTime.now().toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST_STATS")))
      .param("statsType", "PERCENT_GMP_ABANDONED_TO_LAST_MONTH")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.percentAbandonedGmpToLastMonth", closeTo(100, 0.001)))
      .andExpect(jsonPath("$.gmpAbandonedInCurrentMonth", is(2)));
  }

  @Test
  public void testGetStats_PERCENT_GMP_COMPLETION_TO_LAST_MONTH_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setPenRequestStatusCode(PenRequestStatusCode.MANUAL.toString());
    entities.get(0).setStatusUpdateDate(LocalDateTime.now().minusDays(30).toString());
    entities.get(1).setPenRequestStatusCode(PenRequestStatusCode.MANUAL.toString());
    entities.get(1).setStatusUpdateDate(LocalDateTime.now().toString());
    entities.get(2).setPenRequestStatusCode(PenRequestStatusCode.MANUAL.toString());
    entities.get(2).setStatusUpdateDate(LocalDateTime.now().toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST_STATS")))
      .param("statsType", "PERCENT_GMP_COMPLETION_TO_LAST_MONTH")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.percentCompletedGmpToLastMonth", closeTo(100, 0.001)))
      .andExpect(jsonPath("$.gmpCompletedInCurrentMonth", is(2)));
  }

  @Test
  public void testGetStats_PERCENT_GMP_COMPLETED_WITH_DOCUMENTS_TO_LAST_MONTH_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setPenRequestStatusCode(PenRequestStatusCode.MANUAL.toString());
    entities.get(0).setStatusUpdateDate(LocalDateTime.now().toString());
    var PenRequests = repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    DocumentEntity document = new DocumentBuilder()
      .withoutDocumentID()
      .withPenRequest(PenRequests.get(0))
      .withTypeCode("CAPASSPORT")
      .build();
    this.documentRepository.save(document);

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST_STATS")))
      .param("statsType", "PERCENT_GMP_COMPLETED_WITH_DOCUMENTS_TO_LAST_MONTH")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.percentGmpCompletedWithDocumentsToLastMonth", closeTo(1, 0.001)))
      .andExpect(jsonPath("$.gmpCompletedWithDocsInCurrentMonth", is(1)));
  }

  @Test
  public void testGetStats_ALL_STATUSES_LAST_12_MONTH_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setPenRequestStatusCode(PenRequestStatusCode.MANUAL.toString());
    entities.get(0).setStatusUpdateDate(LocalDateTime.now().minusMonths(11).toString());
    entities.get(1).setPenRequestStatusCode(PenRequestStatusCode.MANUAL.toString());
    entities.get(1).setStatusUpdateDate(LocalDateTime.now().toString());
    entities.get(2).setPenRequestStatusCode(PenRequestStatusCode.RETURNED.toString());
    entities.get(2).setStatusUpdateDate(LocalDateTime.now().minusMonths(3).toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST_STATS")))
      .param("statsType", "ALL_STATUSES_LAST_12_MONTH")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.allStatsLastTwelveMonth.MANUAL", is(2)))
      .andExpect(jsonPath("$.allStatsLastTwelveMonth.RETURNED", is(1)));
  }

  @Test
  public void testGetStats_ALL_STATUSES_LAST_6_MONTH_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setPenRequestStatusCode(PenRequestStatusCode.MANUAL.toString());
    entities.get(0).setStatusUpdateDate(LocalDateTime.now().minusMonths(5).toString());
    entities.get(1).setPenRequestStatusCode(PenRequestStatusCode.MANUAL.toString());
    entities.get(1).setStatusUpdateDate(LocalDateTime.now().toString());
    entities.get(2).setPenRequestStatusCode(PenRequestStatusCode.RETURNED.toString());
    entities.get(2).setStatusUpdateDate(LocalDateTime.now().minusMonths(3).toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST_STATS")))
      .param("statsType", "ALL_STATUSES_LAST_6_MONTH")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.allStatsLastSixMonth.MANUAL", is(2)))
      .andExpect(jsonPath("$.allStatsLastSixMonth.RETURNED", is(1)));
  }

  @Test
  public void testGetStats_ALL_STATUSES_LAST_1_MONTH_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setPenRequestStatusCode(PenRequestStatusCode.MANUAL.toString());
    entities.get(0).setStatusUpdateDate(LocalDateTime.now().minusDays(1).minusMonths(1).toString());
    entities.get(1).setPenRequestStatusCode(PenRequestStatusCode.MANUAL.toString());
    entities.get(1).setStatusUpdateDate(LocalDateTime.now().toString());
    entities.get(2).setPenRequestStatusCode(PenRequestStatusCode.RETURNED.toString());
    entities.get(2).setStatusUpdateDate(LocalDateTime.now().minusMonths(1).toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST_STATS")))
      .param("statsType", "ALL_STATUSES_LAST_1_MONTH")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.allStatsLastOneMonth.MANUAL", is(2)))
      .andExpect(jsonPath("$.allStatsLastOneMonth.RETURNED", is(1)));
  }

  @Test
  public void testGetStats_ALL_STATUSES_LAST_1_WEEK_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_pen_requests.json")).getFile()
    );
    List<PenRequest> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setPenRequestStatusCode(PenRequestStatusCode.MANUAL.toString());
    entities.get(0).setStatusUpdateDate(LocalDateTime.now().minusDays(6).toString());
    entities.get(1).setPenRequestStatusCode(PenRequestStatusCode.MANUAL.toString());
    entities.get(1).setStatusUpdateDate(LocalDateTime.now().toString());
    entities.get(2).setPenRequestStatusCode(PenRequestStatusCode.RETURNED.toString());
    entities.get(2).setStatusUpdateDate(LocalDateTime.now().minusDays(2).toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_PEN_REQUEST_STATS")))
      .param("statsType", "ALL_STATUSES_LAST_1_WEEK")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.allStatsLastOneWeek.MANUAL", is(2)))
      .andExpect(jsonPath("$.allStatsLastOneWeek.RETURNED", is(1)));
  }


  private PenRequestStatusCodeEntity createPenReqStatus() {
    final PenRequestStatusCodeEntity entity = new PenRequestStatusCodeEntity();
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
