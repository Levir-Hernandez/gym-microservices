package com.crm.gym.api.config.util;

import com.crm.gym.api.util.PasswordGenerator;
import com.crm.gym.api.util.PasswordGeneratorImpl;
import com.crm.gym.api.util.UsernameGenerator;
import com.crm.gym.api.util.UsernameGeneratorImpl;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import com.crm.gym.api.repositories.interfaces.TraineeRepository;
import com.crm.gym.api.repositories.interfaces.TrainerRepository;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.client.NoOpResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;

@Configuration
public class UtilConfig {
    @Bean
    public UsernameGenerator usernameGenerator(@Lazy TraineeRepository traineeRepository,
                                               @Lazy TrainerRepository trainerRepository) {
        return new UsernameGeneratorImpl(traineeRepository, trainerRepository);
    }

    @Bean
    public PasswordGenerator passwordGenerator(Random random) {
        return new PasswordGeneratorImpl(random);
    }

    @Bean
    public Random random() {
        return new Random();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public ResponseErrorHandler responseErrorHandler()
    {
        return new NoOpResponseErrorHandler();
    }

    @Bean
    public RestTemplate restTemplate(ResponseErrorHandler responseErrorHandler)
    {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(responseErrorHandler);
        return restTemplate;
    }

    @Bean
    public List<String> entities()
    {
        return List.of(
            "trainees",
            "trainers",
            "trainings",
            "training-types"
        );
    }
}
