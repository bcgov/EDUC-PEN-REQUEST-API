package ca.bc.gov.educ.api.penrequest.support;

import ca.bc.gov.educ.api.penrequest.messaging.MessagePublisher;
import ca.bc.gov.educ.api.penrequest.messaging.MessageSubscriber;
import ca.bc.gov.educ.api.penrequest.poll.EventTaskScheduler;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class MockConfiguration {
  @Bean
  @Primary
  public MessagePublisher messagePublisher() {
    return Mockito.mock(MessagePublisher.class);
  }

  @Bean
  @Primary
  public MessageSubscriber messageSubscriber() {
    return Mockito.mock(MessageSubscriber.class);
  }

  @Bean
  @Primary
  public EventTaskScheduler eventTaskScheduler() {
    return Mockito.mock(EventTaskScheduler.class);
  }

}