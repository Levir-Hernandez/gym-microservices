package com.crm.gym.api.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class PermissionDeniedException extends ResponseStatusException
{
    private static final HttpStatusCode STATUS = HttpStatus.FORBIDDEN;
    private static final String REASON = "You don't have permission to access this resource";

    public PermissionDeniedException()
    {
        super(STATUS, REASON);
    }

    public PermissionDeniedException(Throwable cause)
    {
        super(STATUS, REASON, cause);
    }
}