<script setup lang="ts">
import type { GoalProgress } from '@/types/goal.types'

defineProps<{ progress: GoalProgress }>()

function formatDate(date: string): string {
  return new Date(date).toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' })
}
</script>

<template>
  <div class="progress">
    <div class="progress__bar-bg">
      <div class="progress__bar-fill" :style="{ width: Math.min(progress.percent, 100) + '%' }" />
    </div>
    <div class="progress__stats">
      <span class="progress__saved">{{ progress.savedAmount.toFixed(2) }} € épargnés</span>
      <span class="progress__percent">{{ progress.percent }}%</span>
    </div>
    <div class="progress__plan">
      <span v-if="progress.remainingAmount > 0 && progress.monthsNeeded != null" class="progress__remaining">
        Il reste {{ progress.remainingAmount.toFixed(2) }} € — encore {{ progress.monthsNeeded }} mois
        <template v-if="progress.estimatedCompletionDate">
          ({{ formatDate(progress.estimatedCompletionDate) }})
        </template>
      </span>
      <span v-else-if="progress.remainingAmount <= 0" class="progress__done">
        Objectif atteint ! 🎉
      </span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.progress {
  display: flex;
  flex-direction: column;
  gap: 4px;

  &__bar-bg {
    height: 8px;
    background-color: $color-border;
    border-radius: 4px;
    overflow: hidden;
  }

  &__bar-fill {
    height: 100%;
    background-color: $color-secondary;
    border-radius: 4px;
    transition: width 0.4s ease;
  }

  &__stats {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  &__saved {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__percent {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-secondary;
  }

  &__plan {
    min-height: 16px;
  }

  &__remaining {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__done {
    font-size: $font-size-sm;
    color: $color-secondary;
    font-weight: 600;
  }
}
</style>
