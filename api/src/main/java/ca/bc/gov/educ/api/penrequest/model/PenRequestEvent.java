package ca.bc.gov.educ.api.penrequest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "PEN_RETRIEVAL_REQUEST_EVENT")
@Data
@DynamicUpdate
public class PenRequestEvent {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
          @Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy")})
  @Column(name = "EVENT_ID", unique = true, updatable = false, columnDefinition = "BINARY(16)")
  private UUID eventId;

  @Column(name = "EVENT_PAYLOAD", length = 4000)
  private String eventPayload;

  @Column(name = "EVENT_STATUS")
  private String eventStatus;

  @Column(name = "EVENT_TYPE")
  private String eventType;

  @Column(name = "CREATE_USER", updatable = false)
  String createUser;

  @Column(name = "CREATE_DATE", updatable = false)
  @PastOrPresent
  LocalDateTime createDate;

  @Column(name = "UPDATE_USER")
  String updateUser;

  @Column(name = "UPDATE_DATE")
  @PastOrPresent
  LocalDateTime updateDate;

  @Column(name = "SAGA_ID", updatable = false)
  private UUID sagaId;

  @Column(name = "EVENT_OUTCOME")
  private String eventOutcome;

  @Column(name = "REPLY_CHANNEL")
  private String replyChannel;
}