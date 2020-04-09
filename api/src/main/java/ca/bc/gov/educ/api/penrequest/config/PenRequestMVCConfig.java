package ca.bc.gov.educ.api.penrequest.config;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PenRequestMVCConfig implements WebMvcConfigurer {

    @Getter(AccessLevel.PRIVATE)
    private final PenRequestRequestInterceptor penRequestRequestInterceptor;

    @Autowired
    public PenRequestMVCConfig(final PenRequestRequestInterceptor penRequestRequestInterceptor){
        this.penRequestRequestInterceptor = penRequestRequestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(penRequestRequestInterceptor).addPathPatterns("/**/**/");
    }
}
