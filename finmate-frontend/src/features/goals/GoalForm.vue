<script setup lang="ts">
import { reactive } from 'vue'
import { GOAL_TYPE_LABELS } from '@/types/goal.types'
import type { GoalType, CreateGoalPayload } from '@/types/goal.types'

defineProps<{ loading: boolean }>()
const emit = defineEmits<{ submit: [payload: CreateGoalPayload]; cancel: [] }>()

const form = reactive({
  goalName: '',
  goalType: 'OTHER' as GoalType,
  targetAmount: '',
  targetDate: '',
  monthlyContribution: '',
})

const goalTypes = Object.entries(GOAL_TYPE_LABELS) as [GoalType, string][]

function submit() {
  emit('submit', {
    goalName: form.goalName,
    goalType: form.goalType,
    targetAmount: parseFloat(form.targetAmount),
    targetDate: form.targetDate,
    monthlyContribution: parseFloat(form.monthlyContribution),
  })
}
</script>

<template>
  <form class="goal-form" @submit.prevent="submit">
    <div class="goal-form__field">
      <label class="goal-form__label">Nom de l'objectif</label>
      <input v-model="form.goalName" class="goal-form__input" type="text" required placeholder="Ex : Voyage au Japon" />
    </div>

    <div class="goal-form__field">
      <label class="goal-form__label">Type</label>
      <select v-model="form.goalType" class="goal-form__input">
        <option v-for="[value, label] in goalTypes" :key="value" :value="value">{{ label }}</option>
      </select>
    </div>

    <div class="goal-form__field">
      <label class="goal-form__label">Montant cible (€)</label>
      <input v-model="form.targetAmount" class="goal-form__input" type="number" min="0.01" step="0.01" required />
    </div>

    <div class="goal-form__field">
      <label class="goal-form__label">Date cible</label>
      <input v-model="form.targetDate" class="goal-form__input" type="date" required />
    </div>

    <div class="goal-form__field">
      <label class="goal-form__label">Contribution mensuelle (€)</label>
      <input v-model="form.monthlyContribution" class="goal-form__input" type="number" min="0.01" step="0.01" required />
    </div>

    <div class="goal-form__actions">
      <button class="goal-form__cancel" type="button" @click="emit('cancel')">Annuler</button>
      <button class="goal-form__submit" type="submit" :disabled="loading">
        {{ loading ? 'Création...' : 'Créer' }}
      </button>
    </div>
  </form>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.goal-form {
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;
  background-color: $color-surface;
  border: 1px solid $color-border;
  border-radius: $radius-lg;
  padding: $spacing-md $spacing-lg;

  &__field {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__label {
    font-size: $font-size-sm;
    font-weight: 500;
    color: $color-text-muted;
  }

  &__input {
    padding: $spacing-sm $spacing-md;
    border: 1px solid $color-border;
    border-radius: $radius-sm;
    font-size: $font-size-base;
    color: $color-text;
    background-color: #fff;

    &:focus {
      outline: none;
      border-color: $color-primary;
    }
  }

  &__actions {
    display: flex;
    gap: $spacing-sm;
    margin-top: $spacing-xs;
  }

  &__cancel {
    flex: 1;
    padding: $spacing-sm;
    background: none;
    border: 1px solid $color-border;
    border-radius: $radius-md;
    font-size: $font-size-sm;
    color: $color-text-muted;
    cursor: pointer;
  }

  &__submit {
    flex: 1;
    padding: $spacing-sm;
    background-color: $color-primary;
    color: #fff;
    border: none;
    border-radius: $radius-md;
    font-size: $font-size-sm;
    font-weight: 600;
    cursor: pointer;

    &:disabled {
      opacity: 0.6;
    }
  }
}
</style>
