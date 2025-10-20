package com.crm.gym.api.config.jms;

import com.crm.gym.api.dtos.TrainerWorkloadRequest;
import com.crm.gym.api.entities.TrainerWorkloadSummary;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.DeliveryMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.QosSettings;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.Map;

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
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MappingJackson2MessageConverter jacksonMessageConverter,
            @Value("${jms.listener.reply-qos-settings.time-to-live:1000}") long replyTimeToLive
    )
    {
        DefaultJmsListenerContainerFactory factory = new TraceIdAwareJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonMessageConverter);
        factory.setReplyQosSettings(new QosSettings(DeliveryMode.NON_PERSISTENT, 4, replyTimeToLive));
        return factory;
    }
}