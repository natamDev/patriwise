<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { scoreApi } from '@/api/score.api'
import { badgesApi } from '@/api/badges.api'
import type { BadgeStatus } from '@/types/badge.types'
import type { FinancialScore } from '@/types/score.types'
import QuizCard from '@/features/gamification/QuizCard.vue'

const score = ref<FinancialScore | null>(null)
const badges = ref<BadgeStatus[]>([])
const loading = ref(true)

onMounted(async () => {
  try {
    const [s, b] = await Promise.allSettled([scoreApi.getScore(), badgesApi.getBadges()])
    if (s.status === 'fulfilled') score.value = s.value
    if (b.status === 'fulfilled') badges.value = b.value
  } finally {
    loading.value = false
  }
})

const color = computed(() => {
  if (!score.value) return '#94a3b8'
  if (score.value.label === 'healthy') return '#0EA5A4'
  if (score.value.label === 'improving') return '#F59E0B'
  return '#EF4444'
})

const scoreLabel = computed(() => {
  if (!score.value) return ''
  if (score.value.label === 'healthy') return 'Situation saine'
  if (score.value.label === 'improving') return 'En progression'
  return 'À améliorer'
})

// SVG ring
const radius = 54
const circumference = 2 * Math.PI * radius
const dashOffset = computed(() => {
  const pct = score.value ? score.value.score / 100 : 0
  return circumference * (1 - pct)
})

const components = computed(() => {
  if (!score.value) return []
  return [
    {
      label: 'Taux d\'épargne',
      value: score.value.savingsRateScore,
      max: 50,
      tip: score.value.savingsRateScore < 35
        ? 'Augmente ton épargne mensuelle pour améliorer ce score. Vise 20 % de ton revenu.'
        : null,
    },
    {
      label: 'Contrôle des dépenses',
      value: score.value.expenseControlScore,
      max: 20,
      tip: score.value.expenseControlScore < 14
        ? 'Tes dépenses dépassent 70 % de tes revenus. Identifie les postes à réduire.'
        : null,
    },
    {
      label: 'Progrès objectifs',
      value: score.value.goalProgressScore,
      max: 20,
      tip: score.value.goalProgressScore < 10
        ? 'Crée des objectifs d\'épargne et effectue des contributions régulières.'
        : null,
    },
    {
      label: 'Stabilité financière',
      value: score.value.financialStabilityScore,
      max: 10,
      tip: score.value.financialStabilityScore < 7
        ? 'Ta situation professionnelle impacte ce score. Continue à construire ta stabilité.'
        : null,
    },
  ]
})

const tips = computed(() => components.value.filter((c) => c.tip !== null))
</script>

