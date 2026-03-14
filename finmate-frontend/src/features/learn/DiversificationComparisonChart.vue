<script setup lang="ts">
// Simulation de 2 portefeuilles sur 10 ans
// - Mono-actif : forte volatilité, même rendement moyen
// - Diversifié : faible volatilité, rendement similaire

const years = 10
const monthsTotal = years * 12

// Seed déterministe pour des résultats cohérents
function seededRandom(seed: number): number {
  const x = Math.sin(seed + 1) * 10000
  return x - Math.floor(x)
}

function buildPortfolio(volatility: number, annualReturn: number): number[] {
  const monthlyReturn = annualReturn / 12 / 100
  const points: number[] = [100]
  for (let m = 1; m <= monthsTotal; m++) {
    const noise = (seededRandom(m * (volatility + 1)) - 0.5) * volatility
    const prev = points[m - 1]
    points.push(Math.max(10, prev * (1 + monthlyReturn) + noise))
  }
  return points
}

const concentrated = buildPortfolio(8, 7)
const diversified = buildPortfolio(2, 6.5)

const svgWidth = 300
const svgHeight = 130
const padX = 8
const padY = 8

const allValues = [...concentrated, ...diversified]
const minV = Math.min(...allValues) - 5
const maxV = Math.max(...allValues) + 5

function toX(m: number): number {
  return padX + (m / monthsTotal) * (svgWidth - padX * 2)
}
function toY(v: number): number {
  return svgHeight - padY - ((v - minV) / (maxV - minV)) * (svgHeight - padY * 2)
}

function toPolyline(pts: number[]): string {
  return pts.map((v, m) => `${toX(m)},${toY(v)}`).join(' ')
}

const concentratedLine = toPolyline(concentrated)
const diversifiedLine = toPolyline(diversified)

const finalConcentrated = Math.round(concentrated[concentrated.length - 1])
const finalDiversified = Math.round(diversified[diversified.length - 1])

// Calcul de la volatilité (écart-type des variations mensuelles)
function calcVolatility(pts: number[]): number {
  const returns = pts.slice(1).map((v, i) => (v - pts[i]) / pts[i] * 100)
  const mean = returns.reduce((a, b) => a + b, 0) / returns.length
  const variance = returns.reduce((a, b) => a + Math.pow(b - mean, 2), 0) / returns.length
  return Math.round(Math.sqrt(variance) * 10) / 10
}

const volConcentrated = calcVolatility(concentrated)
const volDiversified = calcVolatility(diversified)

const metrics = [
  {
    label: 'Volatilité mensuelle',
    concentrated: volConcentrated + '%',
    diversified: volDiversified + '%',
    better: 'diversified',
  },
  {
    label: 'Capital final',
    concentrated: finalConcentrated,
    diversified: finalDiversified,
    better: finalDiversified >= finalConcentrated ? 'diversified' : 'concentrated',
  },
  {
    label: 'Pire mois',
    concentrated: Math.round(Math.min(...concentrated.slice(1).map((v, i) => (v - concentrated[i]) / concentrated[i] * 100)) * 10) / 10 + '%',
    diversified: Math.round(Math.min(...diversified.slice(1).map((v, i) => (v - diversified[i]) / diversified[i] * 100)) * 10) / 10 + '%',
    better: 'diversified',
  },
]
</script>

<template>
  <div class="divchart">
    <svg :viewBox="`0 0 ${svgWidth} ${svgHeight}`" class="divchart__svg" preserveAspectRatio="none">
      <!-- Ligne de base -->
      <line
        :x1="padX"
        :y1="toY(100)"
        :x2="svgWidth - padX"
        :y2="toY(100)"
        stroke="#94a3b8"
        stroke-width="0.8"
        stroke-dasharray="3,2"
        opacity="0.5"
      />
      <!-- Portefeuille concentré -->
      <polyline
        :points="concentratedLine"
        fill="none"
        stroke="#EF4444"
        stroke-width="1.5"
        stroke-linejoin="round"
        opacity="0.8"
      />
      <!-- Portefeuille diversifié -->
      <polyline
        :points="diversifiedLine"
        fill="none"
        stroke="#0EA5A4"
        stroke-width="2"
        stroke-linejoin="round"
      />
    </svg>

    <div class="divchart__legend">
      <span class="divchart__legend-item divchart__legend-item--concentrated">Mono-actif (1 action)</span>
      <span class="divchart__legend-item divchart__legend-item--diversified">Diversifié (ETF Monde)</span>
    </div>

    <div class="divchart__table">
      <div class="divchart__table-header">
        <span />
        <span class="divchart__table-col divchart__table-col--concentrated">Mono-actif</span>
        <span class="divchart__table-col divchart__table-col--diversified">Diversifié</span>
      </div>
      <div v-for="m in metrics" :key="m.label" class="divchart__table-row">
        <span class="divchart__table-label">{{ m.label }}</span>
        <span
          class="divchart__table-col"
          :class="m.better === 'concentrated' ? 'divchart__table-col--winner' : 'divchart__table-col--loser'"
        >
          {{ m.concentrated }}
        </span>
        <span
          class="divchart__table-col"
          :class="m.better === 'diversified' ? 'divchart__table-col--winner' : 'divchart__table-col--loser'"
        >
          {{ m.diversified }}
        </span>
      </div>
    </div>

    <p class="divchart__insight">
      Le portefeuille diversifié offre un meilleur rapport rendement/risque.
      Moins de stress, moins de volatilité, pour un résultat comparable.
    </p>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.divchart {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  &__svg {
    width: 100%;
    height: 130px;
    background-color: $color-background;
    border-radius: $radius-sm;
  }

  &__legend {
    display: flex;
    gap: $spacing-md;
    flex-wrap: wrap;
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

    &--concentrated::before { background-color: #EF4444; }
    &--diversified::before { background-color: $color-secondary; }
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

    &--concentrated { color: #EF4444; }
    &--diversified { color: $color-secondary; }
    &--winner { color: $color-secondary; }
    &--loser { color: $color-text-muted; font-weight: 400; }
  }

  &__insight {
    font-size: $font-size-sm;
    color: $color-text-muted;
    line-height: 1.5;
    font-style: italic;
  }
}
</style>
