package com.crm.gym.api.config.metrics;

import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.context.annotation.ScopedProxyMode;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestLatencyMetricInterceptor implements HandlerInterceptor
{
    private MeterRegistry meterRegistry;
    private Timer.Sample latencySample;

    public RequestLatencyMetricInterceptor(MeterRegistry meterRegistry)
    {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler)
    {
        latencySample = Timer.start(meterRegistry);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex)
    {
        String url = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
        latencySample.stop(
                Timer.builder("http_requests_latency")
                        .description("Latency of HTTP requests")
                        .tag("method", request.getMethod())
                        .tag("url", url)
                        .register(meterRegistry)
        );
    }
}
