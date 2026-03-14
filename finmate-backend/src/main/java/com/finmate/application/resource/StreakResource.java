package com.finmate.application.resource;

import com.finmate.application.dto.SavingStreakDto;
import com.finmate.domain.model.SavingStreak;
import com.finmate.domain.service.SavingStreakService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/api/streak")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("**")
@Tag(name = "Streak", description = "Saving streak")
@SecurityRequirement(name = "bearerAuth")
public class StreakResource {

    private final SavingStreakService service;
    private final JsonWebToken jwt;

    public StreakResource(SavingStreakService service, JsonWebToken jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    @GET
    @Operation(summary = "Get saving streak")
    public SavingStreakDto getStreak() {
        UUID userId = UUID.fromString(jwt.getSubject());
        SavingStreak streak = service.computeAndSave(userId);
        SavingStreakDto dto = new SavingStreakDto();
        dto.setCurrentStreak(streak.getCurrentStreak());
        dto.setLongestStreak(streak.getLongestStreak());
        return dto;
    }
}
