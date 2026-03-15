package com.finmate.application.resource;

import com.finmate.application.dto.AssistantChatDto;
import com.finmate.application.dto.AssistantRecommendationDto;
import com.finmate.application.dto.AssistantResponseDto;
import com.finmate.application.dto.FinancialAnalysisDto;
import com.finmate.application.dto.SavingsCoachingDto;
import com.finmate.domain.model.AssistantRecommendation;
import com.finmate.domain.service.AssistantService;
import com.finmate.domain.service.CoachingService;
import com.finmate.domain.service.FinancialAnalysisService;
import com.finmate.domain.service.SavingsCoachingService;
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
    private final FinancialAnalysisService financialAnalysisService;
    private final SavingsCoachingService savingsCoachingService;
    private final JsonWebToken jwt;

    public AssistantResource(AssistantService service, CoachingService coachingService,
                             FinancialAnalysisService financialAnalysisService,
                             SavingsCoachingService savingsCoachingService,
                             JsonWebToken jwt) {
        this.service = service;
        this.coachingService = coachingService;
        this.financialAnalysisService = financialAnalysisService;
        this.savingsCoachingService = savingsCoachingService;
        this.jwt = jwt;
    }

    @POST
    @Path("/chat")
    @Operation(summary = "Send a message to the AI assistant")
    public Response chat(@Valid AssistantChatDto dto) {
        try {
            UUID userId = UUID.fromString(jwt.getSubject());
            AssistantService.ChatResult result = service.chat(userId, dto.getConversationId(), dto.getMessage());
            return Response.ok(new AssistantResponseDto(result.conversationId(), result.reply(), result.conceptCard(), result.fomoAlert())).build();
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

    @POST
    @Path("/savings-coaching")
    @Operation(summary = "Generate savings coaching based on goal progress")
    public Response savingsCoaching() {
        try {
            UUID userId = UUID.fromString(jwt.getSubject());
            SavingsCoachingService.SavingsCoachingResult result = savingsCoachingService.coach(userId);
            SavingsCoachingDto dto = new SavingsCoachingDto();
            dto.setCoaching(result.coaching());
            dto.setGoals(result.goals().stream().map(item -> {
                SavingsCoachingDto.GoalSummary gs = new SavingsCoachingDto.GoalSummary();
                gs.setGoalId(item.goalId());
                gs.setGoalName(item.goalName());
                gs.setGoalType(item.goalType());
                gs.setTargetAmount(item.targetAmount());
                gs.setSavedAmount(item.savedAmount());
                gs.setRemainingAmount(item.remainingAmount());
                gs.setPercent(item.percent());
                gs.setMonthsNeeded(item.monthsNeeded());
                gs.setEstimatedCompletionDate(item.estimatedCompletionDate());
                gs.setMonthlyContribution(item.monthlyContribution());
                gs.setOptimizedContribution(item.optimizedContribution());
                return gs;
            }).toList());
            return Response.ok(dto).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/financial-analysis")
    @Operation(summary = "Analyze user financial situation")
    public Response financialAnalysis() {
        try {
            UUID userId = UUID.fromString(jwt.getSubject());
            FinancialAnalysisService.FinancialAnalysisResult result = financialAnalysisService.analyze(userId);
            FinancialAnalysisDto dto = new FinancialAnalysisDto();
            dto.setAnalysis(result.analysis());
            dto.setExpenseByCategory(result.expenseByCategory());
            dto.setSavingCapacity(result.savingCapacity());
            dto.setSpendingAlerts(result.spendingAlerts());
            return Response.ok(dto).build();
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
