package com.crm.gym.api.services;

import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.factories.TraineeFactory;
import org.springframework.stereotype.Service;
import com.crm.gym.api.repositories.interfaces.TraineeRepository;

@Service
public class TraineeService extends UserService<Trainee, TraineeRepository>
{
    private TraineeFactory traineeFactory;

    public TraineeService(TraineeRepository repository, TraineeFactory traineeFactory)
    {
        super(repository);
        this.traineeFactory = traineeFactory;
    }

    @Override
    public Trainee saveEntity(Trainee trainee)
    {
        trainee = traineeFactory.recreate(trainee);
        return super.saveEntity(trainee);
    }

    public boolean deleteTraineeByUsername(String username)
    {
        return repository.deleteByUsernameIfExists(username);
    }
}
