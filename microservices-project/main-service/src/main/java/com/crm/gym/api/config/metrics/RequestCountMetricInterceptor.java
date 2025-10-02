package com.crm.gym.api.config.metrics;

import io.micrometer.core.instrument.Counter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.servlet.HandlerInterceptor;

import org.springframework.stereotype.Component;

@Component
public class RequestCountMetricInterceptor implements HandlerInterceptor
{
    private Counter requests;

    public RequestCountMetricInterceptor(MeterRegistry meterRegistry)
    {
        requests = Counter.builder("http_requests")
                .description("Total number of HTTP requests")
                .register(meterRegistry);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler)
    {
        requests.increment();
        return true;
    }
}