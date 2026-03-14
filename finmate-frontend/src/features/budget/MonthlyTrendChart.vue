<script setup lang="ts">
import { computed } from 'vue'
import type { MonthTrend } from '@/types/budget.types'

const props = defineProps<{ data: MonthTrend[] }>()

const max = computed(() =>
  Math.max(...props.data.flatMap((d) => [d.income, d.expenses]), 1),
)

function pct(value: number): number {
  return Math.round((value / max.value) * 100)
}

function shortMonth(month: string): string {
  const d = new Date(month + '-01')
  return d.toLocaleDateString('fr-FR', { month: 'short' })
}
</script>

<template>
  <div class="trend">
    <p v-if="data.length === 0" class="trend__empty">Aucune donnée.</p>
    <div v-else class="trend__chart">
      <div v-for="item in data" :key="item.month" class="trend__col">
        <div class="trend__bars">
          <div class="trend__bar-wrap">
            <div class="trend__bar trend__bar--income" :style="{ height: pct(item.income) + '%' }" />
          </div>
          <div class="trend__bar-wrap">
            <div class="trend__bar trend__bar--expenses" :style="{ height: pct(item.expenses) + '%' }" />
          </div>
        </div>
        <span class="trend__label">{{ shortMonth(item.month) }}</span>
      </div>
    </div>
    <div class="trend__legend">
      <span class="trend__legend-item trend__legend-item--income">Revenus</span>
      <span class="trend__legend-item trend__legend-item--expenses">Dépenses</span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.trend {
  &__empty {
    font-size: $font-size-sm;
    color: $color-text-muted;
    text-align: center;
    padding: $spacing-md 0;
  }

  &__chart {
    display: flex;
    align-items: flex-end;
    gap: $spacing-xs;
    height: 100px;
  }

  &__col {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    height: 100%;
  }

  &__bars {
    flex: 1;
    display: flex;
    align-items: flex-end;
    gap: 2px;
    width: 100%;
  }

  &__bar-wrap {
    flex: 1;
    height: 100%;
    display: flex;
    align-items: flex-end;
  }

  &__bar {
    width: 100%;
    border-radius: 3px 3px 0 0;
    min-height: 2px;
    transition: height 0.3s ease;

    &--income {
      background-color: $color-secondary;
    }

    &--expenses {
      background-color: #EF4444;
    }
  }

  &__label {
    font-size: 10px;
    color: $color-text-muted;
    text-transform: capitalize;
  }

  &__legend {
    display: flex;
    gap: $spacing-md;
    margin-top: $spacing-sm;
    justify-content: center;
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
      width: 10px;
      height: 10px;
      border-radius: 2px;
    }

    &--income::before {
      background-color: $color-secondary;
    }

    &--expenses::before {
      background-color: #EF4444;
    }
  }
}
</style>
