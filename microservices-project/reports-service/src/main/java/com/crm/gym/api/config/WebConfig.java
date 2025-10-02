package com.crm.gym.api.config;

import com.crm.gym.api.logging.controllers.RestLoggingInterceptor;
import com.crm.gym.api.logging.controllers.TraceIdInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.stream.Stream;

@Configuration
public class WebConfig implements WebMvcConfigurer
{
    private TraceIdInterceptor traceIdInterceptor;
    private RestLoggingInterceptor restLoggingInterceptor;

    public WebConfig(TraceIdInterceptor traceIdInterceptor, RestLoggingInterceptor restLoggingInterceptor)
    {
        this.traceIdInterceptor = traceIdInterceptor;
        this.restLoggingInterceptor = restLoggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        Stream.of(
                        traceIdInterceptor, restLoggingInterceptor
                )
                .forEach(
                        interceptor -> registry
                                .addInterceptor(interceptor)
                                .addPathPatterns("/trainers/**")
                );
    }
}
