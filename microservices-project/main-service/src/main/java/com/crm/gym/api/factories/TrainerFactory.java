package com.crm.gym.api.factories;

import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.entities.TrainingType;
import com.crm.gym.api.util.PasswordGenerator;
import com.crm.gym.api.util.UsernameGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TrainerFactory implements UserFactory<UUID, Trainer>
{
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    public TrainerFactory(UsernameGenerator usernameGenerator, PasswordGenerator passwordGenerator)
    {
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
    }

    public Trainer create(String firstname, String lastname, Boolean isActive,
                                 TrainingType specialization)
    {
        Trainer trainer = new Trainer(null, // will be set by JPA
                firstname, lastname,
                null,
                passwordGenerator.generatePassword(),
                isActive, specialization);

        usernameGenerator.setUser(trainer);
        trainer.setUsername(usernameGenerator.generateUsername());

        return trainer;
    }

    @Override
    public Trainer recreate(Trainer trainer)
    {
        return create(trainer.getFirstname(), trainer.getLastname(),
                trainer.getIsActive(), trainer.getSpecialization());
    }
}
