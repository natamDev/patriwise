<script setup lang="ts">
import { assistantApi } from '@/api/assistant.api'
import { categoryLabel, type FinancialAnalysis } from '@/types/assistant.types'
import { useAsyncAction } from '@/composables/useAsyncAction'

const { data: analysis, loading: analysisLoading, error: analysisError, execute } = useAsyncAction<FinancialAnalysis>()

function loadAnalysis() {
  execute(
    () => assistantApi.financialAnalysis(),
    "Impossible de générer l'analyse. Vérifie ta connexion et réessaie."
  )
}
</script>

<template>
  <div class="analysis">
    <div v-if="!analysis" class="analysis__trigger">
      <p class="analysis__trigger-text">Obtenez une analyse complète de votre situation financière.</p>
      <p v-if="analysisError" class="analysis__error">{{ analysisError }}</p>
      <button
        class="analysis__btn"
        type="button"
        :disabled="analysisLoading"
        @click="loadAnalysis"
      >
        {{ analysisLoading ? 'Analyse en cours...' : '📊 Analyser ma situation' }}
      </button>
    </div>

    <div v-else class="analysis__card">
      <p class="analysis__title">📊 Analyse de ta situation financière</p>
      <p class="analysis__text">{{ analysis.analysis }}</p>

      <div v-if="analysis.spendingAlerts.length" class="analysis__alerts">
        <p class="analysis__alerts-label">⚠️ Alertes dépenses</p>
        <ul class="analysis__alerts-list">
          <li v-for="alert in analysis.spendingAlerts" :key="alert" class="analysis__alert-item">
            {{ alert }}
          </li>
        </ul>
      </div>

      <div v-if="Object.keys(analysis.expenseByCategory).length" class="analysis__breakdown">
        <p class="analysis__breakdown-label">Répartition des dépenses</p>
        <div
          v-for="(pct, cat) in analysis.expenseByCategory"
          :key="cat"
          class="analysis__breakdown-row"
        >
          <span class="analysis__breakdown-cat">{{ categoryLabel(String(cat)) }}</span>
          <div class="analysis__breakdown-bar">
            <div class="analysis__breakdown-fill" :style="{ width: Math.min(pct, 100) + '%' }" />
          </div>
          <span class="analysis__breakdown-pct">{{ pct }}%</span>
        </div>
      </div>

      <div class="analysis__saving">
        <span class="analysis__saving-label">Capacité d'épargne</span>
        <span class="analysis__saving-value">{{ analysis.savingCapacity.toFixed(0) }} €/mois</span>
      </div>

      <button class="analysis__refresh" type="button" @click="loadAnalysis">
        Actualiser
      </button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.analysis {
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

  &__text {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.6;
    white-space: pre-wrap;
  }

  &__alerts {
    background-color: #fef2f2;
    border-radius: $radius-md;
    padding: $spacing-xs $spacing-sm;
  }

  &__alerts-label {
    font-size: 11px;
    font-weight: 600;
    color: $color-danger;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-bottom: 4px;
  }

  &__alerts-list {
    margin: 0;
    padding-left: $spacing-md;
  }

  &__alert-item {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.5;
  }

  &__breakdown {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  &__breakdown-label {
    font-size: 11px;
    font-weight: 600;
    color: $color-text-muted;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  &__breakdown-row {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
  }

  &__breakdown-cat {
    font-size: $font-size-sm;
    color: $color-text;
    width: 100px;
    flex-shrink: 0;
  }

  &__breakdown-bar {
    flex: 1;
    height: 6px;
    background-color: $color-border;
    border-radius: 3px;
    overflow: hidden;
  }

  &__breakdown-fill {
    height: 100%;
    background-color: $color-primary;
    border-radius: 3px;
    transition: width 0.3s ease;
  }

  &__breakdown-pct {
    font-size: 12px;
    color: $color-text-muted;
    width: 36px;
    text-align: right;
    flex-shrink: 0;
  }

  &__saving {
    background-color: #f0fdf4;
    border-radius: $radius-md;
    padding: $spacing-xs $spacing-sm;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  &__saving-label {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__saving-value {
    font-size: $font-size-sm;
    font-weight: 700;
    color: $color-secondary;
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
