package ca.bc.gov.educ.api.penRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan("ca.bc.gov.educ.api.penRequest")
@ComponentScan("ca.bc.gov.educ.api.penRequest")
@EnableCaching
public class PenRequestApiResourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PenRequestApiResourceApplication.class, args);
    }

}