package com.crm.gym.api.util;

import com.crm.gym.api.entities.User;

public interface UsernameGenerator
{
    void setUser(User user);
    String generateUsername();
}
