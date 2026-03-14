<script setup lang="ts">
import { computed, ref } from 'vue'

const marketDrop = ref(30)
const recoveryYears = ref(3)
const totalYears = 10

interface Point {
  x: number
  y: number
  label?: string
}

const points = computed<Point[]>(() => {
  const crashMonth = 12          // fin de la 1ère année
  const recoveryMonths = recoveryYears.value * 12
  const totalMonths = totalYears * 12

  const pts: Point[] = []
  const base = 100
  const bottom = base * (1 - marketDrop.value / 100)

  for (let m = 0; m <= totalMonths; m++) {
    let value: number
    if (m <= crashMonth) {
      // Montée légère pré-crash puis chute
      const preCrash = base + (m / crashMonth) * 5
      if (m < crashMonth * 0.7) {
        value = preCrash
      } else {
        const t = (m - crashMonth * 0.7) / (crashMonth * 0.3)
        value = preCrash * (1 - t) + bottom * t
      }
    } else if (m <= crashMonth + recoveryMonths) {
      // Récupération progressive
      const t = (m - crashMonth) / recoveryMonths
      const eased = 1 - Math.pow(1 - t, 2)
      value = bottom + (base - bottom) * eased
    } else {
      // Croissance post-récupération (~7% an)
      const monthsAfter = m - (crashMonth + recoveryMonths)
      value = base * Math.pow(1 + 0.07 / 12, monthsAfter)
    }
    pts.push({ x: m, y: Math.round(value * 10) / 10 })
  }
  return pts
})

const svgWidth = 300
const svgHeight = 140
const padX = 8
const padY = 8

const minY = computed(() => Math.min(...points.value.map((p) => p.y)) - 5)
const maxY = computed(() => Math.max(...points.value.map((p) => p.y)) + 5)
const totalMonths = totalYears * 12

function toX(m: number): number {
  return padX + (m / totalMonths) * (svgWidth - padX * 2)
}

function toY(v: number): number {
  return svgHeight - padY - ((v - minY.value) / (maxY.value - minY.value)) * (svgHeight - padY * 2)
}

const polyline = computed(() =>
  points.value.map((p) => `${toX(p.x)},${toY(p.y)}`).join(' '),
)

const crashX = computed(() => toX(12))
const recoveryX = computed(() => toX(12 + recoveryYears.value * 12))
const baseY = computed(() => toY(100))
const bottomY = computed(() => toY(100 * (1 - marketDrop.value / 100)))

const finalValue = computed(() => {
  const last = points.value[points.value.length - 1]
  return last ? Math.round(last.y) : 0
})
</script>

<template>
  <div class="crash">
    <div class="crash__fields">
      <div class="crash__field">
        <div class="crash__field-header">
          <label class="crash__label">Amplitude de la chute</label>
          <span class="crash__value crash__value--danger">-{{ marketDrop }}%</span>
        </div>
        <input v-model.number="marketDrop" class="crash__range" type="range" min="10" max="60" step="5" />
      </div>

      <div class="crash__field">
        <div class="crash__field-header">
          <label class="crash__label">Durée de récupération</label>
          <span class="crash__value">{{ recoveryYears }} ans</span>
        </div>
        <input v-model.number="recoveryYears" class="crash__range" type="range" min="1" max="7" step="1" />
      </div>
    </div>

    <svg :viewBox="`0 0 ${svgWidth} ${svgHeight}`" class="crash__svg" preserveAspectRatio="none">
      <!-- Ligne de base (100) -->
      <line
        :x1="padX"
        :y1="baseY"
        :x2="svgWidth - padX"
        :y2="baseY"
        stroke="#94a3b8"
        stroke-width="0.8"
        stroke-dasharray="3,2"
      />
      <!-- Zone de chute -->
      <rect
        :x="crashX - 1"
        :y="bottomY"
        :width="recoveryX - crashX + 1"
        :height="baseY - bottomY"
        fill="#EF4444"
        opacity="0.08"
      />
      <!-- Courbe -->
      <polyline
        :points="polyline"
        fill="none"
        stroke="#1E3A8A"
        stroke-width="2"
        stroke-linejoin="round"
        stroke-linecap="round"
      />
      <!-- Point de crash -->
      <circle :cx="crashX" :cy="toY(100)" r="3" fill="#EF4444" />
      <!-- Point de récupération -->
      <circle :cx="recoveryX" :cy="toY(100)" r="3" fill="#0EA5A4" />
    </svg>

    <div class="crash__timeline">
      <span class="crash__timeline-item">Début</span>
      <span class="crash__timeline-item crash__timeline-item--crash">Crise</span>
      <span class="crash__timeline-item crash__timeline-item--recovery">Récupération</span>
      <span class="crash__timeline-item">{{ totalYears }} ans</span>
    </div>

    <div class="crash__summary">
      <div class="crash__summary-row">
        <span class="crash__summary-label">Valeur initiale</span>
        <span class="crash__summary-value">100</span>
      </div>
      <div class="crash__summary-row">
        <span class="crash__summary-label">Valeur au creux</span>
        <span class="crash__summary-value crash__summary-value--danger">
          {{ Math.round(100 * (1 - marketDrop / 100)) }} (-{{ marketDrop }}%)
        </span>
      </div>
      <div class="crash__summary-row crash__summary-row--total">
        <span class="crash__summary-label">Valeur après {{ totalYears }} ans</span>
        <span class="crash__summary-value crash__summary-value--gain">{{ finalValue }}</span>
      </div>
    </div>

    <p class="crash__insight">
      Malgré une chute de <strong>-{{ marketDrop }}%</strong> et {{ recoveryYears }} ans de récupération,
      un investisseur patient finit avec un capital supérieur au départ.
    </p>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.crash {
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

    &--danger { color: $color-danger; }
  }

  &__range {
    width: 100%;
    accent-color: $color-primary;
  }

  &__svg {
    width: 100%;
    height: 140px;
    background-color: $color-background;
    border-radius: $radius-sm;
  }

  &__timeline {
    display: flex;
    justify-content: space-between;
    margin-top: -$spacing-xs;
  }

  &__timeline-item {
    font-size: 10px;
    color: $color-text-muted;

    &--crash { color: $color-danger; font-weight: 600; }
    &--recovery { color: $color-secondary; font-weight: 600; }
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

    &--danger { color: $color-danger; }
    &--gain { color: $color-secondary; }
  }

  &__insight {
    font-size: $font-size-sm;
    color: $color-text-muted;
    line-height: 1.5;
    font-style: italic;

    strong { color: $color-text; }
  }
}
</style>
