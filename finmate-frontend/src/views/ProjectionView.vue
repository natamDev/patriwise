<script setup lang="ts">
import WealthProjectionChart from '@/features/projections/WealthProjectionChart.vue'
import ScenarioComparisonChart from '@/features/projections/ScenarioComparisonChart.vue'
</script>

<template>
  <div class="page projection">
    <h1 class="projection__title">Projections financières</h1>
    <p class="projection__subtitle">Visualise l'impact futur de ton épargne et de tes investissements.</p>

    <div class="projection__section">
      <p class="projection__section-title">Simulateur de patrimoine</p>
      <p class="projection__section-desc">
        Ajuste les curseurs pour voir comment ton épargne mensuelle, ton rendement et ton horizon transforment ton capital au fil du temps.
      </p>
      <WealthProjectionChart />
    </div>
    <div class="projection__section">
      <p class="projection__section-title">Comparaison de scénarios</p>
      <p class="projection__section-desc">
        Configure deux scénarios et compare leur impact sur le long terme. Idéal pour visualiser la différence entre épargne classique et investissement en ETF.
      </p>
      <ScenarioComparisonChart />
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.projection {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  &__title {
    font-size: $font-size-xl;
    font-weight: 700;
  }

  &__subtitle {
    font-size: $font-size-sm;
    color: $color-text-muted;
    margin-top: -$spacing-xs;
  }

  &__section {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-radius: $radius-lg;
    padding: $spacing-md $spacing-lg;
    display: flex;
    flex-direction: column;
    gap: $spacing-md;
  }

  &__section-title {
    font-size: $font-size-base;
    font-weight: 700;
    color: $color-text;
  }

  &__section-desc {
    font-size: $font-size-sm;
    color: $color-text-muted;
    line-height: 1.5;
    margin-top: -$spacing-xs;
  }
}
</style>
