<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { CreateExpensePayload, ExpenseCategory } from '@/types/expense.types'
import { CATEGORY_LABELS } from '@/types/expense.types'

const props = defineProps<{ loading: boolean }>()
const emit = defineEmits<{ submit: [payload: CreateExpensePayload] }>()

const today = new Date().toISOString().slice(0, 10)

const form = reactive<CreateExpensePayload>({
  amount: 0,
  category: 'OTHER',
  description: '',
  expenseDate: today,
})

const categories = Object.entries(CATEGORY_LABELS) as [ExpenseCategory, string][]

function handleSubmit() {
  emit('submit', { ...form })
  form.amount = 0
  form.description = ''
  form.expenseDate = today
}
</script>

<template>
  <form class="expense-form" @submit.prevent="handleSubmit">
    <div class="expense-form__row">
      <div class="expense-form__field expense-form__field--amount">
        <label class="expense-form__label" for="exp-amount">Montant</label>
        <input
          id="exp-amount"
          v-model.number="form.amount"
          class="expense-form__input"
          type="number"
          min="0.01"
          step="0.01"
          required
        />
      </div>
      <div class="expense-form__field expense-form__field--category">
        <label class="expense-form__label" for="exp-category">Catégorie</label>
        <select id="exp-category" v-model="form.category" class="expense-form__select">
          <option v-for="[value, label] in categories" :key="value" :value="value">
            {{ label }}
          </option>
        </select>
      </div>
    </div>

    <div class="expense-form__field">
      <label class="expense-form__label" for="exp-desc">Description</label>
      <input
        id="exp-desc"
        v-model="form.description"
        class="expense-form__input"
        type="text"
        required
      />
    </div>

    <div class="expense-form__field">
      <label class="expense-form__label" for="exp-date">Date</label>
      <input
        id="exp-date"
        v-model="form.expenseDate"
        class="expense-form__input"
        type="date"
        required
      />
    </div>

    <button class="expense-form__submit" type="submit" :disabled="props.loading">
      {{ props.loading ? '...' : 'Ajouter' }}
    </button>
  </form>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.expense-form {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  &__row {
    display: flex;
    gap: $spacing-md;
  }

  &__field {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;

    &--amount {
      flex: 1;
    }

    &--category {
      flex: 1;
    }
  }

  &__label {
    font-size: $font-size-sm;
    font-weight: 500;
    color: $color-text-muted;
  }

  &__input,
  &__select {
    padding: $spacing-sm $spacing-md;
    border: 1px solid $color-border;
    border-radius: $radius-sm;
    font-size: $font-size-base;
    color: $color-text;

    &:focus {
      outline: none;
      border-color: $color-primary;
    }
  }

  &__submit {
    padding: $spacing-sm $spacing-md;
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
