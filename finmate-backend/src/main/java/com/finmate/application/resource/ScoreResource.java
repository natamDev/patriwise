package com.finmate.application.resource;

import com.finmate.application.dto.FinancialScoreDto;
import com.finmate.domain.service.FinancialScoreService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/api/score")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
@Tag(name = "Score", description = "Financial health score")
@SecurityRequirement(name = "bearerAuth")
public class ScoreResource {

    private final FinancialScoreService service;
    private final JsonWebToken jwt;

    public ScoreResource(FinancialScoreService service, JsonWebToken jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    @GET
    @Operation(summary = "Get financial health score")
    public FinancialScoreDto getScore() {
        UUID userId = UUID.fromString(jwt.getSubject());
        FinancialScoreService.FinancialScore s = service.compute(userId);

        FinancialScoreDto dto = new FinancialScoreDto();
        dto.setScore(s.score());
        dto.setLabel(s.label());
        dto.setExplanation(s.explanation());
        dto.setSavingsRateScore(s.savingsRateScore());
        dto.setExpenseControlScore(s.expenseControlScore());
        dto.setFinancialStabilityScore(s.financialStabilityScore());
        dto.setGoalProgressScore(s.goalProgressScore());
        return dto;
    }
}
