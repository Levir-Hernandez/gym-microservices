package com.crm.gym.api.logging.factories;

import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.factories.TrainerFactory;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TrainerFactoryLogger extends UserFactoryLogger<Trainer>
{
    public TrainerFactoryLogger()
    {
        super(LoggerFactory.getLogger(TrainerFactory.class));
    }

    @Override
    protected Class<Trainer> getUserClass() {return Trainer.class;}

    @Override
    @Pointcut("target(com.crm.gym.factories.TrainerFactory)")
    public void target_UserFactory() {}
}
