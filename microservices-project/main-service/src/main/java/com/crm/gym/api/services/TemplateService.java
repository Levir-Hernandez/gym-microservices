package com.crm.gym.api.services;

import java.util.List;
import com.crm.gym.api.repositories.interfaces.Identifiable;
import com.crm.gym.api.repositories.interfaces.TemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public abstract class TemplateService<Id,
        Entity extends Identifiable<Id>,
        Repository extends TemplateRepository<Id,Entity>>
{
    protected Repository repository;

    public TemplateService(Repository repository)
    {
        this.repository = repository;
    }

    public Entity saveEntity(Entity entity)
    {
        return repository.create(entity);
    }

    protected Entity updateEntity(Id entityId, Entity entity)
    {
        return repository.update(entityId, entity);
    }

    protected boolean deleteEntity(Id entityId)
    {
        return repository.deleteIfExists(entityId);
    }

    public Entity getEntityById(Id entityId)
    {
        return repository.findById(entityId).orElse(null);
    }

    public List<Entity> getAllEntities()
    {
        return repository.findAll();
    }

    public Page<Entity> getAllEntities(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    public long getEntitiesCount()
    {
        return repository.count();
    }
}
