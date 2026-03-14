package com.finmate.application.resource;

import com.finmate.application.dto.CreateContributionDto;
import com.finmate.application.dto.CreateGoalDto;
import com.finmate.application.dto.GoalDto;
import com.finmate.application.dto.GoalProgressDto;
import com.finmate.application.dto.UpdateGoalDto;
import com.finmate.domain.model.Goal;
import com.finmate.domain.service.GoalProgressService;
import com.finmate.domain.service.GoalService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
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

@Path("/api/goals")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("**")
@Tag(name = "Goals", description = "Savings goals")
@SecurityRequirement(name = "bearerAuth")
public class GoalResource {

    private final GoalService service;
    private final GoalProgressService progressService;
    private final JsonWebToken jwt;

    public GoalResource(GoalService service, GoalProgressService progressService, JsonWebToken jwt) {
        this.service = service;
        this.progressService = progressService;
        this.jwt = jwt;
    }

    @POST
    @Operation(summary = "Create a savings goal")
    public Response create(@Valid CreateGoalDto dto) {
        try {
            Goal goal = new Goal();
            goal.setGoalName(dto.getGoalName());
            goal.setGoalType(dto.getGoalType());
            goal.setTargetAmount(dto.getTargetAmount());
            goal.setTargetDate(dto.getTargetDate());
            goal.setMonthlyContribution(dto.getMonthlyContribution());
            Goal created = service.create(goal, currentUserId());
            return Response.status(Response.Status.CREATED).entity(toDto(created)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", e.getMessage())).build();
        }
    }

    @GET
    @Operation(summary = "List savings goals")
    public List<GoalDto> list() {
        return service.findByUserId(currentUserId()).stream().map(this::toDto).toList();
    }

    @PATCH
    @Path("/{id}")
    @Operation(summary = "Update a savings goal")
    public Response update(@PathParam("id") UUID id, UpdateGoalDto dto) {
        try {
            Goal patch = new Goal();
            patch.setGoalName(dto.getGoalName());
            patch.setGoalType(dto.getGoalType());
            patch.setTargetAmount(dto.getTargetAmount());
            patch.setTargetDate(dto.getTargetDate());
            patch.setMonthlyContribution(dto.getMonthlyContribution());
            Goal updated = service.update(id, currentUserId(), patch);
            return Response.ok(toDto(updated)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a savings goal")
    public Response delete(@PathParam("id") UUID id) {
        try {
            service.delete(id, currentUserId());
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("error", e.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}/progress")
    @Operation(summary = "Get goal progress")
    public Response getProgress(@PathParam("id") UUID id) {
        try {
            GoalProgressService.GoalProgress p = progressService.getProgress(id, currentUserId());
            return Response.ok(toProgressDto(p)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("error", e.getMessage())).build();
        }
    }

    @POST
    @Path("/{id}/contributions")
    @Operation(summary = "Add a contribution to a goal")
    public Response addContribution(@PathParam("id") UUID id, @Valid CreateContributionDto dto) {
        try {
            progressService.addContribution(id, currentUserId(), dto.getAmount());
            GoalProgressService.GoalProgress p = progressService.getProgress(id, currentUserId());
            return Response.status(Response.Status.CREATED).entity(toProgressDto(p)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", e.getMessage())).build();
        }
    }

    private UUID currentUserId() {
        return UUID.fromString(jwt.getSubject());
    }

    private GoalProgressDto toProgressDto(GoalProgressService.GoalProgress p) {
        GoalProgressDto dto = new GoalProgressDto();
        dto.setGoalId(p.goalId());
        dto.setSavedAmount(p.savedAmount());
        dto.setTargetAmount(p.targetAmount());
        dto.setRemainingAmount(p.remainingAmount());
        dto.setPercent(p.percent());
        dto.setMonthsNeeded(p.monthsNeeded());
        dto.setEstimatedCompletionDate(p.estimatedCompletionDate());
        return dto;
    }

    private GoalDto toDto(Goal g) {
        GoalDto dto = new GoalDto();
        dto.setId(g.getId());
        dto.setGoalName(g.getGoalName());
        dto.setGoalType(g.getGoalType());
        dto.setTargetAmount(g.getTargetAmount());
        dto.setTargetDate(g.getTargetDate());
        dto.setMonthlyContribution(g.getMonthlyContribution());
        dto.setCreatedAt(g.getCreatedAt());
        return dto;
    }
}
