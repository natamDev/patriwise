<script setup lang="ts">
import { computed } from 'vue'
import type { ExpenseCategory } from '@/types/expense.types'
import { CATEGORY_LABELS } from '@/types/expense.types'

const props = defineProps<{
  data: Partial<Record<ExpenseCategory, number>>
}>()

const total = computed(() =>
  Object.values(props.data).reduce((s, v) => s + (v ?? 0), 0),
)

const items = computed(() =>
  Object.entries(props.data)
    .map(([cat, amount]) => ({
      category: cat as ExpenseCategory,
      label: CATEGORY_LABELS[cat as ExpenseCategory],
      amount: amount ?? 0,
      percent: total.value > 0 ? Math.round(((amount ?? 0) / total.value) * 100) : 0,
    }))
    .sort((a, b) => b.amount - a.amount),
)
</script>

<template>
  <div class="chart">
    <p v-if="items.length === 0" class="chart__empty">Aucune dépense.</p>
    <div v-for="item in items" :key="item.category" class="chart__row">
      <div class="chart__info">
        <span class="chart__label">{{ item.label }}</span>
        <span class="chart__amount">{{ item.amount.toFixed(2) }} €</span>
      </div>
      <div class="chart__bar-bg">
        <div class="chart__bar-fill" :style="{ width: item.percent + '%' }" />
      </div>
      <span class="chart__percent">{{ item.percent }}%</span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.chart {
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;

  &__empty {
    font-size: $font-size-sm;
    color: $color-text-muted;
    text-align: center;
    padding: $spacing-md 0;
  }

  &__row {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
  }

  &__info {
    flex: 0 0 130px;
    display: flex;
    flex-direction: column;
  }

  &__label {
    font-size: $font-size-sm;
    font-weight: 500;
    color: $color-text;
  }

  &__amount {
    font-size: 12px;
    color: $color-text-muted;
  }

  &__bar-bg {
    flex: 1;
    height: 8px;
    background-color: $color-border;
    border-radius: 4px;
    overflow: hidden;
  }

  &__bar-fill {
    height: 100%;
    background-color: $color-primary;
    border-radius: 4px;
    transition: width 0.3s ease;
  }

  &__percent {
    flex: 0 0 35px;
    font-size: $font-size-sm;
    color: $color-text-muted;
    text-align: right;
  }
}
</style>
