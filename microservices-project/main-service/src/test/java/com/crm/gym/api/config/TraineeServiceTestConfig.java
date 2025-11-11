package com.crm.gym.api.config;

import com.crm.gym.api.factories.TraineeFactory;
import com.crm.gym.api.repositories.interfaces.TraineeRepository;
import com.crm.gym.api.repositories.interfaces.TrainerRepository;
import com.crm.gym.api.services.TraineeService;
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
public class TraineeServiceTestConfig
{
    @Bean
    public TraineeService traineeService(TraineeRepository traineeRepository, TraineeFactory traineeFactory)
    {
        return new TraineeService(traineeRepository, traineeFactory);
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TraineeFactory traineeFactory(UsernameGenerator usernameGenerator, PasswordGenerator passwordGenerator)
    {
        return new TraineeFactory(usernameGenerator, passwordGenerator);
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
