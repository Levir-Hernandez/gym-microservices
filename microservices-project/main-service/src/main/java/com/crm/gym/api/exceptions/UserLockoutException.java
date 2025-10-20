package com.crm.gym.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserLockoutException extends ResponseStatusException
{
    public UserLockoutException()
    {
        super(HttpStatus.FORBIDDEN, "User is temporarily locked due to multiple failed login attempts");
    }
}
