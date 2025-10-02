package com.crm.gym.api.repositories.implementations;

import com.crm.gym.api.repositories.interfaces.PreprocessingRepository;
import com.crm.gym.api.repositories.interfaces.Identifiable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Objects;

@Transactional
public abstract class TemplateRepositoryImpl<Id, Entity extends Identifiable<Id>>
    implements PreprocessingRepository<Id, Entity>
{
    @PersistenceContext
    protected EntityManager em;

    protected abstract Class<Entity> getEntityClass();

    @Override
    public Entity create(Entity entity)
    {
        entity.setId(null);
        return em.merge(entity);
    }

    @Override
    public Entity update(Id entityId, Entity entity)
    {
        Entity oldEntity = em.find(getEntityClass(), entityId);
        if(Objects.isNull(oldEntity)) {return null;}

        entity.setId(entityId);
        return em.merge(entity);
    }
}
