package com.crm.gym.api.config;

import com.crm.gym.api.logging.controllers.TraceIdInterceptor;
import com.crm.gym.api.config.metrics.RequestCountMetricInterceptor;
import com.crm.gym.api.config.metrics.RequestLatencyMetricInterceptor;
import com.crm.gym.api.logging.controllers.RestLoggingInterceptor;

import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.List;
import java.util.stream.Stream;

@Configuration
public class WebConfig implements WebMvcConfigurer
{
    private TraceIdInterceptor traceIdInterceptor;
    private RestLoggingInterceptor restLoggingInterceptor;
    private RequestCountMetricInterceptor requestCountMetricInterceptor;
    private RequestLatencyMetricInterceptor requestLatencyMetricInterceptor;

    public WebConfig(TraceIdInterceptor traceIdInterceptor, RestLoggingInterceptor restLoggingInterceptor, RequestCountMetricInterceptor requestCountMetricInterceptor, RequestLatencyMetricInterceptor requestLatencyMetricInterceptor)
    {
        this.traceIdInterceptor = traceIdInterceptor;
        this.restLoggingInterceptor = restLoggingInterceptor;
        this.requestCountMetricInterceptor = requestCountMetricInterceptor;
        this.requestLatencyMetricInterceptor = requestLatencyMetricInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        List<String> pathPatterns = pathPatterns();

        Stream.of(
                traceIdInterceptor, restLoggingInterceptor,
                        requestCountMetricInterceptor, requestLatencyMetricInterceptor
        )
        .forEach(
                interceptor -> registry
                        .addInterceptor(interceptor)
                        .addPathPatterns(pathPatterns)
        );
    }

    private List<String> pathPatterns()
    {
        return List.of(
            "/trainees/**",
            "/trainers/**",
            "/trainings/**",
            "/trainingTypes/**"
        );
    }
}
