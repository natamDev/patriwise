<script setup lang="ts">
import { ref } from 'vue'
import { assistantApi } from '@/api/assistant.api'
import type { DecisionCoaching } from '@/types/assistant.types'
import { useAsyncAction } from '@/composables/useAsyncAction'

const { data: decisionResult, loading: decisionLoading, error: decisionError, execute, reset } = useAsyncAction<DecisionCoaching>()

const decisionForm = ref({
  decisionContext: '',
  whyInvesting: '',
  investmentHorizon: 'MEDIUM',
  riskTolerance: 'MODERATE',
  financialGoal: '',
})

function submitDecisionCoaching() {
  const f = decisionForm.value
  if (!f.decisionContext.trim() || !f.whyInvesting.trim() || !f.financialGoal.trim()) return
  execute(
    () => assistantApi.decisionCoaching(
      f.decisionContext, f.whyInvesting, f.investmentHorizon, f.riskTolerance, f.financialGoal
    ),
    'Impossible de générer le coaching. Réessaie.'
  )
}

function resetDecision() {
  reset()
  decisionForm.value = { decisionContext: '', whyInvesting: '', investmentHorizon: 'MEDIUM', riskTolerance: 'MODERATE', financialGoal: '' }
}
</script>

<template>
  <div class="decision-coaching">
    <div v-if="!decisionResult" class="decision-coaching__form-card">
      <p class="decision-coaching__title">🤔 Coaching avant décision</p>
      <p class="decision-coaching__subtitle">Prenez le temps de réfléchir avant d'agir.</p>

      <div class="decision-coaching__field">
        <label class="decision-coaching__label">Quelle décision envisagez-vous ?</label>
        <textarea
          v-model="decisionForm.decisionContext"
          class="decision-coaching__textarea"
          placeholder="Ex : J'envisage d'investir 5 000 € dans des cryptos..."
          rows="2"
        />
      </div>

      <div class="decision-coaching__field">
        <label class="decision-coaching__label">Pourquoi voulez-vous investir ?</label>
        <textarea
          v-model="decisionForm.whyInvesting"
          class="decision-coaching__textarea"
          placeholder="Ex : Pour préparer ma retraite / J'ai peur de rater une opportunité..."
          rows="2"
        />
      </div>

      <div class="decision-coaching__field">
        <label class="decision-coaching__label">Horizon d'investissement</label>
        <div class="decision-coaching__radio-group">
          <label v-for="opt in [
            { val: 'SHORT', label: '< 1 an' },
            { val: 'MEDIUM', label: '1-5 ans' },
            { val: 'LONG', label: '5-10 ans' },
            { val: 'VERY_LONG', label: '> 10 ans' },
          ]" :key="opt.val" class="decision-coaching__radio">
            <input type="radio" v-model="decisionForm.investmentHorizon" :value="opt.val" />
            <span>{{ opt.label }}</span>
          </label>
        </div>
      </div>

      <div class="decision-coaching__field">
        <label class="decision-coaching__label">Tolérance au risque</label>
        <div class="decision-coaching__radio-group">
          <label v-for="opt in [
            { val: 'LOW', label: 'Faible' },
            { val: 'MODERATE', label: 'Modérée' },
            { val: 'HIGH', label: 'Élevée' },
          ]" :key="opt.val" class="decision-coaching__radio">
            <input type="radio" v-model="decisionForm.riskTolerance" :value="opt.val" />
            <span>{{ opt.label }}</span>
          </label>
        </div>
      </div>

      <div class="decision-coaching__field">
        <label class="decision-coaching__label">Quel est votre objectif financier ?</label>
        <textarea
          v-model="decisionForm.financialGoal"
          class="decision-coaching__textarea"
          placeholder="Ex : Atteindre 50 000 € dans 5 ans pour un apport immobilier..."
          rows="2"
        />
      </div>

      <p v-if="decisionError" class="decision-coaching__error">{{ decisionError }}</p>

      <button
        class="decision-coaching__btn"
        type="button"
        :disabled="decisionLoading || !decisionForm.decisionContext.trim() || !decisionForm.whyInvesting.trim() || !decisionForm.financialGoal.trim()"
        @click="submitDecisionCoaching"
      >
        {{ decisionLoading ? 'Analyse en cours...' : '🧠 Obtenir mon coaching' }}
      </button>
    </div>

    <div v-else class="decision-coaching__result">
      <p class="decision-coaching__title">🧠 Résultat de votre coaching décision</p>
      <div class="decision-coaching__context-recap">
        <p class="decision-coaching__recap-item"><strong>Décision :</strong> {{ decisionResult.decisionContext }}</p>
        <p class="decision-coaching__recap-item"><strong>Pourquoi :</strong> {{ decisionResult.whyInvesting }}</p>
      </div>
      <p class="decision-coaching__recommendation">{{ decisionResult.recommendation }}</p>
      <button class="decision-coaching__reset" type="button" @click="resetDecision">
        Nouvelle décision
      </button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.decision-coaching {
  &__form-card,
  &__result {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-left: 3px solid #7c3aed;
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

  &__subtitle {
    font-size: $font-size-sm;
    color: $color-text-muted;
    margin-top: -$spacing-xs;
  }

  &__field {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__label {
    font-size: 11px;
    font-weight: 600;
    color: $color-text-muted;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  &__textarea {
    padding: $spacing-xs $spacing-sm;
    border: 1px solid $color-border;
    border-radius: $radius-md;
    background-color: #fff;
    font-size: $font-size-sm;
    color: $color-text;
    font-family: inherit;
    resize: none;
    outline: none;
    line-height: 1.5;

    &:focus {
      border-color: #7c3aed;
    }
  }

  &__radio-group {
    display: flex;
    gap: $spacing-sm;
    flex-wrap: wrap;
  }

  &__radio {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: $font-size-sm;
    color: $color-text;
    cursor: pointer;
  }

  &__error {
    font-size: $font-size-sm;
    color: $color-danger;
  }

  &__btn {
    padding: $spacing-sm $spacing-md;
    background-color: #7c3aed;
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

  &__context-recap {
    background-color: #f5f3ff;
    border-radius: $radius-md;
    padding: $spacing-xs $spacing-sm;
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__recap-item {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.4;
  }

  &__recommendation {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.6;
    white-space: pre-wrap;
  }

  &__reset {
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
