package com.finmate.application.resource;

import com.finmate.domain.service.QuizService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/api/quizzes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("**")
@Tag(name = "Quizzes", description = "Financial knowledge quizzes")
@SecurityRequirement(name = "bearerAuth")
public class QuizResource {

    private final QuizService service;
    private final JsonWebToken jwt;

    public QuizResource(QuizService service, JsonWebToken jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    @GET
    @Operation(summary = "Get all quizzes")
    public List<QuizService.QuizDto> getAll() {
        return service.getAll(currentUserId());
    }

    @POST
    @Path("/{id}/answer")
    @Operation(summary = "Submit an answer")
    public Response answer(@PathParam("id") UUID id, Map<String, Integer> body) {
        int selected = body.getOrDefault("selectedOption", -1);
        QuizService.AnswerResult result = service.answer(currentUserId(), id, selected);
        return Response.ok(result).build();
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Get user quiz statistics")
    public QuizService.UserStats getStats() {
        return service.getStats(currentUserId());
    }

    private UUID currentUserId() {
        return UUID.fromString(jwt.getSubject());
    }
}
