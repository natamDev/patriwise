package com.finmate.application.exception;

import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class JwtExceptionMapper implements ExceptionMapper<ParseException> {

    @Override
    public Response toResponse(ParseException exception) {
        String message = "Token invalide ou expiré.";

        if (exception.getMessage() != null) {
            if (exception.getMessage().contains("expired")) {
                message = "Votre session a expiré. Veuillez vous reconnecter.";
            } else if (exception.getMessage().contains("invalid")) {
                message = "Token invalide. Veuillez vous reconnecter.";
            }
        }

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(Map.of("error", message))
                .build();
    }
}
