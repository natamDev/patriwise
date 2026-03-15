<script setup lang="ts">
import { computed, reactive } from 'vue'

interface Scenario {
  label: string
  monthlyContribution: number
  annualReturn: number
  horizonYears: number
}

const scenarioA = reactive<Scenario>({
  label: 'Scénario A',
  monthlyContribution: 200,
  annualReturn: 3,
  horizonYears: 20,
})

const scenarioB = reactive<Scenario>({
  label: 'Scénario B',
  monthlyContribution: 200,
  annualReturn: 7,
  horizonYears: 20,
})

function computeResult(s: Scenario) {
  const r = s.annualReturn / 100 / 12
  const n = s.horizonYears * 12
  const P = s.monthlyContribution
  const total = r === 0 ? P * n : P * ((Math.pow(1 + r, n) - 1) / r)
  const invested = P * n
  return {
    capitalFinal: Math.round(total),
    capitalInvested: Math.round(invested),
    interestsGenerated: Math.round(total - invested),
  }
}

const resultA = computed(() => computeResult(scenarioA))
const resultB = computed(() => computeResult(scenarioB))

const difference = computed(() => resultB.value.capitalFinal - resultA.value.capitalFinal)

// SVG
const svgW = 300
const svgH = 120
const padX = 10
const padY = 10

const maxHorizon = computed(() => Math.max(scenarioA.horizonYears, scenarioB.horizonYears))
const maxVal = computed(() => Math.max(resultA.value.capitalFinal, resultB.value.capitalFinal) + 100)

function buildPoints(s: Scenario, maxH: number): string {
  const r = s.annualReturn / 100 / 12
  const P = s.monthlyContribution
  const points: string[] = []
  for (let y = 0; y <= maxH; y++) {
    const n = y * 12
    const total = r === 0 ? P * n : P * ((Math.pow(1 + r, n) - 1) / r)
    const x = padX + (y / maxH) * (svgW - padX * 2)
    const yPos = svgH - padY - (total / maxVal.value) * (svgH - padY * 2)
    points.push(`${x},${yPos}`)
  }
  return points.join(' ')
}

const lineA = computed(() => buildPoints(scenarioA, maxHorizon.value))
const lineB = computed(() => buildPoints(scenarioB, maxHorizon.value))

function formatEuro(v: number): string {
  if (v >= 1_000_000) return (v / 1_000_000).toFixed(1) + ' M€'
  if (v >= 1_000) return Math.round(v / 1_000) + ' k€'
  return v + ' €'
}
</script>

