package ca.bc.gov.educ.api.penrequest.validator;

import ca.bc.gov.educ.api.penrequest.model.PenRequestMacroTypeCodeEntity;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestMacroRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestMacroTypeCodeRepository;
import ca.bc.gov.educ.api.penrequest.service.PenRequestMacroService;
import ca.bc.gov.educ.api.penrequest.struct.PenRequestMacro;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PenRequestMacroPayloadValidatorTest {

  @Autowired
  PenRequestMacroTypeCodeRepository penRequestMacroTypeCodeRepository;

  @Mock
  PenRequestMacroRepository penRequestMacroRepository;

  @Autowired
  PenRequestMacroService penRequestMacroService;
  @InjectMocks
  PenRequestMacroPayloadValidator penRequestMacroPayloadValidator;

  @Before
  public void before() {
    penRequestMacroTypeCodeRepository.deleteAll();
    penRequestMacroService = new PenRequestMacroService(penRequestMacroRepository, penRequestMacroTypeCodeRepository);
    penRequestMacroPayloadValidator = new PenRequestMacroPayloadValidator(penRequestMacroService);
  }

  @Test
  public void testValidatePayload_WhenMacroIdGivenForPost_ShouldAddAnErrorTOTheReturnedList() {
    val errorList = penRequestMacroPayloadValidator.validatePayload(getPenRequestMacroEntityFromJsonString(), true);
    assertEquals(2, errorList.size());
    assertEquals("macroId should be null for post operation.", errorList.get(0).getDefaultMessage());
  }
  @Test
  public void testValidatePayload_WhenMacroTypeCodeIsInvalid_ShouldAddAnErrorTOTheReturnedList() {
    val entity = getPenRequestMacroEntityFromJsonString();
    entity.setMacroId(null);
    val errorList = penRequestMacroPayloadValidator.validatePayload(entity, true);
    assertEquals(1, errorList.size());
    assertEquals("macroTypeCode Invalid.", errorList.get(0).getDefaultMessage());
  }

  @Test
  public void testValidatePayload_WhenMacroTypeCodeIsNotEffective_ShouldAddAnErrorTOTheReturnedList() {
    val macroTypeCode = createPenReqMacroTypeCode();
    macroTypeCode.setEffectiveDate(LocalDate.MAX);
    penRequestMacroTypeCodeRepository.save(macroTypeCode);
    val entity = getPenRequestMacroEntityFromJsonString();
    val errorList = penRequestMacroPayloadValidator.validatePayload(entity, false);
    assertEquals(1, errorList.size());
    assertEquals("macroTypeCode is not yet effective.", errorList.get(0).getDefaultMessage());
  }
  @Test
  public void testValidatePayload_WhenMacroTypeCodeIsExpired_ShouldAddAnErrorTOTheReturnedList() {
    PenRequestMacroTypeCodeEntity macroTypeCode = createPenReqMacroTypeCode();
    macroTypeCode.setEffectiveDate(LocalDate.now());
    macroTypeCode.setExpiryDate(LocalDate.now().minusDays(1));
    penRequestMacroTypeCodeRepository.save(macroTypeCode);
    val entity = getPenRequestMacroEntityFromJsonString();
    val errorList = penRequestMacroPayloadValidator.validatePayload(entity, false);
    assertEquals(1, errorList.size());
    assertEquals("macroTypeCode is expired.", errorList.get(0).getDefaultMessage());
  }
  private PenRequestMacroTypeCodeEntity createPenReqMacroTypeCode() {
    return PenRequestMacroTypeCodeEntity.builder()
            .createDate(LocalDateTime.now())
            .updateDate(LocalDateTime.now())
            .createUser("TEST")
            .updateUser("TEST")
            .description("TEST")
            .displayOrder(1)
            .effectiveDate(LocalDate.MIN)
            .expiryDate(LocalDate.MAX)
            .label("TEST")
            .macroTypeCode("REJECT")
            .build();
  }

  protected String dummyPenRequestMacroJson() {
    return " {\n" +
            "    \"createUser\": \"om\",\n" +
            "    \"updateUser\": \"om\",\n" +
            "    \"macroId\": \"7f000101-7151-1d84-8171-5187006c0000\",\n" +
            "    \"macroCode\": \"hi\",\n" +
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
