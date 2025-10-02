package com.crm.gym.api.repositories.interfaces;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface TemplateRepository<Id, Entity extends Identifiable<Id>>
        extends PreprocessingRepository<Id, Entity>, JpaRepository<Entity, Id>
{
    @Transactional
    default boolean deleteIfExists(Id entityId)
    {
        Optional<Entity> deletableEntity = findById(entityId);
        deletableEntity.ifPresent(e -> deleteById(entityId));
        return deletableEntity.isPresent();
    }
}
