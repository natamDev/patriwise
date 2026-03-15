package com.finmate.application.resource;

import com.finmate.application.dto.BadgeStatusDto;
import com.finmate.domain.service.BadgeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Path("/api/badges")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("**")
@Tag(name = "Badges", description = "Educational badges")
@SecurityRequirement(name = "bearerAuth")
public class BadgeResource {

    private final BadgeService service;
    private final JsonWebToken jwt;

    public BadgeResource(BadgeService service, JsonWebToken jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    @GET
    @Operation(summary = "Get all badges with user unlock status")
    public List<BadgeStatusDto> getBadges() {
        UUID userId = UUID.fromString(jwt.getSubject());
        return service.checkAndGetBadges(userId).stream()
                .map(s -> new BadgeStatusDto(
                        s.badge().getId(),
                        s.badge().getName(),
                        s.badge().getDescription(),
                        s.badge().getIcon(),
                        s.unlocked(),
                        s.unlockedAt()))
                .toList();
    }
}
