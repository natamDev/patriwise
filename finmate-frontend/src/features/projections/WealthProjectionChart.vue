<script setup lang="ts">
import { computed, ref } from 'vue'

const monthlyContribution = ref(300)
const annualReturn = ref(7)
const horizonYears = ref(20)

const projection = computed(() => {
  const r = annualReturn.value / 100 / 12
  const n = horizonYears.value * 12
  const P = monthlyContribution.value

  const yearlyData: { year: number; invested: number; total: number }[] = []

  for (let y = 0; y <= horizonYears.value; y++) {
    const months = y * 12
    const invested = P * months
    const total =
      r === 0
        ? invested
        : P * ((Math.pow(1 + r, months) - 1) / r)
    yearlyData.push({ year: y, invested: Math.round(invested), total: Math.round(total) })
  }

  const last = yearlyData[yearlyData.length - 1]
  return {
    yearlyData,
    capitalFinal: last.total,
    capitalInvested: last.invested,
    interestsGenerated: last.total - last.invested,
  }
})

// SVG dimensions
const svgW = 300
const svgH = 140
const padX = 10
const padY = 10

const maxVal = computed(() => projection.value.yearlyData[projection.value.yearlyData.length - 1].total + 100)

function toX(year: number): number {
  return padX + (year / horizonYears.value) * (svgW - padX * 2)
}
function toY(val: number): number {
  return svgH - padY - (val / maxVal.value) * (svgH - padY * 2)
}

const investedLine = computed(() =>
  projection.value.yearlyData.map((d) => `${toX(d.year)},${toY(d.invested)}`).join(' ')
)
const totalLine = computed(() =>
  projection.value.yearlyData.map((d) => `${toX(d.year)},${toY(d.total)}`).join(' ')
)

function formatEuro(v: number): string {
  if (v >= 1_000_000) return (v / 1_000_000).toFixed(1) + ' M€'
  if (v >= 1_000) return Math.round(v / 1_000) + ' k€'
  return v + ' €'
}
</script>

<template>
  <div class="wpc">
    <!-- Sliders -->
    <div class="wpc__controls">
      <div class="wpc__control">
        <div class="wpc__control-header">
          <span class="wpc__control-label">Épargne mensuelle</span>
          <span class="wpc__control-value">{{ monthlyContribution }} €</span>
        </div>
        <input v-model.number="monthlyContribution" type="range" min="10" max="2000" step="10" class="wpc__slider" />
      </div>

      <div class="wpc__control">
        <div class="wpc__control-header">
          <span class="wpc__control-label">Rendement annuel</span>
          <span class="wpc__control-value">{{ annualReturn }} %</span>
        </div>
        <input v-model.number="annualReturn" type="range" min="0" max="15" step="0.5" class="wpc__slider" />
      </div>

      <div class="wpc__control">
        <div class="wpc__control-header">
          <span class="wpc__control-label">Horizon</span>
          <span class="wpc__control-value">{{ horizonYears }} ans</span>
        </div>
        <input v-model.number="horizonYears" type="range" min="1" max="40" step="1" class="wpc__slider" />
      </div>
    </div>

    <!-- Stats -->
    <div class="wpc__stats">
      <div class="wpc__stat">
        <span class="wpc__stat-label">Capital final</span>
        <span class="wpc__stat-value wpc__stat-value--total">{{ formatEuro(projection.capitalFinal) }}</span>
      </div>
      <div class="wpc__stat">
        <span class="wpc__stat-label">Capital investi</span>
        <span class="wpc__stat-value">{{ formatEuro(projection.capitalInvested) }}</span>
      </div>
      <div class="wpc__stat">
        <span class="wpc__stat-label">Intérêts générés</span>
        <span class="wpc__stat-value wpc__stat-value--interest">{{ formatEuro(projection.interestsGenerated) }}</span>
      </div>
    </div>

    <!-- Chart -->
    <svg :viewBox="`0 0 ${svgW} ${svgH}`" class="wpc__svg" preserveAspectRatio="none">
      <!-- Invested area -->
      <polyline
        :points="investedLine"
        fill="none"
        stroke="#94a3b8"
        stroke-width="1.5"
        stroke-dasharray="4,2"
        opacity="0.7"
      />
      <!-- Total line -->
      <polyline
        :points="totalLine"
        fill="none"
        stroke="#0EA5A4"
        stroke-width="2"
        stroke-linejoin="round"
      />
    </svg>

    <div class="wpc__legend">
      <span class="wpc__legend-item wpc__legend-item--total">Capital total</span>
      <span class="wpc__legend-item wpc__legend-item--invested">Capital investi</span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.wpc {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  &__controls {
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__control {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__control-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  &__control-label {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__control-value {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-text;
  }

  &__slider {
    width: 100%;
    accent-color: $color-primary;
  }

  &__stats {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: $spacing-sm;
  }

  &__stat {
    display: flex;
    flex-direction: column;
    gap: 2px;
    background-color: $color-background;
    border: 1px solid $color-border;
    border-radius: $radius-md;
    padding: $spacing-xs $spacing-sm;
  }

  &__stat-label {
    font-size: 11px;
    color: $color-text-muted;
  }

  &__stat-value {
    font-size: $font-size-sm;
    font-weight: 700;
    color: $color-text;

    &--total { color: $color-secondary; }
    &--interest { color: $color-primary; }
  }

  &__svg {
    width: 100%;
    height: 140px;
    background-color: $color-background;
    border-radius: $radius-sm;
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
      width: 16px;
      height: 2px;
    }

    &--total::before { background-color: $color-secondary; }
    &--invested::before {
      background-color: #94a3b8;
      background-image: repeating-linear-gradient(
        90deg, #94a3b8 0, #94a3b8 4px, transparent 4px, transparent 6px
      );
    }
  }
}
</style>
