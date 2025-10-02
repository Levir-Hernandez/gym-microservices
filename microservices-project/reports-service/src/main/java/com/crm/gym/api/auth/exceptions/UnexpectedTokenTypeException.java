package com.crm.gym.api.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnexpectedTokenTypeException extends ResponseStatusException
{
    public UnexpectedTokenTypeException()
    {
        super(HttpStatus.FORBIDDEN, "Invalid token type for this operation");
    }
}