<script setup lang="ts">
import { ref } from 'vue'
import { GOAL_TYPE_LABELS } from '@/types/goal.types'
import type { Goal, GoalProgress } from '@/types/goal.types'
import GoalProgressBar from './GoalProgressBar.vue'

const props = defineProps<{
  goals: Goal[]
  progress: Record<string, GoalProgress>
  deleting: string | null
  contributing: string | null
}>()

const emit = defineEmits<{
  delete: [id: string]
  contribute: [id: string, amount: number]
}>()

const contributionInput = ref<Record<string, string>>({})

function formatDate(date: string): string {
  return new Date(date).toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' })
}

function submitContribution(goalId: string) {
  const val = parseFloat(contributionInput.value[goalId] ?? '')
  if (!val || val <= 0) return
  emit('contribute', goalId, val)
  contributionInput.value[goalId] = ''
}
</script>

<template>
  <div class="goal-list">
    <p v-if="goals.length === 0" class="goal-list__empty">Aucun objectif créé.</p>
    <div v-for="goal in goals" :key="goal.id" class="goal-list__item">
      <div class="goal-list__header">
        <div class="goal-list__info">
          <span class="goal-list__name">{{ goal.goalName }}</span>
          <span class="goal-list__type">{{ GOAL_TYPE_LABELS[goal.goalType] }}</span>
          <span class="goal-list__contribution">{{ goal.monthlyContribution.toFixed(2) }} €/mois prévu</span>
        </div>
        <div class="goal-list__meta">
          <span class="goal-list__target">{{ goal.targetAmount.toFixed(2) }} €</span>
          <span class="goal-list__date">Objectif : {{ formatDate(goal.targetDate) }}</span>
        </div>
        <button
          class="goal-list__delete"
          type="button"
          :disabled="deleting === goal.id"
          @click="emit('delete', goal.id)"
        >
          {{ deleting === goal.id ? '...' : '✕' }}
        </button>
      </div>

      <GoalProgressBar v-if="progress[goal.id]" :progress="progress[goal.id]" />

      <div class="goal-list__contribute">
        <input
          v-model="contributionInput[goal.id]"
          class="goal-list__contribute-input"
          type="number"
          min="0.01"
          step="0.01"
          placeholder="Montant (€)"
          @keydown.enter="submitContribution(goal.id)"
        />
        <button
          class="goal-list__contribute-btn"
          type="button"
          :disabled="contributing === goal.id"
          @click="submitContribution(goal.id)"
        >
          {{ contributing === goal.id ? '...' : '+ Épargner' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.goal-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;

  &__empty {
    font-size: $font-size-sm;
    color: $color-text-muted;
    text-align: center;
    padding: $spacing-lg 0;
  }

  &__item {
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-radius: $radius-lg;
    padding: $spacing-md $spacing-lg;
  }

  &__header {
    display: flex;
    align-items: flex-start;
    gap: $spacing-sm;
  }

  &__info {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  &__name {
    font-size: $font-size-base;
    font-weight: 600;
    color: $color-text;
  }

  &__type {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__contribution {
    font-size: $font-size-sm;
    color: $color-secondary;
    font-weight: 500;
  }

  &__meta {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 2px;
  }

  &__target {
    font-size: $font-size-base;
    font-weight: 700;
    color: $color-primary;
  }

  &__date {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__delete {
    background: none;
    border: none;
    color: $color-text-muted;
    font-size: $font-size-sm;
    cursor: pointer;
    padding: $spacing-xs;
    align-self: flex-start;

    &:disabled {
      opacity: 0.4;
    }
  }

  &__contribute {
    display: flex;
    gap: $spacing-sm;
  }

  &__contribute-input {
    flex: 1;
    padding: $spacing-xs $spacing-sm;
    border: 1px solid $color-border;
    border-radius: $radius-sm;
    font-size: $font-size-sm;
    color: $color-text;
    background-color: #fff;

    &:focus {
      outline: none;
      border-color: $color-secondary;
    }
  }

  &__contribute-btn {
    padding: $spacing-xs $spacing-md;
    background-color: $color-secondary;
    color: #fff;
    border: none;
    border-radius: $radius-sm;
    font-size: $font-size-sm;
    font-weight: 600;
    cursor: pointer;
    white-space: nowrap;

    &:disabled {
      opacity: 0.6;
    }
  }
}
</style>
