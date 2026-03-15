<script setup lang="ts">
import { assistantApi } from '@/api/assistant.api'
import type { FinancialProjection } from '@/types/assistant.types'
import { useAsyncAction } from '@/composables/useAsyncAction'

const { data: projection, loading: projectionLoading, error: projectionError, execute } = useAsyncAction<FinancialProjection>()

function loadProjection() {
  execute(
    () => assistantApi.financialProjection(),
    'Impossible de générer la projection. Réessaie.'
  )
}

function formatEur(val: number): string {
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR', maximumFractionDigits: 0 }).format(val)
}
</script>

<template>
  <div class="fp">
    <div v-if="!projection" class="fp__trigger">
      <p class="fp__trigger-text">Projetez votre richesse future basée sur vos données réelles (profil + objectifs).</p>
      <p v-if="projectionError" class="fp__error">{{ projectionError }}</p>
      <button class="fp__btn" type="button" :disabled="projectionLoading" @click="loadProjection">
        {{ projectionLoading ? 'Calcul en cours...' : '🔭 Mes projections financières' }}
      </button>
    </div>

    <div v-else class="fp__card">
      <p class="fp__title">🔭 Projections financières personnalisées</p>
      <p class="fp__meta">
        {{ formatEur(projection.monthlyInvestment) }}/mois · {{ projection.horizonYears }} ans
      </p>

      <div class="fp__scenarios">
        <div
          v-for="scenario in [projection.conservative, projection.moderate, projection.optimistic]"
          :key="scenario.label"
          class="fp__scenario"
          :class="`fp__scenario--${scenario.label.toLowerCase()}`"
        >
          <p class="fp__scenario-label">{{ scenario.label }} ({{ scenario.returnPct }}%/an)</p>
          <p class="fp__scenario-final">{{ formatEur(scenario.capitalFinal) }}</p>
          <p class="fp__scenario-gain">+{{ formatEur(scenario.interestGain) }} d'intérêts</p>
          <p class="fp__scenario-invested">{{ formatEur(scenario.capitalInvested) }} investis</p>
        </div>
      </div>

      <p class="fp__explanation">{{ projection.explanation }}</p>

      <button class="fp__refresh" type="button" @click="loadProjection">Actualiser</button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.fp {
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

  &__card {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-left: 3px solid $color-primary;
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

  &__meta {
    font-size: $font-size-sm;
    color: $color-text-muted;
    margin-top: -$spacing-xs;
  }

  &__scenarios {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
    gap: $spacing-xs;
  }

  &__scenario {
    border-radius: $radius-md;
    padding: $spacing-sm;
    display: flex;
    flex-direction: column;
    gap: 4px;
    border: 1px solid $color-border;

    &--conservateur {
      background-color: #f0fdf4;
      border-color: #86efac;
    }

    &--modéré {
      background-color: #eff6ff;
      border-color: #93c5fd;
    }

    &--optimiste {
      background-color: #fef3c7;
      border-color: #fcd34d;
    }
  }

  &__scenario-label {
    font-size: 11px;
    font-weight: 600;
    color: $color-text-muted;
    text-transform: uppercase;
    letter-spacing: 0.03em;
  }

  &__scenario-final {
    font-size: $font-size-base;
    font-weight: 700;
    color: $color-text;
  }

  &__scenario-gain {
    font-size: 12px;
    color: $color-secondary;
    font-weight: 600;
  }

  &__scenario-invested {
    font-size: 11px;
    color: $color-text-muted;
  }

  &__explanation {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.6;
    white-space: pre-wrap;
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
