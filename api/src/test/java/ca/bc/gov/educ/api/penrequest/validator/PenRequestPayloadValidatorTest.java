package ca.bc.gov.educ.api.penrequest.validator;

import ca.bc.gov.educ.api.penrequest.model.GenderCodeEntity;
import ca.bc.gov.educ.api.penrequest.props.ApplicationProperties;
import ca.bc.gov.educ.api.penrequest.repository.*;
import ca.bc.gov.educ.api.penrequest.service.PenRequestService;
import ca.bc.gov.educ.api.penrequest.struct.PenRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PenRequestPayloadValidatorTest {
    private boolean isCreateOperation = false;
    @Mock
    PenRequestRepository repository;
    @Mock
    PenRequestStatusCodeTableRepository penRequestStatusCodeTableRepo;
    @Mock
    GenderCodeTableRepository genderCodeTableRepo;
    @Mock
    PenRequestService service;
    @Autowired
    ApplicationProperties properties;
    @InjectMocks
    PenRequestPayloadValidator penRequestPayloadValidator;
    @Mock
    private PenRequestCommentRepository penRequestCommentRepository;
    @Mock
    private DocumentRepository documentRepository;

    @Before
    public void before() {
        service = new PenRequestService(repository, penRequestCommentRepository, documentRepository, penRequestStatusCodeTableRepo, genderCodeTableRepo);
        penRequestPayloadValidator = new PenRequestPayloadValidator(service, properties);
    }

    @Test
    public void testValidateGenderCode_WhenGenderCodeDoesNotExistInDB_ShouldAddAnErrorTOTheReturnedList() {
        isCreateOperation = true;
        List<FieldError> errorList = new ArrayList<>();
        when(service.getGenderCodesList()).thenReturn(new ArrayList<>());
        PenRequest penRequest = getPenRequestEntityFromJsonString();
        penRequestPayloadValidator.validateGenderCode(penRequest, errorList);
        assertEquals(1, errorList.size());
        assertEquals("Invalid Gender Code.", errorList.get(0).getDefaultMessage());
    }

    @Test
    public void testValidateGenderCode_WhenGenderCodeExistInDBAndIsNotEffective_ShouldAddAnErrorTOTheReturnedList() {
        isCreateOperation = true;
        List<FieldError> errorList = new ArrayList<>();
        List<GenderCodeEntity> genderCodeEntities = new ArrayList<>();
        GenderCodeEntity entity = createGenderCodeData();
        entity.setEffectiveDate(LocalDateTime.MAX);
        genderCodeEntities.add(entity);
        when(service.getGenderCodesList()).thenReturn(genderCodeEntities);
        PenRequest penRequest = getPenRequestEntityFromJsonString();
        penRequestPayloadValidator.validateGenderCode(penRequest, errorList);
        assertEquals(1, errorList.size());
        assertEquals("Gender Code provided is not yet effective.", errorList.get(0).getDefaultMessage());
    }

    @Test
    public void testValidateGenderCode_WhenGenderCodeExistInDBAndIsExpired_ShouldAddAnErrorTOTheReturnedList() {
        isCreateOperation = true;
        List<FieldError> errorList = new ArrayList<>();
        List<GenderCodeEntity> genderCodeEntities = new ArrayList<>();
        GenderCodeEntity entity = createGenderCodeData();
        entity.setExpiryDate(LocalDateTime.MIN);
        genderCodeEntities.add(entity);
        when(service.getGenderCodesList()).thenReturn(genderCodeEntities);
        PenRequest penRequest = getPenRequestEntityFromJsonString();
        penRequestPayloadValidator.validateGenderCode(penRequest, errorList);
        assertEquals(1, errorList.size());
        assertEquals("Gender Code provided has expired.", errorList.get(0).getDefaultMessage());
    }

    @Test
    public void testValidatePayload_GivenPenRequestIDInCreate_ShouldAddAnErrorTOTheReturnedList() {
        isCreateOperation = true;
        List<GenderCodeEntity> genderCodeEntities = new ArrayList<>();
        genderCodeEntities.add(createGenderCodeData());
        when(service.getGenderCodesList()).thenReturn(genderCodeEntities);
        PenRequest penRequest = getPenRequestEntityFromJsonString();
        penRequest.setPenRequestID(UUID.randomUUID().toString());
        List<FieldError> errorList = penRequestPayloadValidator.validatePayload(penRequest, true);
        assertEquals(1, errorList.size());
        assertEquals("penRequestID should be null for post operation.", errorList.get(0).getDefaultMessage());
    }

    @Test
    public void testValidatePayload_GivenInitialSubmitDateInCreate_ShouldAddAnErrorTOTheReturnedList() {
        isCreateOperation = true;
        List<GenderCodeEntity> genderCodeEntities = new ArrayList<>();
        genderCodeEntities.add(createGenderCodeData());
        when(service.getGenderCodesList()).thenReturn(genderCodeEntities);
        PenRequest penRequest = getPenRequestEntityFromJsonString();
        penRequest.setInitialSubmitDate(LocalDateTime.now().toString());
        List<FieldError> errorList = penRequestPayloadValidator.validatePayload(penRequest, true);
        assertEquals(1, errorList.size());
        assertEquals("initialSubmitDate should be null for post operation.", errorList.get(0).getDefaultMessage());
    }

    @Test
    public void testValidatePayload_WhenBCSCAutoMatchIsInvalid_ShouldAddAnErrorTOTheReturnedList() {
        isCreateOperation = true;
        List<GenderCodeEntity> genderCodeEntities = new ArrayList<>();
        genderCodeEntities.add(createGenderCodeData());
        when(service.getGenderCodesList()).thenReturn(genderCodeEntities);
        PenRequest penRequest = getPenRequestEntityFromJsonString();
        penRequest.setBcscAutoMatchOutcome("junk");
        List<FieldError> errorList = penRequestPayloadValidator.validatePayload(penRequest, true);
        assertEquals(1, errorList.size());
        assertEquals("Invalid bcscAutoMatchOutcome. It should be one of :: [RIGHTPEN, WRONGPEN, NOMATCH, MANYMATCHES, ONEMATCH]", errorList.get(0).getDefaultMessage());
    }

    private GenderCodeEntity createGenderCodeData() {
        return GenderCodeEntity.builder().genderCode("M").description("Male")
                .effectiveDate(LocalDateTime.now()).expiryDate(LocalDateTime.MAX).displayOrder(1).label("label").createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now()).createUser("TEST").updateUser("TEST").build();
    }

    protected String dummyPenRequestJson() {
        return "{\"digitalID\":\"b1e0788a-7dab-4b92-af86-c678e411f1e3\",\"legalFirstName\":\"Chester\",\"legalMiddleNames\":\"Grestie\",\"legalLastName\":\"Baulk\",\"dob\":\"1952-10-31\",\"genderCode\":\"M\",\"email\":\"cbaulk0@bluehost.com\",\"emailVerified\":\"N\",\"currentSchool\":\"Xanthoparmelia wyomingica (Gyel.) Hale\"}";
    }

    protected PenRequest getPenRequestEntityFromJsonString() {
        try {
            return new ObjectMapper().readValue(dummyPenRequestJson(), PenRequest.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
