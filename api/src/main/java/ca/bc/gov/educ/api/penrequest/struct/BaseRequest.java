package ca.bc.gov.educ.api.penrequest.struct;

import lombok.Data;

import javax.validation.constraints.Null;
import java.util.Date;

@Data
public abstract class BaseRequest {
  protected String createUser;
  protected String updateUser;
  @Null(message = "createDate should be null.")
  protected Date createDate;
  @Null(message = "updateDate should be null.")
  protected Date updateDate;
}
