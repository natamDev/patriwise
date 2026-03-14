<script setup lang="ts">
import { computed } from 'vue'
import type { FinancialScore } from '@/types/score.types'

const props = defineProps<{ data: FinancialScore }>()

const labelText = computed(() => {
  switch (props.data.label) {
    case 'healthy': return 'Solide'
    case 'improving': return 'En progression'
    default: return 'À améliorer'
  }
})

const scoreColor = computed(() => {
  switch (props.data.label) {
    case 'healthy': return '#0EA5A4'
    case 'improving': return '#F59E0B'
    default: return '#EF4444'
  }
})

const breakdown = computed(() => [
  { label: 'Épargne', value: props.data.savingsRateScore, max: 50 },
  { label: 'Dépenses', value: props.data.expenseControlScore, max: 20 },
  { label: 'Stabilité', value: props.data.financialStabilityScore, max: 10 },
  { label: 'Objectifs', value: props.data.goalProgressScore, max: 20 },
])
</script>

<template>
  <div class="score-card">
    <div class="score-card__header">
      <div class="score-card__score-wrap">
        <span class="score-card__score" :style="{ color: scoreColor }">{{ data.score }}</span>
        <span class="score-card__max">/100</span>
      </div>
      <span class="score-card__label" :style="{ color: scoreColor }">{{ labelText }}</span>
    </div>

    <div class="score-card__bar-bg">
      <div
        class="score-card__bar-fill"
        :style="{ width: data.score + '%', backgroundColor: scoreColor }"
      />
    </div>

    <p class="score-card__explanation">{{ data.explanation }}</p>

    <div class="score-card__breakdown">
      <div v-for="item in breakdown" :key="item.label" class="score-card__breakdown-row">
        <span class="score-card__breakdown-label">{{ item.label }}</span>
        <div class="score-card__breakdown-bar-bg">
          <div
            class="score-card__breakdown-bar-fill"
            :style="{ width: (item.value / item.max * 100) + '%', backgroundColor: scoreColor }"
          />
        </div>
        <span class="score-card__breakdown-value">{{ Math.round(item.value) }}/{{ item.max }}</span>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.score-card {
  background-color: $color-surface;
  border: 1px solid $color-border;
  border-radius: $radius-lg;
  padding: $spacing-md $spacing-lg;
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;

  &__header {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
  }

  &__score-wrap {
    display: flex;
    align-items: baseline;
    gap: 2px;
  }

  &__score {
    font-size: 40px;
    font-weight: 800;
    line-height: 1;
  }

  &__max {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__label {
    font-size: $font-size-sm;
    font-weight: 600;
  }

  &__bar-bg {
    height: 8px;
    background-color: $color-border;
    border-radius: 4px;
    overflow: hidden;
  }

  &__bar-fill {
    height: 100%;
    border-radius: 4px;
    transition: width 0.4s ease;
  }

  &__explanation {
    font-size: $font-size-sm;
    color: $color-text-muted;
    line-height: 1.5;
  }

  &__breakdown {
    display: flex;
    flex-direction: column;
    gap: 6px;
    margin-top: $spacing-xs;
  }

  &__breakdown-row {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
  }

  &__breakdown-label {
    flex: 0 0 70px;
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__breakdown-bar-bg {
    flex: 1;
    height: 5px;
    background-color: $color-border;
    border-radius: 3px;
    overflow: hidden;
  }

  &__breakdown-bar-fill {
    height: 100%;
    border-radius: 3px;
    transition: width 0.4s ease;
  }

  &__breakdown-value {
    flex: 0 0 40px;
    font-size: 11px;
    color: $color-text-muted;
    text-align: right;
  }
}
</style>
