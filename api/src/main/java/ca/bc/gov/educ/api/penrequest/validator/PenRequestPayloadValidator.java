package ca.bc.gov.educ.api.penrequest.validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import ca.bc.gov.educ.api.penrequest.model.GenderCodeEntity;
import ca.bc.gov.educ.api.penrequest.service.PenRequestService;
import ca.bc.gov.educ.api.penrequest.struct.PenRequest;
import lombok.AccessLevel;
import lombok.Getter;

@Component
public class PenRequestPayloadValidator {

  public static final String GENDER_CODE = "genderCode";
  @Getter(AccessLevel.PRIVATE)
  private final PenRequestService penRequestService;

  @Autowired
  public PenRequestPayloadValidator(final PenRequestService penRequestService) {
    this.penRequestService = penRequestService;
  }

  public List<FieldError> validatePayload(PenRequest penRequest, boolean isCreateOperation) {
    final List<FieldError> apiValidationErrors = new ArrayList<>();
    if (isCreateOperation && penRequest.getPenRequestID() != null) {
      apiValidationErrors.add(createFieldError("penRequestID", penRequest.getPenRequestID(), "penRequestID should be null for post operation."));
    }
    
    if (isCreateOperation && penRequest.getInitialSubmitDate() != null) {
        apiValidationErrors.add(createFieldError("initialSubmitDate", penRequest.getPenRequestID(), "initialSubmitDate should be null for post operation."));
    }
    validateGenderCode(penRequest, apiValidationErrors);
    return apiValidationErrors;
  }

  protected void validateGenderCode(PenRequest penRequest, List<FieldError> apiValidationErrors) {
	if(penRequest.getGenderCode() != null) {
	    Optional<GenderCodeEntity> genderCodeEntity = penRequestService.findGenderCode(penRequest.getGenderCode());
	   	if (!genderCodeEntity.isPresent()) {
	      apiValidationErrors.add(createFieldError(GENDER_CODE, penRequest.getGenderCode(), "Invalid Gender Code."));
	   	} else if (genderCodeEntity.get().getEffectiveDate() != null && genderCodeEntity.get().getEffectiveDate().isAfter(LocalDateTime.now())) {
	      apiValidationErrors.add(createFieldError(GENDER_CODE, penRequest.getGenderCode(), "Gender Code provided is not yet effective."));
	    } else if (genderCodeEntity.get().getExpiryDate() != null && genderCodeEntity.get().getExpiryDate().isBefore(LocalDateTime.now())) {
	      apiValidationErrors.add(createFieldError(GENDER_CODE, penRequest.getGenderCode(), "Gender Code provided has expired."));
	    }
	}
  }
  
  private FieldError createFieldError(String fieldName, Object rejectedValue, String message) {
    return new FieldError("penRequest", fieldName, rejectedValue, false, null, null, message);
  }

}