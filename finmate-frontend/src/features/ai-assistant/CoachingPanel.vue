<script setup lang="ts">
import { ref } from 'vue'
import { assistantApi } from '@/api/assistant.api'
import type { Recommendation } from '@/types/assistant.types'

const coaching = ref<Recommendation | null>(null)
const coachingLoading = ref(false)

const RECOMMENDATION_LABELS: Record<string, string> = {
  COMPLETE_PROFILE: '👤 Complète ton profil',
  INCREASE_SAVINGS: '💰 Augmente ton épargne',
  REDUCE_EXPENSES: '✂️ Réduis tes dépenses',
  START_INVESTING: '📈 Commence à investir',
  CONTRIBUTE_TO_GOALS: '🎯 Alimente tes objectifs',
  WELL_DONE: '🏆 Bien joué !',
}

async function loadCoaching() {
  coachingLoading.value = true
  try {
    coaching.value = await assistantApi.coaching()
  } finally {
    coachingLoading.value = false
  }
}
</script>

<template>
  <div class="coaching">
    <div v-if="!coaching" class="coaching__trigger">
      <p class="coaching__trigger-text">Analyse ta situation financière et reçois un conseil personnalisé.</p>
      <button
        class="coaching__btn"
        type="button"
        :disabled="coachingLoading"
        @click="loadCoaching"
      >
        {{ coachingLoading ? 'Analyse en cours...' : '🔍 Mon coaching personnalisé' }}
      </button>
    </div>

    <div v-else class="coaching__card">
      <p class="coaching__type">{{ RECOMMENDATION_LABELS[coaching.recommendationType] ?? coaching.recommendationType }}</p>
      <p class="coaching__message">{{ coaching.message }}</p>
      <div class="coaching__action">
        <p class="coaching__action-label">Action recommandée</p>
        <p class="coaching__action-text">{{ coaching.suggestedAction }}</p>
      </div>
      <button class="coaching__refresh" type="button" @click="loadCoaching">
        Actualiser
      </button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.coaching {
  &__trigger {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-radius: $radius-lg;
    padding: $spacing-md;
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__trigger-text {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__btn {
    padding: $spacing-sm $spacing-md;
    background-color: $color-primary;
    color: #fff;
    border: none;
    border-radius: $radius-md;
    font-size: $font-size-sm;
    font-weight: 600;
    cursor: pointer;
    transition: opacity 0.15s;

    &:disabled {
      opacity: 0.6;
      cursor: default;
    }
  }

  &__card {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-left: 3px solid $color-secondary;
    border-radius: $radius-lg;
    padding: $spacing-md;
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__type {
    font-size: $font-size-base;
    font-weight: 700;
    color: $color-text;
  }

  &__message {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.6;
  }

  &__action {
    background-color: #f0fdf4;
    border-radius: $radius-md;
    padding: $spacing-xs $spacing-sm;
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  &__action-label {
    font-size: 11px;
    font-weight: 600;
    color: $color-secondary;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  &__action-text {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.5;
  }

  &__refresh {
    align-self: flex-end;
    background: none;
    border: 1px solid $color-border;
    border-radius: $radius-md;
    padding: 4px $spacing-sm;
    font-size: 12px;
    color: $color-text-muted;
    cursor: pointer;
  }
}
</style>
