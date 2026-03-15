<script setup lang="ts">
import { RouterLink } from 'vue-router'

const tabs = [
  { to: '/',           label: 'Home',       icon: '⊙' },
  { to: '/budget',     label: 'Budget',     icon: '◫' },
  { to: '/goals',      label: 'Objectifs',  icon: '◎' },
  { to: '/learn',      label: 'Apprendre',  icon: '◈' },
  { to: '/projection', label: 'Projection', icon: '◬' },
  { to: '/assistant',  label: 'Assistant',  icon: '◉' },
]
</script>

<template>
  <nav class="bottom-nav">
    <RouterLink
      v-for="tab in tabs"
      :key="tab.to"
      :to="tab.to"
      class="bottom-nav__tab"
      active-class="bottom-nav__tab--active"
      exact-active-class="bottom-nav__tab--active"
    >
      <span class="bottom-nav__icon">{{ tab.icon }}</span>
      <span class="bottom-nav__label">{{ tab.label }}</span>
    </RouterLink>
  </nav>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 480px;
  height: $bottom-nav-height;
  background-color: $color-surface;
  border-top: 1px solid $color-border;
  display: flex;
  align-items: center;
  justify-content: space-around;
  z-index: 100;

  &__tab {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
    text-decoration: none;
    color: $color-text-muted;
    flex: 1;
    padding: $spacing-xs 0;
    transition: color 0.15s ease;

    &--active {
      color: $color-primary;
    }
  }

  &__icon {
    font-size: 20px;
    line-height: 1;
  }

  &__label {
    font-size: 11px;
    font-weight: 500;
  }
}
</style>
