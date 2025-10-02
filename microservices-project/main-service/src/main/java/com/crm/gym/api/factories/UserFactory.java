package com.crm.gym.api.factories;

import com.crm.gym.api.repositories.interfaces.Identifiable;

public interface UserFactory<Id, Entity extends Identifiable<Id>>
{
    Entity recreate(Entity entity);
}
