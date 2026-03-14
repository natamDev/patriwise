<script setup lang="ts">
defineProps<{
  label: string
  amount: number
  color?: string
}>()

function formatAmount(n: number): string {
  return n.toFixed(2)
}
</script>

<template>
  <div class="card">
    <p class="card__label">{{ label }}</p>
    <p class="card__amount" :style="color ? { color } : {}">{{ formatAmount(amount) }} €</p>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.card {
  background-color: $color-surface;
  border: 1px solid $color-border;
  border-radius: $radius-lg;
  padding: $spacing-md $spacing-lg;

  &__label {
    font-size: $font-size-sm;
    color: $color-text-muted;
    margin-bottom: $spacing-xs;
  }

  &__amount {
    font-size: $font-size-lg;
    font-weight: 700;
    color: $color-text;
  }
}
</style>
