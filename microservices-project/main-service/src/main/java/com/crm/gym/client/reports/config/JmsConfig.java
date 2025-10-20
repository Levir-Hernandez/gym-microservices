package com.crm.gym.client.reports.config;

import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import com.crm.gym.client.reports.dtos.TrainerWorkloadRequest;
import com.crm.gym.client.reports.dtos.TrainerWorkloadSummary;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.core.MessagePostProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.MDC;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.UUID;
import java.util.Optional;
import java.util.function.Supplier;

@Configuration
public class JmsConfig
{
    @Bean
    public MappingJackson2MessageConverter jacksonMessageConverter(ObjectMapper objectMapper)
    {
        MappingJackson2MessageConverter jacksonMessageConverter = new MappingJackson2MessageConverter();
        jacksonMessageConverter.setTargetType(MessageType.TEXT);
        jacksonMessageConverter.setTypeIdPropertyName("_type");
        jacksonMessageConverter.setObjectMapper(objectMapper);
        jacksonMessageConverter.setTypeIdMappings(Map.of(
                "TrainerWorkloadSummary", TrainerWorkloadSummary.class,
                "TrainerWorkloadRequest", TrainerWorkloadRequest.class
        ));
        return jacksonMessageConverter;
    }

    @Bean
    public MessagePostProcessor messagePostProcessor()
    {
        return message -> {
            String traceId = Optional.ofNullable(MDC.get("traceId"))
                    .orElse(UUID.randomUUID().toString());
            message.setJMSCorrelationID(traceId);
            return message;
        };
    }

    @Bean
    public Supplier<String> messageSelectorSupplier()
    {
        return () -> Optional.ofNullable(MDC.get("traceId"))
                .map(traceId -> String.format("JMSCorrelationID = '%s'", traceId))
                .orElse(null);
    }
}