<script setup lang="ts">
import { computed, ref } from 'vue'

const monthly = ref(200)
const years = ref(20)
const returnRate = ref(7)

interface YearPoint {
  year: number
  invested: number
  total: number
}

const dataPoints = computed<YearPoint[]>(() => {
  const r = returnRate.value / 100 / 12
  const points: YearPoint[] = []
  for (let y = 1; y <= years.value; y++) {
    const n = y * 12
    const invested = monthly.value * n
    const total = r === 0 ? invested : monthly.value * ((Math.pow(1 + r, n) - 1) / r)
    points.push({ year: y, invested: Math.round(invested), total: Math.round(total) })
  }
  return points
})

const maxValue = computed(() => Math.max(...dataPoints.value.map((p) => p.total), 1))

const final = computed(() => dataPoints.value[dataPoints.value.length - 1] ?? { invested: 0, total: 0 })
const finalGain = computed(() => final.value.total - final.value.invested)

function fmt(n: number): string {
  return new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 0 }).format(n)
}

function pct(value: number): number {
  return Math.round((value / maxValue.value) * 100)
}

function showLabel(year: number): boolean {
  return year === 1 || year % 5 === 0
}
</script>

<template>
  <div class="compound">
    <div class="compound__fields">
      <div class="compound__field">
        <div class="compound__field-header">
          <label class="compound__label">Investissement mensuel</label>
          <span class="compound__value">{{ monthly }} €</span>
        </div>
        <input v-model.number="monthly" class="compound__range" type="range" min="10" max="2000" step="10" />
      </div>

      <div class="compound__field">
        <div class="compound__field-header">
          <label class="compound__label">Durée</label>
          <span class="compound__value">{{ years }} ans</span>
        </div>
        <input v-model.number="years" class="compound__range" type="range" min="5" max="40" step="1" />
      </div>

      <div class="compound__field">
        <div class="compound__field-header">
          <label class="compound__label">Rendement annuel</label>
          <span class="compound__value">{{ returnRate }} %</span>
        </div>
        <input v-model.number="returnRate" class="compound__range" type="range" min="1" max="15" step="0.5" />
      </div>
    </div>

    <div class="compound__chart">
      <div v-for="point in dataPoints" :key="point.year" class="compound__col">
        <div class="compound__bars">
          <div class="compound__bar-total" :style="{ height: pct(point.total) + '%' }">
            <div
              class="compound__bar-invested"
              :style="{ height: (point.invested / point.total * 100) + '%' }"
            />
          </div>
        </div>
        <span class="compound__col-label">{{ showLabel(point.year) ? point.year + 'a' : '' }}</span>
      </div>
    </div>

    <div class="compound__legend">
      <span class="compound__legend-item compound__legend-item--invested">Capital investi</span>
      <span class="compound__legend-item compound__legend-item--gain">Intérêts composés</span>
    </div>

    <div class="compound__summary">
      <div class="compound__summary-row">
        <span class="compound__summary-label">Capital investi</span>
        <span class="compound__summary-value">{{ fmt(final.invested) }} €</span>
      </div>
      <div class="compound__summary-row">
        <span class="compound__summary-label">Intérêts générés</span>
        <span class="compound__summary-value compound__summary-value--gain">{{ fmt(finalGain) }} €</span>
      </div>
      <div class="compound__summary-row compound__summary-row--total">
        <span class="compound__summary-label">Capital final</span>
        <span class="compound__summary-value compound__summary-value--total">{{ fmt(final.total) }} €</span>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.compound {
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

  &__chart {
    display: flex;
    align-items: flex-end;
    gap: 2px;
    height: 130px;
  }

  &__col {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
    height: 100%;
  }

  &__bars {
    flex: 1;
    width: 100%;
    display: flex;
    align-items: flex-end;
  }

  &__bar-total {
    width: 100%;
    background-color: $color-secondary;
    border-radius: 2px 2px 0 0;
    position: relative;
    transition: height 0.3s ease;
    min-height: 2px;
  }

  &__bar-invested {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    background-color: $color-primary;
    border-radius: 0;
    transition: height 0.3s ease;
  }

  &__col-label {
    font-size: 9px;
    color: $color-text-muted;
    text-align: center;
    min-height: 12px;
    white-space: nowrap;
  }

  &__legend {
    display: flex;
    gap: $spacing-md;
  }

  &__legend-item {
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

  &__summary {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-radius: $radius-lg;
    padding: $spacing-md;
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }

  &__summary-row {
    display: flex;
    justify-content: space-between;
    align-items: center;

    &--total {
      border-top: 1px solid $color-border;
      padding-top: $spacing-xs;
      margin-top: $spacing-xs;
    }
  }

  &__summary-label {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__summary-value {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-text;

    &--gain { color: $color-secondary; }
    &--total {
      font-size: $font-size-base;
      color: $color-primary;
    }
  }
}
</style>
