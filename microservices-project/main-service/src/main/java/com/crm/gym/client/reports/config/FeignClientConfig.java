package com.crm.gym.client.reports.config;

import com.crm.gym.api.auth.services.JwtTokenService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Optional;

@Component
public class FeignClientConfig implements RequestInterceptor
{
    private JwtTokenService jwtTokenService;

    public FeignClientConfig(JwtTokenService jwtTokenService)
    {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public void apply(RequestTemplate requestTemplate)
    {
        String token = jwtTokenService.issueAccessToken("main-service", "TRAINER");
        requestTemplate.header("Authorization", "Bearer " + token);

        // Propagate the current traceId to downstream services
        Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(requestAttributes -> requestAttributes.getAttribute("traceId", RequestAttributes.SCOPE_REQUEST))
                .map(Object::toString)
                .ifPresent(traceId -> requestTemplate.header("X-Trace-Id", traceId));
    }
}
