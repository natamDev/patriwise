<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{ rate: number }>()

const percentage = computed(() => Math.round(props.rate * 100))

const statusLabel = computed(() => {
  if (percentage.value >= 20) return 'Excellent'
  if (percentage.value >= 10) return 'Bien'
  if (percentage.value > 0) return 'À améliorer'
  return 'Attention'
})
</script>

<template>
  <div class="savings-rate">
    <p class="savings-rate__label">Taux d'épargne</p>
    <p class="savings-rate__value">{{ percentage }} %</p>
    <p class="savings-rate__status">{{ statusLabel }}</p>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.savings-rate {
  background-color: $color-surface;
  border: 1px solid $color-border;
  border-radius: $radius-lg;
  padding: $spacing-md $spacing-lg;

  &__label {
    font-size: $font-size-sm;
    color: $color-text-muted;
    margin-bottom: $spacing-xs;
  }

  &__value {
    font-size: $font-size-xl;
    font-weight: 700;
    color: $color-primary;
  }

  &__status {
    font-size: $font-size-sm;
    color: $color-text-muted;
    margin-top: 2px;
  }
}
</style>
