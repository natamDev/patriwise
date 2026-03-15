package com.finmate.application.resource;

import com.finmate.domain.exception.ConflictException;
import com.finmate.domain.exception.ResourceNotFoundException;
import com.finmate.domain.exception.UnauthorizedException;
import com.finmate.domain.exception.ValidationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException exception) {
        if (exception instanceof ResourceNotFoundException) {
            return buildResponse(Response.Status.NOT_FOUND, exception.getMessage());
        }
        if (exception instanceof ValidationException) {
            return buildResponse(Response.Status.BAD_REQUEST, exception.getMessage());
        }
        if (exception instanceof ConflictException) {
            return buildResponse(Response.Status.CONFLICT, exception.getMessage());
        }
        if (exception instanceof UnauthorizedException) {
            return buildResponse(Response.Status.UNAUTHORIZED, exception.getMessage());
        }
        return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    private Response buildResponse(Response.Status status, String message) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", message != null ? message : "Erreur interne du serveur."))
                .build();
    }
}
