package ca.bc.gov.educ.api.penrequest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PenRequestMVCConfig implements WebMvcConfigurer {

    private final RequestResponseInterceptor requestResponseInterceptor;

    @Autowired
    public PenRequestMVCConfig(final RequestResponseInterceptor requestResponseInterceptor){
        this.requestResponseInterceptor = requestResponseInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestResponseInterceptor).addPathPatterns("/**");
    }
}
