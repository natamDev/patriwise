<script setup lang="ts">
import { computed, ref } from 'vue'

const monthly = ref(200)
const years = ref(10)
const returnRate = ref(7)

const totalInvested = computed(() => monthly.value * years.value * 12)

const futureValue = computed(() => {
  const r = returnRate.value / 100 / 12
  const n = years.value * 12
  if (r === 0) return totalInvested.value
  return monthly.value * ((Math.pow(1 + r, n) - 1) / r)
})

const totalGain = computed(() => futureValue.value - totalInvested.value)

const gainPercent = computed(() =>
  totalInvested.value > 0 ? Math.round((totalGain.value / totalInvested.value) * 100) : 0,
)

const investedWidth = computed(() =>
  futureValue.value > 0 ? Math.round((totalInvested.value / futureValue.value) * 100) : 0,
)

function fmt(n: number): string {
  return new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 0 }).format(n)
}
</script>

<template>
  <div class="calc">
    <div class="calc__fields">
      <div class="calc__field">
        <div class="calc__field-header">
          <label class="calc__label">Investissement mensuel</label>
          <span class="calc__value">{{ monthly }} €</span>
        </div>
        <input v-model.number="monthly" class="calc__range" type="range" min="10" max="2000" step="10" />
      </div>

      <div class="calc__field">
        <div class="calc__field-header">
          <label class="calc__label">Durée</label>
          <span class="calc__value">{{ years }} ans</span>
        </div>
        <input v-model.number="years" class="calc__range" type="range" min="1" max="40" step="1" />
      </div>

      <div class="calc__field">
        <div class="calc__field-header">
          <label class="calc__label">Rendement annuel estimé</label>
          <span class="calc__value">{{ returnRate }} %</span>
        </div>
        <input v-model.number="returnRate" class="calc__range" type="range" min="1" max="15" step="0.5" />
      </div>
    </div>

    <div class="calc__results">
      <div class="calc__result-row">
        <span class="calc__result-label">Capital investi</span>
        <span class="calc__result-value">{{ fmt(totalInvested) }} €</span>
      </div>
      <div class="calc__result-row">
        <span class="calc__result-label">Gains générés</span>
        <span class="calc__result-value calc__result-value--gain">+ {{ fmt(totalGain) }} € ({{ gainPercent }}%)</span>
      </div>
      <div class="calc__result-row calc__result-row--total">
        <span class="calc__result-label">Capital final</span>
        <span class="calc__result-value calc__result-value--total">{{ fmt(futureValue) }} €</span>
      </div>
    </div>

    <div class="calc__bar-bg">
      <div class="calc__bar-invested" :style="{ width: investedWidth + '%' }" />
      <div class="calc__bar-gain" :style="{ width: (100 - investedWidth) + '%' }" />
    </div>
    <div class="calc__bar-legend">
      <span class="calc__bar-legend-item calc__bar-legend-item--invested">Capital</span>
      <span class="calc__bar-legend-item calc__bar-legend-item--gain">Intérêts</span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.calc {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  &__fields {
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__field {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__field-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  &__label {
    font-size: $font-size-sm;
    color: $color-text-muted;
    font-weight: 500;
  }

  &__value {
    font-size: $font-size-sm;
    font-weight: 700;
    color: $color-primary;
  }

  &__range {
    width: 100%;
    accent-color: $color-primary;
  }

  &__results {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-radius: $radius-lg;
    padding: $spacing-md;
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }

  &__result-row {
    display: flex;
    justify-content: space-between;
    align-items: center;

    &--total {
      border-top: 1px solid $color-border;
      padding-top: $spacing-xs;
      margin-top: $spacing-xs;
    }
  }

  &__result-label {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__result-value {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-text;

    &--gain { color: $color-secondary; }
    &--total {
      font-size: $font-size-base;
      color: $color-primary;
    }
  }

  &__bar-bg {
    display: flex;
    height: 10px;
    border-radius: $radius-sm;
    overflow: hidden;
  }

  &__bar-invested {
    background-color: $color-primary;
    transition: width 0.3s ease;
  }

  &__bar-gain {
    background-color: $color-secondary;
    transition: width 0.3s ease;
  }

  &__bar-legend {
    display: flex;
    gap: $spacing-md;
  }

  &__bar-legend-item {
    font-size: $font-size-sm;
    color: $color-text-muted;
    display: flex;
    align-items: center;
    gap: 4px;

    &::before {
      content: '';
      display: inline-block;
      width: 10px;
      height: 10px;
      border-radius: 2px;
    }

    &--invested::before { background-color: $color-primary; }
    &--gain::before { background-color: $color-secondary; }
  }
}
</style>
