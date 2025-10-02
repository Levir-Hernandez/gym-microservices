package com.crm.gym.api.util;

import java.util.Collection;
import java.util.stream.Stream;
import com.crm.gym.api.entities.User;
import com.crm.gym.api.repositories.interfaces.TraineeRepository;
import com.crm.gym.api.repositories.interfaces.TrainerRepository;

import lombok.Setter;

public class UsernameGeneratorImpl implements UsernameGenerator
{
    private TraineeRepository traineeRepository;
    private TrainerRepository trainerRepository;

    @Setter private User user;

    public UsernameGeneratorImpl(TraineeRepository traineeRepository, TrainerRepository trainerRepository)
    {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Override
    public String generateUsername()
    {
        String username = user.getFirstname() + "." + user.getLastname();

        final String prefix = username;
        long counter = Stream.of(traineeRepository, trainerRepository)
                .map(r -> r.findByUsernameStartsWith(prefix))
                .flatMap(Collection::stream).map(User::getUsername) // Fetch all Users that collide with the username
                .map(fullUsername -> fullUsername.substring(prefix.length())) // Extract its numeric suffix
                .map(suffix -> "0"+suffix).map(Long::parseLong) // Handle empty suffix as zero
                .max(Long::compareTo)
                .map(maxSuffix -> maxSuffix+1) // If collisions exist (counter = maxSuffix + 1)
                .orElse(0L); // If no collisions (counter = 0)

        if(counter > 0){username += counter;}

        return username;
    }
}
