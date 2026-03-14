package com.finmate.application.resource;

import com.finmate.application.dto.AuthRequestDto;
import com.finmate.application.dto.TokenDto;
import com.finmate.domain.service.AuthService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Auth", description = "Registration and login")
public class AuthResource {

    private final AuthService service;

    public AuthResource(AuthService service) {
        this.service = service;
    }

    @POST
    @Path("/register")
    @Operation(summary = "Register", description = "Create a new account and return a JWT")
    public Response register(@Valid AuthRequestDto dto) {
        try {
            String token = service.register(dto.getEmail(), dto.getPassword());
            return Response.status(Response.Status.CREATED).entity(new TokenDto(token)).build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/login")
    @Operation(summary = "Login", description = "Authenticate and return a JWT")
    public Response login(@Valid AuthRequestDto dto) {
        try {
            String token = service.login(dto.getEmail(), dto.getPassword());
            return Response.ok(new TokenDto(token)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}
