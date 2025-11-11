package com.crm.gym.api.config;

import com.crm.gym.api.factories.TrainerFactory;
import com.crm.gym.api.repositories.interfaces.TraineeRepository;
import com.crm.gym.api.repositories.interfaces.TrainerRepository;
import com.crm.gym.api.services.TrainerService;
import com.crm.gym.api.util.PasswordGenerator;
import com.crm.gym.api.util.PasswordGeneratorImpl;
import com.crm.gym.api.util.UsernameGenerator;
import com.crm.gym.api.util.UsernameGeneratorImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;

@TestConfiguration
public class TrainerServiceTestConfig
{
    @Bean
    public TrainerService trainerService(TrainerRepository trainerRepository, TrainerFactory trainerFactory, TraineeRepository traineeRepository)
    {
        return new TrainerService(trainerRepository, trainerFactory, traineeRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TrainerFactory trainerFactory(UsernameGenerator usernameGenerator, PasswordGenerator passwordGenerator)
    {
        return new TrainerFactory(usernameGenerator, passwordGenerator);
    }

    @Bean
    public UsernameGenerator usernameGenerator(TraineeRepository traineeRepository, TrainerRepository trainerRepository)
    {
        return new UsernameGeneratorImpl(traineeRepository, trainerRepository);
    }

    @Bean
    public PasswordGenerator passwordGenerator(Random random)
    {
        return new PasswordGeneratorImpl(random);
    }

    @Bean
    public Random random()
    {
        return new Random();
    }
}
