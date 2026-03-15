package com.finmate.application.resource;

import com.finmate.application.dto.BudgetSummaryDto;
import com.finmate.domain.service.BudgetService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Path("/api/budget")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
@Tag(name = "Budget", description = "Budget summary")
@SecurityRequirement(name = "bearerAuth")
public class BudgetResource {

    private final BudgetService service;
    private final JsonWebToken jwt;

    public BudgetResource(BudgetService service, JsonWebToken jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    @GET
    @Path("/summary")
    @Operation(summary = "Get budget summary for a month")
    public BudgetSummaryDto summary(@QueryParam("month") String month) {
        YearMonth ym = month != null ? YearMonth.parse(month) : YearMonth.now();
        UUID userId = UUID.fromString(jwt.getSubject());
        BudgetService.BudgetSummary s = service.computeSummary(userId, ym);

        BudgetSummaryDto dto = new BudgetSummaryDto();
        dto.setMonthlyIncome(s.monthlyIncome());
        dto.setTotalExpenses(s.totalExpenses());
        dto.setRemainingIncome(s.remainingIncome());
        dto.setSavingsRate(s.savingsRate());
        dto.setExpensesByCategory(s.expensesByCategory());
        dto.setMonth(s.month());
        return dto;
    }

    @GET
    @Path("/trend")
    @Operation(summary = "Get monthly income vs expenses trend")
    public List<BudgetSummaryDto.MonthTrendDto> trend(@QueryParam("months") @DefaultValue("6") int months) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return service.computeTrend(userId, months).stream()
                .map(t -> new BudgetSummaryDto.MonthTrendDto(t.month(), t.income(), t.expenses()))
                .toList();
    }
}
