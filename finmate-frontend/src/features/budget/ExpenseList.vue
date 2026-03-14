<script setup lang="ts">
import type { Expense } from '@/types/expense.types'
import { CATEGORY_LABELS } from '@/types/expense.types'

defineProps<{
  expenses: Expense[]
  deleting: string | null
}>()

const emit = defineEmits<{ delete: [id: string] }>()

function formatAmount(amount: number): string {
  return amount.toFixed(2)
}

function formatDate(date: string): string {
  return new Date(date).toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' })
}
</script>

<template>
  <div class="expense-list">
    <p v-if="expenses.length === 0" class="expense-list__empty">Aucune dépense ce mois.</p>
    <div v-for="expense in expenses" :key="expense.id" class="expense-list__item">
      <div class="expense-list__info">
        <span class="expense-list__category">{{ CATEGORY_LABELS[expense.category] }}</span>
        <span class="expense-list__desc">{{ expense.description }}</span>
        <span class="expense-list__date">{{ formatDate(expense.expenseDate) }}</span>
      </div>
      <div class="expense-list__actions">
        <span class="expense-list__amount">{{ formatAmount(expense.amount) }} €</span>
        <button
          class="expense-list__delete"
          type="button"
          :disabled="deleting === expense.id"
          @click="emit('delete', expense.id)"
        >
          ×
        </button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.expense-list {
  display: flex;
  flex-direction: column;

  &__empty {
    font-size: $font-size-sm;
    color: $color-text-muted;
    text-align: center;
    padding: $spacing-lg 0;
  }

  &__item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: $spacing-md 0;
    border-bottom: 1px solid $color-border;

    &:last-child {
      border-bottom: none;
    }
  }

  &__info {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  &__category {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-text;
  }

  &__desc {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__date {
    font-size: 12px;
    color: $color-text-muted;
  }

  &__actions {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
  }

  &__amount {
    font-size: $font-size-base;
    font-weight: 600;
    color: $color-text;
  }

  &__delete {
    background: none;
    border: none;
    color: $color-danger;
    font-size: 20px;
    cursor: pointer;
    padding: 0 $spacing-xs;
    line-height: 1;
  }
}
</style>
