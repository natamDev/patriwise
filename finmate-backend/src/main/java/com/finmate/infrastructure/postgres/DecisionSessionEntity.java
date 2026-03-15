package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.DecisionSession;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "decision_sessions")
public class DecisionSessionEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(name = "user_id", nullable = false)
    public UUID userId;

    @Column(name = "decision_context", columnDefinition = "TEXT")
    public String decisionContext;

    @Column(name = "why_investing", columnDefinition = "TEXT")
    public String whyInvesting;

    @Column(name = "investment_horizon")
    public String investmentHorizon;

    @Column(name = "risk_tolerance")
    public String riskTolerance;

    @Column(name = "financial_goal", columnDefinition = "TEXT")
    public String financialGoal;

    @Column(columnDefinition = "TEXT")
    public String recommendation;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    public static List<DecisionSessionEntity> findByUserId(UUID userId) {
        return find("userId = ?1 order by createdAt desc", userId).list();
    }

    public DecisionSession toDomain() {
        DecisionSession s = new DecisionSession();
        s.setId(id);
        s.setUserId(userId);
        s.setDecisionContext(decisionContext);
        s.setWhyInvesting(whyInvesting);
        s.setInvestmentHorizon(investmentHorizon);
        s.setRiskTolerance(riskTolerance);
        s.setFinancialGoal(financialGoal);
        s.setRecommendation(recommendation);
        s.setCreatedAt(createdAt);
        return s;
    }

    public static DecisionSessionEntity fromDomain(DecisionSession s) {
        DecisionSessionEntity e = new DecisionSessionEntity();
        e.userId = s.getUserId();
        e.decisionContext = s.getDecisionContext();
        e.whyInvesting = s.getWhyInvesting();
        e.investmentHorizon = s.getInvestmentHorizon();
        e.riskTolerance = s.getRiskTolerance();
        e.financialGoal = s.getFinancialGoal();
        e.recommendation = s.getRecommendation();
        e.createdAt = s.getCreatedAt();
        return e;
    }
}
