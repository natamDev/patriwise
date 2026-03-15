<script setup lang="ts">
import { assistantApi } from '@/api/assistant.api'
import type { Motivation } from '@/types/assistant.types'
import { useAsyncAction } from '@/composables/useAsyncAction'

const { data: motivation, loading: motivationLoading, error: motivationError, execute } = useAsyncAction<Motivation>()

function loadMotivation() {
  execute(
    () => assistantApi.motivation(),
    'Impossible de générer le message. Réessaie.'
  )
}
</script>

<template>
  <div class="motivation">
    <div v-if="!motivation" class="motivation__trigger">
      <p class="motivation__trigger-text">Recevez un message d'encouragement basé sur vos progrès financiers.</p>
      <p v-if="motivationError" class="motivation__error">{{ motivationError }}</p>
      <button
        class="motivation__btn"
        type="button"
        :disabled="motivationLoading"
        @click="loadMotivation"
      >
        {{ motivationLoading ? 'Génération en cours...' : '💪 Mon encouragement du jour' }}
      </button>
    </div>

    <div v-else class="motivation__card">
      <p class="motivation__title">💪 Ton encouragement du jour</p>

      <div class="motivation__stats">
        <div class="motivation__stat">
          <p class="motivation__stat-value">🔥 {{ motivation.currentStreak }}</p>
          <p class="motivation__stat-label">mois de streak</p>
        </div>
        <div class="motivation__stat">
          <p class="motivation__stat-value">⭐ {{ motivation.financialScore }}/100</p>
          <p class="motivation__stat-label">{{ motivation.scoreLabel }}</p>
        </div>
        <div v-if="motivation.activeGoals > 0" class="motivation__stat">
          <p class="motivation__stat-value">🎯 {{ motivation.averageGoalProgress }}%</p>
          <p class="motivation__stat-label">objectifs en cours</p>
        </div>
      </div>

      <p class="motivation__message">{{ motivation.message }}</p>

      <button class="motivation__refresh" type="button" @click="loadMotivation">
        Actualiser
      </button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.motivation {
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

  &__error {
    font-size: $font-size-sm;
    color: $color-danger;
  }

  &__btn {
    padding: $spacing-sm $spacing-md;
    background-color: $color-secondary;
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
    background-color: #f0fdf4;
    border: 1px solid #86efac;
    border-left: 3px solid $color-secondary;
    border-radius: $radius-lg;
    padding: $spacing-md;
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__title {
    font-size: $font-size-base;
    font-weight: 700;
    color: $color-text;
  }

  &__stats {
    display: flex;
    gap: $spacing-sm;
  }

  &__stat {
    flex: 1;
    background-color: #fff;
    border: 1px solid #86efac;
    border-radius: $radius-md;
    padding: $spacing-sm;
    text-align: center;
  }

  &__stat-value {
    font-size: $font-size-base;
    font-weight: 700;
    color: $color-text;
  }

  &__stat-label {
    font-size: 11px;
    color: $color-text-muted;
    margin-top: 2px;
  }

  &__message {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.6;
    white-space: pre-wrap;
  }

  &__refresh {
    align-self: flex-end;
    background: none;
    border: 1px solid #86efac;
    border-radius: $radius-md;
    padding: 4px $spacing-sm;
    font-size: 12px;
    color: $color-secondary;
    cursor: pointer;
  }
}
</style>
