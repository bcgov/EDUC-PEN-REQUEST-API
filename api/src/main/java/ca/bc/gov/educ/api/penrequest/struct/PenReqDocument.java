package ca.bc.gov.educ.api.penrequest.struct;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PenReqDocument extends BaseRequest implements Serializable {

  private static final long serialVersionUID = -7471585921119777006L;

  private String documentID;

  @Size(max = 10)
  @NotNull(message = "documentTypeCode cannot be null")
  private String documentTypeCode;

  @Size(max = 255)
  @NotNull(message = "fileName cannot be null")
  private String fileName;

  @Size(max = 255)
  @NotNull(message = "fileExtension cannot be null")
  private String fileExtension;

  @NotNull(message = "fileSize cannot be null")
  private Integer fileSize;

  @NotNull(message = "documentData cannot be null")
  @ToString.Exclude
  private String documentData;
}