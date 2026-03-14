<script setup lang="ts">
// Points simulant un marché volatile avec une tendance haussière long terme
// Inspiré du comportement réel des marchés actions sur 20 ans
const points = [
  { x: 0, y: 80 },
  { x: 5, y: 65 },
  { x: 10, y: 90 },
  { x: 15, y: 70 },
  { x: 20, y: 105 },
  { x: 25, y: 85 },
  { x: 30, y: 120 },
  { x: 35, y: 75 },  // crise
  { x: 40, y: 100 },
  { x: 45, y: 115 },
  { x: 50, y: 95 },
  { x: 55, y: 135 },
  { x: 60, y: 110 },
  { x: 65, y: 90 },  // crise
  { x: 70, y: 125 },
  { x: 75, y: 145 },
  { x: 80, y: 130 },
  { x: 85, y: 160 },
  { x: 90, y: 140 },
  { x: 95, y: 170 },
  { x: 100, y: 185 },
]

const width = 300
const height = 150
const padX = 10
const padY = 10

const minY = Math.min(...points.map((p) => p.y))
const maxY = Math.max(...points.map((p) => p.y))

function toSvgX(x: number): number {
  return padX + (x / 100) * (width - padX * 2)
}

function toSvgY(y: number): number {
  return height - padY - ((y - minY) / (maxY - minY)) * (height - padY * 2)
}

const polyline = points.map((p) => `${toSvgX(p.x)},${toSvgY(p.y)}`).join(' ')

// Ligne de tendance (début → fin)
const trendStart = { x: toSvgX(0), y: toSvgY(points[0].y) }
const trendEnd = { x: toSvgX(100), y: toSvgY(points[points.length - 1].y) }
</script>

<template>
  <div class="vchart">
    <svg :viewBox="`0 0 ${width} ${height}`" class="vchart__svg" preserveAspectRatio="none">
      <!-- Tendance long terme -->
      <line
        :x1="trendStart.x"
        :y1="trendStart.y"
        :x2="trendEnd.x"
        :y2="trendEnd.y"
        stroke="#0EA5A4"
        stroke-width="1.5"
        stroke-dasharray="5,3"
        opacity="0.6"
      />
      <!-- Courbe volatile -->
      <polyline
        :points="polyline"
        fill="none"
        stroke="#1E3A8A"
        stroke-width="2"
        stroke-linejoin="round"
        stroke-linecap="round"
      />
    </svg>
    <div class="vchart__legend">
      <span class="vchart__legend-item vchart__legend-item--market">Marché (volatile)</span>
      <span class="vchart__legend-item vchart__legend-item--trend">Tendance long terme</span>
    </div>
    <div class="vchart__labels">
      <span class="vchart__label">Année 0</span>
      <span class="vchart__label">Année 20</span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.vchart {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

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

    &--market::before {
      background-color: $color-primary;
    }

    &--trend::before {
      background-color: $color-secondary;
      background-image: repeating-linear-gradient(
        to right,
        $color-secondary 0,
        $color-secondary 4px,
        transparent 4px,
        transparent 7px
      );
    }
  }

  &__labels {
    display: flex;
    justify-content: space-between;
  }

  &__label {
    font-size: 11px;
    color: $color-text-muted;
  }
}
</style>
