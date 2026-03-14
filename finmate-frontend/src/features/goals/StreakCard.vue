<script setup lang="ts">
import type { SavingStreak } from '@/types/streak.types'

defineProps<{ streak: SavingStreak }>()
</script>

<template>
  <div class="streak">
    <div class="streak__current">
      <span class="streak__icon">🔥</span>
      <div class="streak__info">
        <span class="streak__value">{{ streak.currentStreak }}</span>
        <span class="streak__label">mois consécutifs</span>
      </div>
    </div>
    <div class="streak__best">
      <span class="streak__best-label">Record</span>
      <span class="streak__best-value">{{ streak.longestStreak }} mois</span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.streak {
  background-color: $color-surface;
  border: 1px solid $color-border;
  border-radius: $radius-lg;
  padding: $spacing-md $spacing-lg;
  display: flex;
  align-items: center;
  justify-content: space-between;

  &__current {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
  }

  &__icon {
    font-size: 28px;
  }

  &__info {
    display: flex;
    flex-direction: column;
  }

  &__value {
    font-size: $font-size-xl;
    font-weight: 800;
    color: $color-text;
    line-height: 1;
  }

  &__label {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__best {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 2px;
  }

  &__best-label {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__best-value {
    font-size: $font-size-base;
    font-weight: 600;
    color: $color-secondary;
  }
}
</style>
