package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.GoalAssistantSession;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "goal_assistant_sessions")
public class GoalAssistantSessionEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(name = "user_id", nullable = false)
    public UUID userId;

    @Column(name = "user_intent", columnDefinition = "TEXT")
    public String userIntent;

    @Column(name = "clarification_questions", columnDefinition = "TEXT")
    public String clarificationQuestions;

    @Column(name = "user_answers", columnDefinition = "TEXT")
    public String userAnswers;

    @Column(name = "proposed_goal_name")
    public String proposedGoalName;

    @Column(name = "proposed_goal_type")
    public String proposedGoalType;

    @Column(name = "proposed_target_amount")
    public BigDecimal proposedTargetAmount;

    @Column(name = "proposed_target_date")
    public LocalDate proposedTargetDate;

    @Column(name = "proposed_monthly_contribution")
    public BigDecimal proposedMonthlyContribution;

    @Column(name = "feasibility_assessment", columnDefinition = "TEXT")
    public String feasibilityAssessment;

    @Column(name = "feasibility_percent")
    public Integer feasibilityPercent;

    @Column(nullable = false)
    public String status;

    @Column(name = "created_goal_id")
    public UUID createdGoalId;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    public static List<GoalAssistantSessionEntity> findByUserId(UUID userId) {
        return find("userId = ?1 order by createdAt desc", userId).list();
    }

    public GoalAssistantSession toDomain() {
        GoalAssistantSession s = new GoalAssistantSession();
        s.setId(id);
        s.setUserId(userId);
        s.setUserIntent(userIntent);
        s.setClarificationQuestions(clarificationQuestions);
        s.setUserAnswers(userAnswers);
        s.setProposedGoalName(proposedGoalName);
        s.setProposedGoalType(proposedGoalType != null ? GoalAssistantSession.GoalType.valueOf(proposedGoalType) : null);
        s.setProposedTargetAmount(proposedTargetAmount);
        s.setProposedTargetDate(proposedTargetDate);
        s.setProposedMonthlyContribution(proposedMonthlyContribution);
        s.setFeasibilityAssessment(feasibilityAssessment);
        s.setFeasibilityPercent(feasibilityPercent);
        s.setStatus(GoalAssistantSession.Status.valueOf(status));
        s.setCreatedGoalId(createdGoalId);
        s.setCreatedAt(createdAt);
        return s;
    }

    public static GoalAssistantSessionEntity fromDomain(GoalAssistantSession s) {
        GoalAssistantSessionEntity e = new GoalAssistantSessionEntity();
        e.id = s.getId();
        e.userId = s.getUserId();
        e.userIntent = s.getUserIntent();
        e.clarificationQuestions = s.getClarificationQuestions();
        e.userAnswers = s.getUserAnswers();
        e.proposedGoalName = s.getProposedGoalName();
        e.proposedGoalType = s.getProposedGoalType() != null ? s.getProposedGoalType().name() : null;
        e.proposedTargetAmount = s.getProposedTargetAmount();
        e.proposedTargetDate = s.getProposedTargetDate();
        e.proposedMonthlyContribution = s.getProposedMonthlyContribution();
        e.feasibilityAssessment = s.getFeasibilityAssessment();
        e.feasibilityPercent = s.getFeasibilityPercent();
        e.status = s.getStatus().name();
        e.createdGoalId = s.getCreatedGoalId();
        e.createdAt = s.getCreatedAt();
        return e;
    }
}
