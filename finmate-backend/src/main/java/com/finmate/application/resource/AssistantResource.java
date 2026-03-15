package com.finmate.application.resource;

import com.finmate.application.dto.AssistantChatDto;
import com.finmate.application.dto.AssistantRecommendationDto;
import com.finmate.application.dto.AssistantResponseDto;
import com.finmate.application.dto.FinancialAnalysisDto;
import com.finmate.application.dto.InvestmentEducationDto;
import com.finmate.application.dto.InvestmentEducationRequestDto;
import com.finmate.application.dto.InvestmentSimulationDto;
import com.finmate.application.dto.InvestmentSimulationRequestDto;
import com.finmate.application.dto.DecisionCoachingDto;
import com.finmate.application.dto.DecisionCoachingRequestDto;
import com.finmate.application.dto.FinancialProjectionDto;
import com.finmate.application.dto.GoalAssistantRequestDto;
import com.finmate.application.dto.GoalAssistantResponseDto;
import com.finmate.application.dto.MotivationDto;
import com.finmate.application.dto.RiskEducationDto;
import com.finmate.application.dto.RiskEducationRequestDto;
import com.finmate.application.dto.SavingsCoachingDto;
import com.finmate.domain.exception.ValidationException;
import com.finmate.domain.model.AssistantRecommendation;
import com.finmate.domain.service.AssistantService;
import com.finmate.domain.service.CoachingService;
import com.finmate.domain.service.FinancialAnalysisService;
import com.finmate.domain.service.InvestmentEducationService;
import com.finmate.domain.service.InvestmentSimulatorService;
import com.finmate.domain.model.GoalAssistantSession;
import com.finmate.domain.service.DecisionCoachingService;
import com.finmate.domain.service.GoalAssistantService;
import com.finmate.domain.service.FinancialProjectionService;
import com.finmate.domain.service.MotivationService;
import com.finmate.domain.service.RiskEducationService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
    private final InvestmentEducationService investmentEducationService;
    private final InvestmentSimulatorService investmentSimulatorService;
    private final RiskEducationService riskEducationService;
    private final DecisionCoachingService decisionCoachingService;
    private final FinancialProjectionService financialProjectionService;
    private final MotivationService motivationService;
    private final GoalAssistantService goalAssistantService;
    private final JsonWebToken jwt;

    public AssistantResource(AssistantService service, CoachingService coachingService,
                             FinancialAnalysisService financialAnalysisService,
                             SavingsCoachingService savingsCoachingService,
                             InvestmentEducationService investmentEducationService,
                             InvestmentSimulatorService investmentSimulatorService,
                             RiskEducationService riskEducationService,
                             DecisionCoachingService decisionCoachingService,
                             FinancialProjectionService financialProjectionService,
                             MotivationService motivationService,
                             GoalAssistantService goalAssistantService,
                             JsonWebToken jwt) {
        this.service = service;
        this.coachingService = coachingService;
        this.financialAnalysisService = financialAnalysisService;
        this.savingsCoachingService = savingsCoachingService;
        this.investmentEducationService = investmentEducationService;
        this.investmentSimulatorService = investmentSimulatorService;
        this.riskEducationService = riskEducationService;
        this.decisionCoachingService = decisionCoachingService;
        this.financialProjectionService = financialProjectionService;
        this.motivationService = motivationService;
        this.goalAssistantService = goalAssistantService;
        this.jwt = jwt;
    }

    @POST
    @Path("/chat")
    @Operation(summary = "Send a message to the AI assistant")
    public Response chat(@Valid AssistantChatDto dto) {
        UUID userId = UUID.fromString(jwt.getSubject());
        AssistantService.ChatResult result = service.chat(userId, dto.getConversationId(), dto.getMessage());
        return Response.ok(new AssistantResponseDto(result.conversationId(), result.reply(), result.conceptCard(), result.fomoAlert(), result.biasAlert())).build();
    }

    @POST
    @Path("/coaching")
    @Operation(summary = "Generate a personalized coaching recommendation")
    public Response coaching() {
        UUID userId = UUID.fromString(jwt.getSubject());
        AssistantRecommendation rec = coachingService.generateCoaching(userId);
        return Response.ok(toDto(rec)).build();
    }

    @POST
    @Path("/motivation")
    @Operation(summary = "Generate a personalized motivational message based on financial progress")
    public Response motivation() {
        UUID userId = UUID.fromString(jwt.getSubject());
        MotivationService.MotivationResult result = motivationService.motivate(userId);
        MotivationDto dto = new MotivationDto();
        dto.setMessage(result.message());
        dto.setCurrentStreak(result.stats().currentStreak());
        dto.setLongestStreak(result.stats().longestStreak());
        dto.setFinancialScore(result.stats().financialScore());
        dto.setScoreLabel(result.stats().scoreLabel());
        dto.setAverageGoalProgress(result.stats().averageGoalProgress());
        dto.setActiveGoals(result.stats().activeGoals());
        return Response.ok(dto).build();
    }

    @POST
    @Path("/financial-projection")
    @Operation(summary = "Generate personalized financial projections based on user data")
    public Response financialProjection() {
        UUID userId = UUID.fromString(jwt.getSubject());
        FinancialProjectionService.FinancialProjectionResult result = financialProjectionService.project(userId);
        FinancialProjectionDto dto = new FinancialProjectionDto();
        dto.setMonthlyInvestment(result.monthlyInvestment());
        dto.setHorizonYears(result.horizonYears());
        dto.setCurrency(result.currency());
        dto.setConservative(toScenarioDto(result.conservative()));
        dto.setModerate(toScenarioDto(result.moderate()));
        dto.setOptimistic(toScenarioDto(result.optimistic()));
        dto.setExplanation(result.explanation());
        return Response.ok(dto).build();
    }

    private FinancialProjectionDto.ScenarioDto toScenarioDto(FinancialProjectionService.Scenario s) {
        FinancialProjectionDto.ScenarioDto dto = new FinancialProjectionDto.ScenarioDto();
        dto.setLabel(s.label());
        dto.setReturnPct(s.returnPct());
        dto.setCapitalInvested(s.capitalInvested());
        dto.setCapitalFinal(s.capitalFinal());
        dto.setInterestGain(s.interestGain());
        return dto;
    }

    @POST
    @Path("/decision-coaching")
    @Operation(summary = "Guide the user through a structured financial decision-making process")
    public Response decisionCoaching(@Valid DecisionCoachingRequestDto dto) {
        UUID userId = UUID.fromString(jwt.getSubject());
        DecisionCoachingService.DecisionCoachingResult result = decisionCoachingService.coach(
                userId, dto.getDecisionContext(), dto.getWhyInvesting(),
                dto.getInvestmentHorizon(), dto.getRiskTolerance(), dto.getFinancialGoal());
        DecisionCoachingDto out = new DecisionCoachingDto();
        out.setSessionId(result.sessionId());
        out.setRecommendation(result.recommendation());
        out.setDecisionContext(result.decisionContext());
        out.setWhyInvesting(result.whyInvesting());
        out.setInvestmentHorizon(result.investmentHorizon());
        out.setRiskTolerance(result.riskTolerance());
        out.setFinancialGoal(result.financialGoal());
        return Response.ok(out).build();
    }

    @POST
    @Path("/risk-education")
    @Operation(summary = "Generate an educational explanation for a financial risk concept")
    public Response riskEducation(@Valid RiskEducationRequestDto dto) {
        RiskEducationService.RiskExplanation result = riskEducationService.explain(dto.getTopic());
        RiskEducationDto out = new RiskEducationDto();
        out.setTopic(result.topic());
        out.setTopicLabel(result.topicLabel());
        out.setDefinition(result.definition());
        out.setExample(result.example());
        out.setHistoricalExample(result.historicalExample());
        out.setHowToReact(result.howToReact());
        out.setSimpleSummary(result.simpleSummary());
        out.setKeyPoints(result.keyPoints());
        return Response.ok(out).build();
    }

    @POST
    @Path("/investment-simulation")
    @Operation(summary = "Simulate an investment and get an AI-powered explanation")
    public Response investmentSimulation(@Valid InvestmentSimulationRequestDto dto) {
        InvestmentSimulatorService.SimulationResult result = investmentSimulatorService.simulate(
                dto.getMonthlyInvestment(), dto.getExpectedReturn(), dto.getHorizonYears());
        InvestmentSimulationDto out = new InvestmentSimulationDto();
        out.setMonthlyInvestment(result.monthlyInvestment());
        out.setExpectedReturn(result.expectedReturn());
        out.setHorizonYears(result.horizonYears());
        out.setCapitalInvested(result.capitalInvested());
        out.setCapitalFinal(result.capitalFinal());
        out.setInterestGain(result.interestGain());
        out.setExplanation(result.explanation());
        return Response.ok(out).build();
    }

    @POST
    @Path("/investment-education")
    @Operation(summary = "Generate an educational explanation for an investment concept")
    public Response investmentEducation(@Valid InvestmentEducationRequestDto dto) {
        InvestmentEducationService.InvestmentExplanation result = investmentEducationService.explain(dto.getTopic());
        InvestmentEducationDto out = new InvestmentEducationDto();
        out.setTopic(result.topic());
        out.setTopicLabel(result.topicLabel());
        out.setDefinition(result.definition());
        out.setExample(result.example());
        out.setRisk(result.risk());
        out.setSimpleSummary(result.simpleSummary());
        out.setKeyPoints(result.keyPoints());
        return Response.ok(out).build();
    }

    @POST
    @Path("/savings-coaching")
    @Operation(summary = "Generate savings coaching based on goal progress")
    public Response savingsCoaching() {
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
    }

    @POST
    @Path("/financial-analysis")
    @Operation(summary = "Analyze user financial situation")
    public Response financialAnalysis() {
        UUID userId = UUID.fromString(jwt.getSubject());
        FinancialAnalysisService.FinancialAnalysisResult result = financialAnalysisService.analyze(userId);
        FinancialAnalysisDto dto = new FinancialAnalysisDto();
        dto.setAnalysis(result.analysis());
        dto.setExpenseByCategory(result.expenseByCategory());
        dto.setSavingCapacity(result.savingCapacity());
        dto.setSpendingAlerts(result.spendingAlerts());
        return Response.ok(dto).build();
    }

    @POST
    @Path("/goal-assistant")
    @Operation(summary = "Goal creation assistant — conversational flow in 3 steps (INTENT, CLARIFY_RESPONSE, CONFIRM)")
    public Response goalAssistant(@Valid GoalAssistantRequestDto dto) {
        UUID userId = UUID.fromString(jwt.getSubject());
        GoalAssistantResponseDto out = new GoalAssistantResponseDto();

        switch (dto.getStep().toUpperCase()) {
            case "INTENT" -> {
                GoalAssistantSession session = goalAssistantService.analyzeIntent(userId, dto.getUserIntent());
                out.setSessionId(session.getId().toString());
                out.setStep("INTENT");
                List<String> questions = Arrays.stream(session.getClarificationQuestions().split("\n"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
                out.setClarificationQuestions(questions);
            }
            case "CLARIFY_RESPONSE" -> {
                GoalAssistantSession session = goalAssistantService.proposeGoal(
                        userId, UUID.fromString(dto.getSessionId()), dto.getUserAnswers());
                out.setSessionId(session.getId().toString());
                out.setStep("CLARIFY_RESPONSE");
                out.setGoalName(session.getProposedGoalName());
                out.setGoalType(session.getProposedGoalType() != null ? session.getProposedGoalType().name() : null);
                out.setTargetAmount(session.getProposedTargetAmount());
                out.setTargetDate(session.getProposedTargetDate() != null ? session.getProposedTargetDate().toString() : null);
                out.setMonthlyContribution(session.getProposedMonthlyContribution());
                out.setFeasibilityAssessment(session.getFeasibilityAssessment());
                out.setFeasibilityPercent(session.getFeasibilityPercent());
            }
            case "CONFIRM" -> {
                GoalAssistantSession session = goalAssistantService.confirmGoal(
                        userId, UUID.fromString(dto.getSessionId()),
                        dto.getGoalName(), dto.getGoalType(),
                        dto.getTargetAmount(), LocalDate.parse(dto.getTargetDate()),
                        dto.getMonthlyContribution());
                out.setSessionId(session.getId().toString());
                out.setStep("CONFIRM");
                out.setCreatedGoalId(session.getCreatedGoalId().toString());
                out.setConfirmationMessage("Objectif \"" + dto.getGoalName() + "\" créé avec succès !");
            }
            default -> throw new ValidationException("Step invalide : " + dto.getStep());
        }

        return Response.ok(out).build();
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
