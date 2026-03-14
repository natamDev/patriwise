<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import { scoreApi } from '@/api/score.api'
import { budgetApi } from '@/api/budget.api'
import { goalsApi } from '@/api/goals.api'
import type { FinancialScore } from '@/types/score.types'
import type { BudgetSummary } from '@/types/budget.types'
import type { Goal, GoalProgress } from '@/types/goal.types'

const { logout } = useAuth()

const score = ref<FinancialScore | null>(null)
const summary = ref<BudgetSummary | null>(null)
const mainGoal = ref<Goal | null>(null)
const mainGoalProgress = ref<GoalProgress | null>(null)

const currentMonth = new Date().toISOString().slice(0, 7)

const scoreColor = (label?: string) => {
  if (label === 'healthy') return '#0EA5A4'
  if (label === 'improving') return '#F59E0B'
  return '#EF4444'
}

const goalProgressWidth = computed(() =>
  mainGoalProgress.value ? Math.min(mainGoalProgress.value.percent, 100) + '%' : '0%',
)

onMounted(async () => {
  const [s, b, g] = await Promise.allSettled([
    scoreApi.getScore(),
    budgetApi.getSummary(currentMonth),
    goalsApi.list(),
  ])
  if (s.status === 'fulfilled') score.value = s.value
  if (b.status === 'fulfilled') summary.value = b.value
  if (g.status === 'fulfilled' && g.value.length > 0) {
    mainGoal.value = g.value[0]
    const p = await goalsApi.getProgress(g.value[0].id).catch(() => null)
    if (p) mainGoalProgress.value = p
  }
})
</script>

<template>
  <div class="page home">
    <div class="home__header">
      <div class="home__header-row">
        <div>
          <p class="home__greeting">Bonjour 👋</p>
          <p class="home__subtitle">Ta progression financière</p>
        </div>
        <button class="home__logout" type="button" @click="logout">Déconnexion</button>
      </div>
    </div>

    <div class="home__card">
      <p class="home__card-label">Score financier</p>
      <template v-if="score">
        <p class="home__score" :style="{ color: scoreColor(score.label) }">
          {{ score.score }} <span class="home__score-max">/100</span>
        </p>
        <p class="home__card-hint">{{ score.explanation }}</p>
      </template>
      <template v-else>
        <p class="home__score">— / 100</p>
        <p class="home__card-hint">Configure ton profil pour voir ton score.</p>
      </template>
    </div>

    <div class="home__card">
      <p class="home__card-label">Épargne ce mois</p>
      <template v-if="summary">
        <p
          class="home__score"
          :style="{ color: summary.remainingIncome >= 0 ? '#0EA5A4' : '#EF4444' }"
        >
          {{ summary.remainingIncome.toFixed(2) }} €
        </p>
        <p class="home__card-hint">
          {{ summary.totalExpenses.toFixed(2) }} € dépensés sur {{ summary.monthlyIncome.toFixed(2) }} € de revenus
        </p>
      </template>
      <template v-else>
        <p class="home__card-empty">Ajoute tes dépenses pour le calcul.</p>
      </template>
    </div>

    <div class="home__card">
      <p class="home__card-label">Objectif principal</p>
      <template v-if="mainGoal && mainGoalProgress">
        <p class="home__goal-name">{{ mainGoal.goalName }}</p>
        <div class="home__goal-bar-bg">
          <div class="home__goal-bar-fill" :style="{ width: goalProgressWidth }" />
        </div>
        <div class="home__goal-stats">
          <span class="home__card-hint">{{ mainGoalProgress.savedAmount.toFixed(2) }} € / {{ mainGoal.targetAmount.toFixed(2) }} €</span>
          <span class="home__goal-percent">{{ mainGoalProgress.percent }}%</span>
        </div>
      </template>
      <template v-else>
        <p class="home__card-empty">Aucun objectif créé.</p>
      </template>
    </div>

    <RouterLink class="home__assistant-cta" to="/assistant">
      <span class="home__assistant-label">Demander à FinMate</span>
      <span class="home__assistant-hint">"Que dois-je faire maintenant ?"</span>
    </RouterLink>

    <RouterLink class="home__profile-link" to="/profile">
      Configurer mon profil →
    </RouterLink>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.home {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  &__header {
    margin-bottom: $spacing-sm;
  }

  &__header-row {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }

  &__logout {
    background: none;
    border: none;
    color: $color-text-muted;
    font-size: $font-size-sm;
    cursor: pointer;
    padding: $spacing-xs $spacing-sm;
  }

  &__greeting {
    font-size: $font-size-xl;
    font-weight: 700;
    color: $color-text;
  }

  &__subtitle {
    font-size: $font-size-sm;
    color: $color-text-muted;
    margin-top: $spacing-xs;
  }

  &__card {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-radius: $radius-lg;
    padding: $spacing-lg;
  }

  &__card-label {
    font-size: $font-size-sm;
    font-weight: 500;
    color: $color-text-muted;
    margin-bottom: $spacing-sm;
  }

  &__score {
    font-size: $font-size-xxl;
    font-weight: 700;
    color: $color-primary;
  }

  &__score-max {
    font-size: $font-size-base;
    font-weight: 400;
    color: $color-text-muted;
  }

  &__card-hint,
  &__card-empty {
    font-size: $font-size-sm;
    color: $color-text-muted;
    margin-top: $spacing-xs;
  }

  &__goal-name {
    font-size: $font-size-base;
    font-weight: 600;
    color: $color-text;
    margin-bottom: $spacing-sm;
  }

  &__goal-bar-bg {
    height: 8px;
    background-color: $color-border;
    border-radius: 4px;
    overflow: hidden;
    margin-bottom: 4px;
  }

  &__goal-bar-fill {
    height: 100%;
    background-color: $color-secondary;
    border-radius: 4px;
    transition: width 0.4s ease;
  }

  &__goal-stats {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  &__goal-percent {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-secondary;
  }

  &__assistant-cta {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
    background-color: $color-primary;
    color: #fff;
    border-radius: $radius-lg;
    padding: $spacing-lg;
    text-decoration: none;
    margin-top: $spacing-sm;
  }

  &__assistant-label {
    font-size: $font-size-base;
    font-weight: 600;
  }

  &__assistant-hint {
    font-size: $font-size-sm;
    opacity: 0.8;
  }

  &__profile-link {
    text-align: center;
    font-size: $font-size-sm;
    color: $color-secondary;
    text-decoration: none;
    font-weight: 500;
  }
}
</style>