<template>
  <div class="page score-view">
    <h1 class="score-view__title">Score financier</h1>

    <div v-if="loading" class="score-view__loading">Chargement…</div>

    <template v-else-if="score">
      <!-- Ring -->
      <div class="score-view__ring-wrapper">
        <svg viewBox="0 0 128 128" class="score-view__ring">
          <circle cx="64" cy="64" :r="radius" fill="none" stroke="#e2e8f0" stroke-width="10" />
          <circle
            cx="64" cy="64" :r="radius"
            fill="none"
            :stroke="color"
            stroke-width="10"
            stroke-linecap="round"
            :stroke-dasharray="circumference"
            :stroke-dashoffset="dashOffset"
            transform="rotate(-90 64 64)"
            style="transition: stroke-dashoffset 0.6s ease"
          />
        </svg>
        <div class="score-view__ring-content">
          <p class="score-view__score-number" :style="{ color }">{{ score.score }}</p>
          <p class="score-view__score-max">/100</p>
          <p class="score-view__score-label" :style="{ color }">{{ scoreLabel }}</p>
        </div>
      </div>

      <p class="score-view__explanation">{{ score.explanation }}</p>

      <!-- Breakdown -->
      <div class="score-view__section">
        <p class="score-view__section-title">Détail du score</p>
        <div class="score-view__components">
          <div v-for="c in components" :key="c.label" class="score-view__component">
            <div class="score-view__component-header">
              <span class="score-view__component-label">{{ c.label }}</span>
              <span class="score-view__component-value">{{ Math.round(c.value) }} / {{ c.max }} pts</span>
            </div>
            <div class="score-view__bar">
              <div
                class="score-view__bar-fill"
                :style="{ width: (c.value / c.max * 100) + '%', backgroundColor: color }"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- Amélioration -->
      <div v-if="tips.length" class="score-view__section">
        <p class="score-view__section-title">Comment améliorer ton score</p>
        <div class="score-view__tips">
          <div v-for="t in tips" :key="t.label" class="score-view__tip">
            <p class="score-view__tip-category">{{ t.label }}</p>
            <p class="score-view__tip-text">{{ t.tip }}</p>
          </div>
        </div>
      </div>

      <div v-else class="score-view__perfect">
        🏆 Excellent ! Tous tes indicateurs sont au vert. Continue sur cette lancée.
      </div>

      <!-- Badges -->
      <div v-if="badges.length" class="score-view__section">
        <p class="score-view__section-title">Badges</p>
        <div class="score-view__badges">
          <div
            v-for="b in badges"
            :key="b.id"
            class="score-view__badge"
            :class="{ 'score-view__badge--locked': !b.unlocked }"
          >
            <span class="score-view__badge-icon">{{ b.unlocked ? b.icon : '🔒' }}</span>
            <p class="score-view__badge-name">{{ b.name }}</p>
            <p class="score-view__badge-desc">{{ b.description }}</p>
          </div>
        </div>
      </div>
      <!-- Quiz -->
      <div class="score-view__section">
        <p class="score-view__section-title">Quiz financier</p>
        <QuizCard />
      </div>
    </template>

    <p v-else class="score-view__empty">
      Configure ton profil financier pour calculer ton score.
    </p>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.score-view {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  &__title {
    font-size: $font-size-xl;
    font-weight: 700;
  }

  &__loading,
  &__empty {
    font-size: $font-size-sm;
    color: $color-text-muted;
    text-align: center;
    padding: $spacing-lg 0;
  }

  &__ring-wrapper {
    position: relative;
    width: 160px;
    align-self: center;
  }

  &__ring {
    width: 100%;
    height: auto;
  }

  &__ring-content {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  &__score-number {
    font-size: 32px;
    font-weight: 800;
    line-height: 1;
  }

  &__score-max {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__score-label {
    font-size: 11px;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-top: 2px;
  }

  &__explanation {
    font-size: $font-size-sm;
    color: $color-text-muted;
    text-align: center;
    line-height: 1.5;
  }

  &__section {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-radius: $radius-lg;
    padding: $spacing-md $spacing-lg;
    display: flex;
    flex-direction: column;
    gap: $spacing-md;
  }

  &__section-title {
    font-size: $font-size-sm;
    font-weight: 700;
    color: $color-text;
  }

  &__components {
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__component {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__component-header {
    display: flex;
    justify-content: space-between;
  }

  &__component-label {
    font-size: $font-size-sm;
    color: $color-text;
  }

  &__component-value {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-text-muted;
  }

  &__bar {
    height: 6px;
    background-color: $color-border;
    border-radius: 3px;
    overflow: hidden;
  }

  &__bar-fill {
    height: 100%;
    border-radius: 3px;
    transition: width 0.5s ease;
  }

  &__tips {
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__tip {
    background-color: #fefce8;
    border-left: 3px solid #F59E0B;
    border-radius: 0 $radius-sm $radius-sm 0;
    padding: $spacing-xs $spacing-sm;
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  &__tip-category {
    font-size: 11px;
    font-weight: 600;
    color: #92400e;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  &__tip-text {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.5;
  }

  &__badges {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: $spacing-sm;
  }

  &__badge {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    padding: $spacing-sm;
    background-color: $color-background;
    border: 1px solid $color-border;
    border-radius: $radius-md;
    text-align: center;

    &--locked {
      opacity: 0.4;
    }
  }

  &__badge-icon {
    font-size: 24px;
  }

  &__badge-name {
    font-size: 11px;
    font-weight: 600;
    color: $color-text;
    line-height: 1.3;
  }

  &__badge-desc {
    font-size: 10px;
    color: $color-text-muted;
    line-height: 1.3;
  }

  &__perfect {
    font-size: $font-size-sm;
    color: $color-text;
    background-color: #f0fdf4;
    border: 1px solid #86efac;
    border-radius: $radius-md;
    padding: $spacing-md;
    text-align: center;
  }
}
</style>
