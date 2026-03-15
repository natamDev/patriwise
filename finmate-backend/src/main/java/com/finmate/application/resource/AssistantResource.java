package com.finmate.application.resource;

import com.finmate.application.dto.AssistantChatDto;
import com.finmate.application.dto.AssistantRecommendationDto;
import com.finmate.application.dto.AssistantResponseDto;
import com.finmate.domain.model.AssistantRecommendation;
import com.finmate.domain.service.AssistantService;
import com.finmate.domain.service.CoachingService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;
import java.util.UUID;

@Path("/api/assistant")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("**")
@Tag(name = "Assistant", description = "AI financial assistant")
@SecurityRequirement(name = "bearerAuth")
public class AssistantResource {

    private final AssistantService service;
    private final CoachingService coachingService;
    private final JsonWebToken jwt;

    public AssistantResource(AssistantService service, CoachingService coachingService, JsonWebToken jwt) {
        this.service = service;
        this.coachingService = coachingService;
        this.jwt = jwt;
    }

    @POST
    @Path("/chat")
    @Operation(summary = "Send a message to the AI assistant")
    public Response chat(@Valid AssistantChatDto dto) {
        try {
            UUID userId = UUID.fromString(jwt.getSubject());
            AssistantService.ChatResult result = service.chat(userId, dto.getConversationId(), dto.getMessage());
            return Response.ok(new AssistantResponseDto(result.conversationId(), result.reply(), result.conceptCard())).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/coaching")
    @Operation(summary = "Generate a personalized coaching recommendation")
    public Response coaching() {
        try {
            UUID userId = UUID.fromString(jwt.getSubject());
            AssistantRecommendation rec = coachingService.generateCoaching(userId);
            return Response.ok(toDto(rec)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    private AssistantRecommendationDto toDto(AssistantRecommendation r) {
        AssistantRecommendationDto dto = new AssistantRecommendationDto();
        dto.setId(r.getId());
        dto.setRecommendationType(r.getRecommendationType());
        dto.setMessage(r.getMessage());
        dto.setSuggestedAction(r.getSuggestedAction());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }
}
