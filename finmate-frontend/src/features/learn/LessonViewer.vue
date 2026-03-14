<script setup lang="ts">
import type { Lesson } from './lessons.data'
import VolatilityChart from './VolatilityChart.vue'

defineProps<{ lesson: Lesson }>()
const emit = defineEmits<{ close: [] }>()
</script>

<template>
  <div class="lesson">
    <div class="lesson__header">
      <span class="lesson__category">{{ lesson.category }}</span>
      <button class="lesson__close" type="button" @click="emit('close')">✕</button>
    </div>

    <h2 class="lesson__title">{{ lesson.title }}</h2>

    <section class="lesson__section">
      <h3 class="lesson__section-title">Définition</h3>
      <p class="lesson__text">{{ lesson.definition }}</p>
    </section>

    <section v-if="lesson.chartId === 'volatility'" class="lesson__section">
      <h3 class="lesson__section-title">Fluctuations vs tendance long terme</h3>
      <VolatilityChart />
    </section>

    <section class="lesson__section">
      <h3 class="lesson__section-title">Exemple historique</h3>
      <div class="lesson__example">
        <p class="lesson__text">{{ lesson.example }}</p>
      </div>
    </section>

    <section class="lesson__section">
      <h3 class="lesson__section-title">Avantages</h3>
      <ul class="lesson__list lesson__list--positive">
        <li v-for="a in lesson.advantages" :key="a">{{ a }}</li>
      </ul>
    </section>

    <section class="lesson__section">
      <h3 class="lesson__section-title">Risques</h3>
      <ul class="lesson__list lesson__list--negative">
        <li v-for="r in lesson.risks" :key="r">{{ r }}</li>
      </ul>
    </section>

    <div class="lesson__summary">
      <p class="lesson__summary-text">{{ lesson.summary }}</p>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.lesson {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  &__category {
    font-size: $font-size-sm;
    color: $color-secondary;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  &__close {
    background: none;
    border: none;
    color: $color-text-muted;
    font-size: $font-size-base;
    cursor: pointer;
    padding: $spacing-xs;
  }

  &__title {
    font-size: $font-size-xl;
    font-weight: 700;
    color: $color-text;
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
    background-color: #f0f9ff;
    border-left: 3px solid $color-secondary;
    border-radius: 0 $radius-sm $radius-sm 0;
    padding: $spacing-sm $spacing-md;
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: 6px;
    padding-left: 0;
    list-style: none;

    li {
      font-size: $font-size-sm;
      color: $color-text;
      line-height: 1.5;
      padding-left: $spacing-md;
      position: relative;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 7px;
        width: 8px;
        height: 8px;
        border-radius: 50%;
      }
    }

    &--positive li::before {
      background-color: $color-secondary;
    }

    &--negative li::before {
      background-color: #F59E0B;
    }
  }

  &__summary {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-radius: $radius-lg;
    padding: $spacing-md $spacing-lg;
  }

  &__summary-text {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.6;
    font-style: italic;
  }
}
</style>
