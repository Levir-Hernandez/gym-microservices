package com.crm.gym.api.repositories.interfaces;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.crm.gym.api.entities.User;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UserRepository<S extends User> extends TemplateRepository<UUID, S>
{
    Optional<S> findByUsername(String username);
    List<S> findByUsernameStartsWith(String prefix);
    boolean existsByUsername(String username);

    @Transactional
    default S updateByUsername(String username, S user)
    {
        UUID id = findByUsername(username).map(User::getId).orElse(null);

        if(Objects.nonNull(id))
        {
            user.setId(id);
            user.setUsername(username);
            user = save(user);
        }
        else {user = null;}

        return user;
    }
}
