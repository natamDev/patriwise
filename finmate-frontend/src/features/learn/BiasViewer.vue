<script setup lang="ts">
import type { CognitiveBias } from './biases.data'

defineProps<{ bias: CognitiveBias }>()
const emit = defineEmits<{ close: [] }>()
</script>

<template>
  <div class="bias">
    <div class="bias__header">
      <div class="bias__title-row">
        <span class="bias__icon">{{ bias.icon }}</span>
        <h2 class="bias__title">{{ bias.name }}</h2>
      </div>
      <button class="bias__close" type="button" @click="emit('close')">✕</button>
    </div>

    <section class="bias__section">
      <h3 class="bias__section-title">C'est quoi ?</h3>
      <p class="bias__text">{{ bias.definition }}</p>
    </section>

    <section class="bias__section">
      <h3 class="bias__section-title">Exemple concret</h3>
      <div class="bias__example">
        <p class="bias__text">{{ bias.example }}</p>
      </div>
    </section>

    <section class="bias__section">
      <h3 class="bias__section-title">Impact sur tes finances</h3>
      <div class="bias__impact">
        <p class="bias__text">{{ bias.impact }}</p>
      </div>
    </section>

    <section class="bias__section">
      <h3 class="bias__section-title">Comment l'éviter ?</h3>
      <div class="bias__avoid">
        <p class="bias__text">{{ bias.howToAvoid }}</p>
      </div>
    </section>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.bias {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }

  &__title-row {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
  }

  &__icon {
    font-size: 28px;
  }

  &__title {
    font-size: $font-size-xl;
    font-weight: 700;
    color: $color-text;
  }

  &__close {
    background: none;
    border: none;
    color: $color-text-muted;
    font-size: $font-size-base;
    cursor: pointer;
    padding: $spacing-xs;
  }

  &__section {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }

  &__section-title {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-text-muted;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  &__text {
    font-size: $font-size-base;
    color: $color-text;
    line-height: 1.6;
  }

  &__example {
    background-color: #fef3c7;
    border-left: 3px solid #F59E0B;
    border-radius: 0 $radius-sm $radius-sm 0;
    padding: $spacing-sm $spacing-md;
  }

  &__impact {
    background-color: #fef2f2;
    border-left: 3px solid $color-danger;
    border-radius: 0 $radius-sm $radius-sm 0;
    padding: $spacing-sm $spacing-md;
  }

  &__avoid {
    background-color: #f0fdf4;
    border-left: 3px solid $color-secondary;
    border-radius: 0 $radius-sm $radius-sm 0;
    padding: $spacing-sm $spacing-md;
  }
}
</style>
