package ca.bc.gov.educ.api.penrequest.controller;

import ca.bc.gov.educ.api.penrequest.props.ApplicationProperties;
import ca.bc.gov.educ.api.penrequest.struct.BaseRequest;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Date;

public abstract class BaseController {
  protected void setAuditColumns(@NotNull BaseRequest baseRequest) {
    if (StringUtils.isBlank(baseRequest.getCreateUser())) {
      baseRequest.setCreateUser(ApplicationProperties.CLIENT_ID);
    }
    if (StringUtils.isBlank(baseRequest.getUpdateUser())) {
      baseRequest.setUpdateUser(ApplicationProperties.CLIENT_ID);
    }
    if (baseRequest.getCreateDate() == null) {
      baseRequest.setCreateDate(new Date());
    }
    if (baseRequest.getUpdateDate() == null) {
      baseRequest.setUpdateDate(new Date());
    }
  }
}
