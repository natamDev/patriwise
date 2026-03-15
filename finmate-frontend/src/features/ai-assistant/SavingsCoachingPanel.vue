<script setup lang="ts">
import { assistantApi } from '@/api/assistant.api'
import type { SavingsCoaching } from '@/types/assistant.types'
import { useAsyncAction } from '@/composables/useAsyncAction'

const { data: savingsCoaching, loading: savingsLoading, error: savingsError, execute } = useAsyncAction<SavingsCoaching>()

function loadSavingsCoaching() {
  execute(
    () => assistantApi.savingsCoaching(),
    'Impossible de générer le coaching. Vérifie ta connexion et réessaie.'
  )
}

function goalTypeLabel(type: string): string {
  const labels: Record<string, string> = {
    TRAVEL: 'Voyage',
    EMERGENCY_FUND: "Fonds d'urgence",
    INVESTMENT: 'Investissement',
    PURCHASE: 'Achat',
    OTHER: 'Autre',
  }
  return labels[type] ?? type
}
</script>

<template>
  <div class="savings-coaching">
    <div v-if="!savingsCoaching" class="savings-coaching__trigger">
      <p class="savings-coaching__trigger-text">Analysez vos objectifs d'épargne et recevez des conseils personnalisés.</p>
      <p v-if="savingsError" class="savings-coaching__error">{{ savingsError }}</p>
      <button
        class="savings-coaching__btn"
        type="button"
        :disabled="savingsLoading"
        @click="loadSavingsCoaching"
      >
        {{ savingsLoading ? 'Analyse en cours...' : '🎯 Mon coaching épargne' }}
      </button>
    </div>

    <div v-else class="savings-coaching__card">
      <p class="savings-coaching__title">🎯 Coaching épargne</p>
      <p class="savings-coaching__text">{{ savingsCoaching.coaching }}</p>

      <div v-if="savingsCoaching.goals.length" class="savings-coaching__goals">
        <div
          v-for="goal in savingsCoaching.goals"
          :key="goal.goalId"
          class="savings-coaching__goal"
        >
          <div class="savings-coaching__goal-header">
            <span class="savings-coaching__goal-name">{{ goal.goalName }}</span>
            <span class="savings-coaching__goal-type">{{ goalTypeLabel(goal.goalType) }}</span>
          </div>

          <div class="savings-coaching__goal-bar">
            <div
              class="savings-coaching__goal-fill"
              :style="{ width: goal.percent + '%' }"
            />
          </div>

          <div class="savings-coaching__goal-stats">
            <span>{{ goal.savedAmount.toFixed(0) }} € / {{ goal.targetAmount.toFixed(0) }} €</span>
            <span class="savings-coaching__goal-pct">{{ goal.percent }}%</span>
          </div>

          <div v-if="goal.monthsNeeded" class="savings-coaching__goal-estimate">
            {{ goal.monthsNeeded }} mois restants
            <template v-if="goal.estimatedCompletionDate">
              · {{ new Date(goal.estimatedCompletionDate).toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' }) }}
            </template>
          </div>

          <div v-if="goal.optimizedContribution" class="savings-coaching__goal-tip">
            💡 Pour atteindre ta date cible : <strong>{{ goal.optimizedContribution.toFixed(0) }} €/mois</strong>
            (actuel : {{ goal.monthlyContribution.toFixed(0) }} €/mois)
          </div>
        </div>
      </div>

      <button class="savings-coaching__refresh" type="button" @click="loadSavingsCoaching">
        Actualiser
      </button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.savings-coaching {
  &__trigger {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-radius: $radius-lg;
    padding: $spacing-md;
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__trigger-text {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__error {
    font-size: $font-size-sm;
    color: $color-danger;
  }

  &__btn {
    padding: $spacing-sm $spacing-md;
    background-color: $color-secondary;
    color: #fff;
    border: none;
    border-radius: $radius-md;
    font-size: $font-size-sm;
    font-weight: 600;
    cursor: pointer;
    transition: opacity 0.15s;

    &:disabled {
      opacity: 0.6;
      cursor: default;
    }
  }

  &__card {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-left: 3px solid $color-secondary;
    border-radius: $radius-lg;
    padding: $spacing-md;
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__title {
    font-size: $font-size-base;
    font-weight: 700;
    color: $color-text;
  }

  &__text {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.6;
    white-space: pre-wrap;
  }

  &__goals {
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__goal {
    background-color: #f8fafc;
    border: 1px solid $color-border;
    border-radius: $radius-md;
    padding: $spacing-sm;
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  &__goal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  &__goal-name {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-text;
  }

  &__goal-type {
    font-size: 11px;
    color: $color-text-muted;
    background-color: $color-border;
    border-radius: $radius-sm;
    padding: 2px 6px;
  }

  &__goal-bar {
    height: 6px;
    background-color: $color-border;
    border-radius: 3px;
    overflow: hidden;
  }

  &__goal-fill {
    height: 100%;
    background-color: $color-secondary;
    border-radius: 3px;
    transition: width 0.3s ease;
  }

  &__goal-stats {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: $color-text-muted;
  }

  &__goal-pct {
    font-weight: 600;
    color: $color-secondary;
  }

  &__goal-estimate {
    font-size: 12px;
    color: $color-text-muted;
  }

  &__goal-tip {
    font-size: $font-size-sm;
    color: $color-text;
    background-color: #f0fdf4;
    border-radius: $radius-sm;
    padding: 4px $spacing-sm;
    line-height: 1.5;
  }

  &__refresh {
    align-self: flex-end;
    background: none;
    border: 1px solid $color-border;
    border-radius: $radius-md;
    padding: 4px $spacing-sm;
    font-size: 12px;
    color: $color-text-muted;
    cursor: pointer;
  }
}
</style>
