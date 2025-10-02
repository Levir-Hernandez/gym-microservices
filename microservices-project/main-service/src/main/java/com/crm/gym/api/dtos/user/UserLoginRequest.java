package com.crm.gym.api.dtos.user;

public interface UserLoginRequest
{
    String getUsername();
    String getPassword();

    void setUsername(String username);
    void setPassword(String password);
}
