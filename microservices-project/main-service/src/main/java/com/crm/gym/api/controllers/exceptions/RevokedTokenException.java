package com.crm.gym.api.controllers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RevokedTokenException extends ResponseStatusException
{
    public RevokedTokenException()
    {
        super(HttpStatus.FORBIDDEN, "Token has been revoked");
    }
}