<script setup lang="ts">
import { ref } from 'vue'

const allocations = [
  {
    name: 'ETF Monde (MSCI World)',
    percent: 60,
    color: '#1E3A8A',
    riskLevel: 'Modéré',
    description:
      "Investit dans 1 600+ entreprises mondiales. C'est le cœur du portefeuille : croissance long terme avec une bonne diversification géographique.",
  },
  {
    name: 'ETF Obligataire',
    percent: 30,
    color: '#0EA5A4',
    riskLevel: 'Faible',
    description:
      "Les obligations stabilisent le portefeuille lors des crises boursières. Moins de rendement, mais moins de volatilité.",
  },
  {
    name: 'Liquidités',
    percent: 10,
    color: '#F59E0B',
    riskLevel: 'Très faible',
    description:
      "Une réserve de cash permet de saisir les opportunités de marché ou de faire face aux imprévus sans devoir vendre ses investissements.",
  },
]

const selectedIndex = ref<number | null>(null)

function select(i: number) {
  selectedIndex.value = selectedIndex.value === i ? null : i
}
</script>

<template>
  <div class="portfolio">
    <div class="portfolio__bars">
      <div
        v-for="(a, i) in allocations"
        :key="a.name"
        class="portfolio__segment"
        :style="{
          flex: a.percent,
          backgroundColor: a.color,
          opacity: selectedIndex === null || selectedIndex === i ? 1 : 0.3,
        }"
        @click="select(i)"
      />
    </div>

    <div class="portfolio__legend">
      <div
        v-for="(a, i) in allocations"
        :key="a.name"
        class="portfolio__legend-item"
        :class="{ 'portfolio__legend-item--active': selectedIndex === i }"
        @click="select(i)"
      >
        <span class="portfolio__dot" :style="{ backgroundColor: a.color }" />
        <span class="portfolio__legend-name">{{ a.name }}</span>
        <span class="portfolio__legend-percent">{{ a.percent }}%</span>
      </div>
    </div>

    <div v-if="selectedIndex !== null" class="portfolio__detail">
      <div class="portfolio__detail-header">
        <span class="portfolio__detail-name">{{ allocations[selectedIndex].name }}</span>
        <span class="portfolio__detail-risk">Risque : {{ allocations[selectedIndex].riskLevel }}</span>
      </div>
      <p class="portfolio__detail-desc">{{ allocations[selectedIndex].description }}</p>
    </div>
    <p v-else class="portfolio__hint">Appuie sur un segment pour en savoir plus.</p>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.portfolio {
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;

  &__bars {
    display: flex;
    height: 32px;
    border-radius: $radius-sm;
    overflow: hidden;
    gap: 2px;
  }

  &__segment {
    cursor: pointer;
    transition: opacity 0.2s ease;
    border-radius: 2px;
  }

  &__legend {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  &__legend-item {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
    cursor: pointer;
    padding: $spacing-xs;
    border-radius: $radius-sm;
    transition: background-color 0.15s;

    &--active {
      background-color: $color-background;
    }
  }

  &__dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    flex-shrink: 0;
  }

  &__legend-name {
    flex: 1;
    font-size: $font-size-sm;
    color: $color-text;
  }

  &__legend-percent {
    font-size: $font-size-sm;
    font-weight: 700;
    color: $color-text;
  }

  &__detail {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-radius: $radius-lg;
    padding: $spacing-md;
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }

  &__detail-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  &__detail-name {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-text;
  }

  &__detail-risk {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__detail-desc {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.5;
  }

  &__hint {
    font-size: $font-size-sm;
    color: $color-text-muted;
    text-align: center;
  }
}
</style>
