package com.crm.gym.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class InvalidTokenException extends ResponseStatusException
{
    private static final HttpStatusCode STATUS = HttpStatus.FORBIDDEN;
    private static final String REASON = "Invalid or expired token";

    public InvalidTokenException()
    {
        super(STATUS, REASON);
    }

    public InvalidTokenException(Throwable cause)
    {
      super(STATUS, REASON, cause);
    }
}