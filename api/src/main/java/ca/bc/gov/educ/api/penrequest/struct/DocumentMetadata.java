package ca.bc.gov.educ.api.penrequest.struct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentMetadata implements Serializable {

  private static final long serialVersionUID = -7471585921119777006L;

  private String documentID;

  @NotNull(message = "documentTypeCode cannot be null")
  private String documentTypeCode;

  @NotNull(message = "fileName cannot be null")
  private String fileName;

  @NotNull(message = "fileExtension cannot be null")
  private String fileExtension;

  @NotNull(message = "fileSize cannot be null")
  private Integer fileSize;

  @Null(message = "Create Date Should be null")
  private Date createDate;
}
