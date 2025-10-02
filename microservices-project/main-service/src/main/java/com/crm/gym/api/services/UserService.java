package com.crm.gym.api.services;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;

import com.crm.gym.api.entities.User;
import com.crm.gym.api.repositories.interfaces.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class UserService<S extends User, R extends UserRepository<S>>
        extends TemplateService<UUID, S, R>
{
    private PasswordEncoder passwordEncoder;

    public UserService(R repository)
    {
        super(repository);
    }

    @Override
    public S saveEntity(S user)
    {
        return executeWithPasswordEncoding(user, super::saveEntity);
    }

    @Override
    protected S updateEntity(UUID entityId, S user)
    {
        return executeWithPasswordEncoding(user, u -> super.updateEntity(entityId, u));
    }

    public S updateUserByUsername(String username, S user)
    {
        return executeWithPasswordEncoding(user, u -> repository.updateByUsername(username, u));
    }

    public S getUserByUsername(String username)
    {
        return repository.findByUsername(username).orElse(null);
    }

    public boolean login(String username, String password)
    {
        return Optional.ofNullable(getUserByUsername(username))
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    public boolean changePassword(String username, String oldPassword, String newPassword)
    {
        S user = getUserByUsername(username);

        if(Objects.isNull(user) || !passwordEncoder.matches(oldPassword, user.getPassword())) {return false;}

        user.setPassword(newPassword);
        updateUserByUsername(username, user); // <- check

        return true;
    }

    public Boolean activateUser(String username)
    {
        return updateUserIsActive(username, true);
    }

    public Boolean deactivateUser(String username)
    {
        return updateUserIsActive(username, false);
    }

    private Boolean updateUserIsActive(String username, Boolean isActive)
    {
        S user = getUserByUsername(username);
        if(Objects.isNull(user)){return null;}

        Boolean isActiveHasChanged = !user.getIsActive().equals(isActive);
        if(isActiveHasChanged)
        {
            user.setIsActive(isActive);
            repository.save(user);
        }

        return isActiveHasChanged;
    }

    private S executeWithPasswordEncoding(S user, UnaryOperator<S> userOperation)
    {
        String rawPassword = user.getPassword();
        if(Objects.isNull(rawPassword)){return userOperation.apply(user);} // Skip encoding when password is null

        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);

        user = userOperation.apply(user);
        if(Objects.nonNull(user)){user.setPassword(rawPassword);}

        return user;
    }

    @Autowired
    private void setPasswordEncoder(PasswordEncoder passwordEncoder)
    {
        this.passwordEncoder = passwordEncoder;
    }
}
