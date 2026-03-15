<script setup lang="ts">
import { ref } from 'vue'
import { assistantApi } from '@/api/assistant.api'
import type { InvestmentSimulation } from '@/types/assistant.types'
import { useAsyncAction } from '@/composables/useAsyncAction'

const { data: simulation, loading: simulationLoading, error: simulationError, execute } = useAsyncAction<InvestmentSimulation>()
const simMonthly = ref(100)
const simReturn = ref(7)
const simHorizon = ref(10)

function runSimulation() {
  if (simMonthly.value <= 0 || simHorizon.value <= 0) return
  execute(
    () => assistantApi.investmentSimulation(simMonthly.value, simReturn.value, simHorizon.value),
    'Impossible de lancer la simulation. Réessaie.'
  )
}

function formatEur(val: number): string {
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR', maximumFractionDigits: 0 }).format(val)
}
</script>

<template>
  <div class="simulator">
    <p class="simulator__title">🔢 Simulateur d'investissement</p>

    <div class="simulator__form">
      <div class="simulator__field">
        <label class="simulator__label">Investissement mensuel</label>
        <div class="simulator__input-row">
          <input v-model.number="simMonthly" class="simulator__input" type="number" min="1" max="10000" />
          <span class="simulator__unit">€/mois</span>
        </div>
      </div>
      <div class="simulator__field">
        <label class="simulator__label">Rendement annuel estimé</label>
        <div class="simulator__input-row">
          <input v-model.number="simReturn" class="simulator__input" type="number" min="0" max="50" step="0.5" />
          <span class="simulator__unit">%/an</span>
        </div>
      </div>
      <div class="simulator__field">
        <label class="simulator__label">Durée</label>
        <div class="simulator__input-row">
          <input v-model.number="simHorizon" class="simulator__input" type="number" min="1" max="50" />
          <span class="simulator__unit">ans</span>
        </div>
      </div>
    </div>

    <p v-if="simulationError" class="simulator__error">{{ simulationError }}</p>

    <button
      class="simulator__btn"
      type="button"
      :disabled="simulationLoading || simMonthly <= 0 || simHorizon <= 0"
      @click="runSimulation"
    >
      {{ simulationLoading ? 'Calcul en cours...' : '▶ Lancer la simulation' }}
    </button>

    <div v-if="simulation && !simulationLoading" class="simulator__result">
      <div class="simulator__stats">
        <div class="simulator__stat">
          <p class="simulator__stat-label">Capital investi</p>
          <p class="simulator__stat-value">{{ formatEur(simulation.capitalInvested) }}</p>
        </div>
        <div class="simulator__stat simulator__stat--gain">
          <p class="simulator__stat-label">Capital final</p>
          <p class="simulator__stat-value simulator__stat-value--primary">{{ formatEur(simulation.capitalFinal) }}</p>
        </div>
        <div class="simulator__stat">
          <p class="simulator__stat-label">Intérêts générés</p>
          <p class="simulator__stat-value simulator__stat-value--green">+{{ formatEur(simulation.interestGain) }}</p>
        </div>
      </div>
      <p class="simulator__explanation">{{ simulation.explanation }}</p>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.simulator {
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;

  &__title {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-text;
  }

  &__form {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-radius: $radius-lg;
    padding: $spacing-md;
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__field {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__label {
    font-size: 11px;
    font-weight: 600;
    color: $color-text-muted;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  &__input-row {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
  }

  &__input {
    flex: 1;
    padding: $spacing-xs $spacing-sm;
    border: 1px solid $color-border;
    border-radius: $radius-md;
    background-color: #fff;
    font-size: $font-size-sm;
    color: $color-text;
    outline: none;

    &:focus {
      border-color: $color-primary;
    }
  }

  &__unit {
    font-size: $font-size-sm;
    color: $color-text-muted;
    white-space: nowrap;
    width: 60px;
    flex-shrink: 0;
  }

  &__error {
    font-size: $font-size-sm;
    color: $color-danger;
  }

  &__btn {
    padding: $spacing-sm $spacing-md;
    background-color: $color-primary;
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

  &__result {
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__stats {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
    gap: $spacing-xs;
  }

  &__stat {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-radius: $radius-md;
    padding: $spacing-sm;
    display: flex;
    flex-direction: column;
    gap: 4px;

    &--gain {
      border-color: $color-primary;
    }
  }

  &__stat-label {
    font-size: 11px;
    color: $color-text-muted;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  &__stat-value {
    font-size: $font-size-sm;
    font-weight: 700;
    color: $color-text;

    &--primary {
      color: $color-primary;
    }

    &--green {
      color: $color-secondary;
    }
  }

  &__explanation {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.6;
    white-space: pre-wrap;
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-left: 3px solid $color-primary;
    border-radius: $radius-lg;
    padding: $spacing-md;
  }
}
</style>
