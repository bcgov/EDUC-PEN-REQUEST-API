package ca.bc.gov.educ.api.penrequest.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
@EnableAsync
@Profile("!test")
public class AsyncConfiguration {
  /**
   * Thread pool task executor executor.
   *
   * @return the executor
   */
  @Bean(name = "subscriberExecutor")
  public Executor threadPoolTaskExecutor() {
    ThreadFactory namedThreadFactory =
      new ThreadFactoryBuilder().setNameFormat("message-subscriber-%d").build();
    return Executors.newFixedThreadPool(2, namedThreadFactory);
  }

  /**
   * Controller task executor executor.
   *
   * @return the executor
   */
  @Bean(name = "taskExecutor")
  public Executor controllerTaskExecutor() {
    ThreadFactory namedThreadFactory =
      new ThreadFactoryBuilder().setNameFormat("async-executor-%d").build();
    return Executors.newFixedThreadPool(8, namedThreadFactory);
  }
}
