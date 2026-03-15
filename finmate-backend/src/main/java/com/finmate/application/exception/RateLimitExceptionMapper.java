package com.finmate.application.exception;

import com.finmate.domain.exception.RateLimitException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class RateLimitExceptionMapper implements ExceptionMapper<RateLimitException> {

    @Override
    public Response toResponse(RateLimitException exception) {
        return Response.status(429) // Too Many Requests
                .entity(Map.of("error", exception.getMessage()))
                .build();
    }
}