<template>
  <div class="scc">
    <!-- Scénarios -->
    <div class="scc__scenarios">
      <div v-for="(s, key) in { A: scenarioA, B: scenarioB }" :key="key" class="scc__scenario">
        <p class="scc__scenario-label" :class="`scc__scenario-label--${key.toLowerCase()}`">
          Scénario {{ key }}
        </p>

        <div class="scc__control">
          <div class="scc__control-header">
            <span class="scc__control-name">Épargne / mois</span>
            <span class="scc__control-value">{{ s.monthlyContribution }} €</span>
          </div>
          <input v-model.number="s.monthlyContribution" type="range" min="10" max="2000" step="10" class="scc__slider" :class="`scc__slider--${key.toLowerCase()}`" />
        </div>

        <div class="scc__control">
          <div class="scc__control-header">
            <span class="scc__control-name">Rendement</span>
            <span class="scc__control-value">{{ s.annualReturn }} %</span>
          </div>
          <input v-model.number="s.annualReturn" type="range" min="0" max="15" step="0.5" class="scc__slider" :class="`scc__slider--${key.toLowerCase()}`" />
        </div>

        <div class="scc__control">
          <div class="scc__control-header">
            <span class="scc__control-name">Horizon</span>
            <span class="scc__control-value">{{ s.horizonYears }} ans</span>
          </div>
          <input v-model.number="s.horizonYears" type="range" min="1" max="40" step="1" class="scc__slider" :class="`scc__slider--${key.toLowerCase()}`" />
        </div>
      </div>
    </div>

    <!-- Graphique -->
    <svg :viewBox="`0 0 ${svgW} ${svgH}`" class="scc__svg" preserveAspectRatio="none">
      <polyline :points="lineA" fill="none" stroke="#EF4444" stroke-width="2" stroke-linejoin="round" opacity="0.85" />
      <polyline :points="lineB" fill="none" stroke="#0EA5A4" stroke-width="2" stroke-linejoin="round" />
    </svg>

    <div class="scc__legend">
      <span class="scc__legend-item scc__legend-item--a">Scénario A</span>
      <span class="scc__legend-item scc__legend-item--b">Scénario B</span>
    </div>

    <!-- Comparaison -->
    <div class="scc__table">
      <div class="scc__table-header">
        <span />
        <span class="scc__table-col scc__table-col--a">A</span>
        <span class="scc__table-col scc__table-col--b">B</span>
      </div>
      <div class="scc__table-row">
        <span class="scc__table-label">Capital final</span>
        <span class="scc__table-col" :class="resultA.capitalFinal >= resultB.capitalFinal ? 'scc__table-col--winner' : 'scc__table-col--loser'">
          {{ formatEuro(resultA.capitalFinal) }}
        </span>
        <span class="scc__table-col" :class="resultB.capitalFinal >= resultA.capitalFinal ? 'scc__table-col--winner' : 'scc__table-col--loser'">
          {{ formatEuro(resultB.capitalFinal) }}
        </span>
      </div>
      <div class="scc__table-row">
        <span class="scc__table-label">Capital investi</span>
        <span class="scc__table-col scc__table-col--neutral">{{ formatEuro(resultA.capitalInvested) }}</span>
        <span class="scc__table-col scc__table-col--neutral">{{ formatEuro(resultB.capitalInvested) }}</span>
      </div>
      <div class="scc__table-row">
        <span class="scc__table-label">Intérêts</span>
        <span class="scc__table-col" :class="resultA.interestsGenerated >= resultB.interestsGenerated ? 'scc__table-col--winner' : 'scc__table-col--loser'">
          {{ formatEuro(resultA.interestsGenerated) }}
        </span>
        <span class="scc__table-col" :class="resultB.interestsGenerated >= resultA.interestsGenerated ? 'scc__table-col--winner' : 'scc__table-col--loser'">
          {{ formatEuro(resultB.interestsGenerated) }}
        </span>
      </div>
    </div>

    <!-- Différence -->
    <div class="scc__diff" :class="difference >= 0 ? 'scc__diff--positive' : 'scc__diff--negative'">
      <span class="scc__diff-label">
        {{ difference >= 0 ? 'Scénario B génère' : 'Scénario A génère' }}
      </span>
      <span class="scc__diff-value">{{ formatEuro(Math.abs(difference)) }} de plus</span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.scc {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  &__scenarios {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: $spacing-md;
  }

  &__scenario {
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__scenario-label {
    font-size: $font-size-sm;
    font-weight: 700;

    &--a { color: #EF4444; }
    &--b { color: $color-secondary; }
  }

  &__control {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  &__control-header {
    display: flex;
    justify-content: space-between;
  }

  &__control-name {
    font-size: 11px;
    color: $color-text-muted;
  }

  &__control-value {
    font-size: 11px;
    font-weight: 600;
    color: $color-text;
  }

  &__slider {
    width: 100%;

    &--a { accent-color: #EF4444; }
    &--b { accent-color: $color-secondary; }
  }

  &__svg {
    width: 100%;
    height: 120px;
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

    &--a::before { background-color: #EF4444; }
    &--b::before { background-color: $color-secondary; }
  }

  &__table {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-radius: $radius-lg;
    overflow: hidden;
  }

  &__table-header,
  &__table-row {
    display: grid;
    grid-template-columns: 1fr 80px 80px;
    align-items: center;
    padding: $spacing-xs $spacing-md;
  }

  &__table-header {
    background-color: $color-background;
    border-bottom: 1px solid $color-border;
  }

  &__table-row + &__table-row {
    border-top: 1px solid $color-border;
  }

  &__table-label {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__table-col {
    font-size: $font-size-sm;
    font-weight: 600;
    text-align: center;

    &--a { color: #EF4444; }
    &--b { color: $color-secondary; }
    &--winner { color: $color-secondary; }
    &--loser { color: $color-text-muted; font-weight: 400; }
    &--neutral { color: $color-text; font-weight: 400; }
  }

  &__diff {
    border-radius: $radius-md;
    padding: $spacing-sm $spacing-md;
    display: flex;
    justify-content: space-between;
    align-items: center;

    &--positive {
      background-color: #f0fdf4;
      border: 1px solid #86efac;
    }

    &--negative {
      background-color: #fef2f2;
      border: 1px solid #fca5a5;
    }
  }

  &__diff-label {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__diff-value {
    font-size: $font-size-sm;
    font-weight: 700;
    color: $color-text;
  }
}
</style>
