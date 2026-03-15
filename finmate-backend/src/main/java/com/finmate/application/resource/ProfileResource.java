package com.finmate.application.resource;

import com.finmate.application.dto.CreateFinancialProfileDto;
import com.finmate.application.dto.FinancialProfileDto;
import com.finmate.application.dto.UpdateFinancialProfileDto;
import com.finmate.domain.model.FinancialProfile;
import com.finmate.domain.service.FinancialProfileService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/api/profile")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
@Tag(name = "Profile", description = "Financial profile management")
@SecurityRequirement(name = "bearerAuth")
public class ProfileResource {

    private final FinancialProfileService service;
    private final JsonWebToken jwt;

    public ProfileResource(FinancialProfileService service, JsonWebToken jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    @POST
    @Operation(summary = "Create financial profile")
    @APIResponse(responseCode = "201", description = "Profile created",
            content = @Content(schema = @Schema(implementation = FinancialProfileDto.class)))
    @APIResponse(responseCode = "409", description = "Profile already exists")
    public Response create(@Valid CreateFinancialProfileDto dto) {
        FinancialProfile profile = toModel(dto);
        profile.setUserId(currentUserId());
        FinancialProfile created = service.create(profile);
        return Response.status(Response.Status.CREATED).entity(toDto(created)).build();
    }

    @GET
    @Operation(summary = "Get financial profile")
    @APIResponse(responseCode = "200", description = "Profile found",
            content = @Content(schema = @Schema(implementation = FinancialProfileDto.class)))
    @APIResponse(responseCode = "404", description = "Profile not found")
    public Response get() {
        return service.findByUserId(currentUserId())
                .map(profile -> Response.ok(toDto(profile)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PATCH
    @Operation(summary = "Update financial profile")
    @APIResponse(responseCode = "200", description = "Profile updated",
            content = @Content(schema = @Schema(implementation = FinancialProfileDto.class)))
    @APIResponse(responseCode = "404", description = "Profile not found")
    public Response update(@Valid UpdateFinancialProfileDto dto) {
        FinancialProfile patch = toPatchModel(dto);
        FinancialProfile updated = service.update(currentUserId(), patch);
        return Response.ok(toDto(updated)).build();
    }

    private UUID currentUserId() {
        return UUID.fromString(jwt.getSubject());
    }

    // --- Mappers ---

    private FinancialProfile toModel(CreateFinancialProfileDto dto) {
        FinancialProfile profile = new FinancialProfile();
        profile.setMonthlyIncome(dto.getMonthlyIncome());
        profile.setEmploymentStatus(dto.getEmploymentStatus());
        profile.setAge(dto.getAge());
        profile.setFinancialExperienceLevel(dto.getFinancialExperienceLevel());
        profile.setCurrency(dto.getCurrency());
        return profile;
    }

    private FinancialProfile toPatchModel(UpdateFinancialProfileDto dto) {
        FinancialProfile patch = new FinancialProfile();
        patch.setMonthlyIncome(dto.getMonthlyIncome());
        patch.setEmploymentStatus(dto.getEmploymentStatus());
        patch.setAge(dto.getAge() > 0 ? dto.getAge() : 0);
        patch.setFinancialExperienceLevel(dto.getFinancialExperienceLevel());
        patch.setCurrency(dto.getCurrency());
        return patch;
    }

    private FinancialProfileDto toDto(FinancialProfile profile) {
        FinancialProfileDto dto = new FinancialProfileDto();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUserId());
        dto.setMonthlyIncome(profile.getMonthlyIncome());
        dto.setEmploymentStatus(profile.getEmploymentStatus());
        dto.setAge(profile.getAge());
        dto.setFinancialExperienceLevel(profile.getFinancialExperienceLevel());
        dto.setCurrency(profile.getCurrency());
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());
        return dto;
    }
}
