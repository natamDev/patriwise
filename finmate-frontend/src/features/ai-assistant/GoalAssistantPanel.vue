<script setup lang="ts">
import { ref, computed } from 'vue'
import { assistantApi } from '@/api/assistant.api'
import { GOAL_TYPE_LABELS } from '@/types/goal.types'
import type { GoalType } from '@/types/goal.types'
import type { GoalAssistantResponse } from '@/types/assistant.types'

const emit = defineEmits<{ 'goal-created': [goalId: string]; cancel: [] }>()

type Step = 'input' | 'questions' | 'proposal' | 'editing' | 'success'

const step = ref<Step>('input')
const loading = ref(false)
const error = ref<string | null>(null)
const sessionId = ref<string | null>(null)

const userIntent = ref('')
const questions = ref<string[]>([])
const userAnswers = ref('')

const proposal = ref<{
  goalName: string
  goalType: string
  targetAmount: number
  targetDate: string
  monthlyContribution: number
  feasibilityAssessment: string
  feasibilityPercent: number
} | null>(null)

const editForm = ref({
  goalName: '',
  goalType: 'OTHER' as GoalType,
  targetAmount: '',
  targetDate: '',
  monthlyContribution: '',
})

const createdGoalId = ref<string | null>(null)

const goalTypes = Object.entries(GOAL_TYPE_LABELS) as [GoalType, string][]

const feasibilityColor = computed(() => {
  if (!proposal.value) return '#94a3b8'
  const pct = proposal.value.feasibilityPercent
  if (pct >= 80) return '#22c55e'
  if (pct >= 50) return '#f59e0b'
  return '#ef4444'
})

async function analyzeIntent() {
  if (!userIntent.value.trim()) return
  loading.value = true
  error.value = null
  try {
    const res = await assistantApi.goalAssistant({ step: 'INTENT', userIntent: userIntent.value })
    sessionId.value = res.sessionId
    questions.value = res.clarificationQuestions ?? []
    step.value = 'questions'
  } catch (e: any) {
    error.value = e.response?.data?.error ?? "Impossible d'analyser votre projet. Réessayez."
  } finally {
    loading.value = false
  }
}

async function sendAnswers() {
  if (!userAnswers.value.trim() || !sessionId.value) return
  loading.value = true
  error.value = null
  try {
    const res = await assistantApi.goalAssistant({
      step: 'CLARIFY_RESPONSE',
      sessionId: sessionId.value,
      userAnswers: userAnswers.value,
    })
    proposal.value = {
      goalName: res.goalName ?? '',
      goalType: res.goalType ?? 'OTHER',
      targetAmount: res.targetAmount ?? 0,
      targetDate: res.targetDate ?? '',
      monthlyContribution: res.monthlyContribution ?? 0,
      feasibilityAssessment: res.feasibilityAssessment ?? '',
      feasibilityPercent: res.feasibilityPercent ?? 50,
    }
    step.value = 'proposal'
  } catch (e: any) {
    error.value = e.response?.data?.error ?? 'Impossible de générer la proposition. Réessayez.'
  } finally {
    loading.value = false
  }
}

async function confirmGoal() {
  if (!proposal.value || !sessionId.value) return
  loading.value = true
  error.value = null
  try {
    const res = await assistantApi.goalAssistant({
      step: 'CONFIRM',
      sessionId: sessionId.value,
      goalName: proposal.value.goalName,
      goalType: proposal.value.goalType,
      targetAmount: proposal.value.targetAmount,
      targetDate: proposal.value.targetDate,
      monthlyContribution: proposal.value.monthlyContribution,
    })
    createdGoalId.value = res.createdGoalId ?? null
    step.value = 'success'
    if (res.createdGoalId) emit('goal-created', res.createdGoalId)
  } catch (e: any) {
    error.value = e.response?.data?.error ?? "Erreur lors de la création de l'objectif."
  } finally {
    loading.value = false
  }
}

function startEditing() {
  if (!proposal.value) return
  editForm.value = {
    goalName: proposal.value.goalName,
    goalType: proposal.value.goalType as GoalType,
    targetAmount: proposal.value.targetAmount.toString(),
    targetDate: proposal.value.targetDate,
    monthlyContribution: proposal.value.monthlyContribution.toString(),
  }
  step.value = 'editing'
}

function applyEdits() {
  if (!proposal.value) return
  proposal.value = {
    ...proposal.value,
    goalName: editForm.value.goalName,
    goalType: editForm.value.goalType,
    targetAmount: parseFloat(editForm.value.targetAmount),
    targetDate: editForm.value.targetDate,
    monthlyContribution: parseFloat(editForm.value.monthlyContribution),
  }
  step.value = 'proposal'
}

function resetAll() {
  step.value = 'input'
  userIntent.value = ''
  questions.value = []
  userAnswers.value = ''
  proposal.value = null
  sessionId.value = null
  createdGoalId.value = null
  error.value = null
}
</script>

<template>
  <div class="goal-assistant">
    <!-- Step: Input -->
    <div v-if="step === 'input'" class="goal-assistant__card">
      <p class="goal-assistant__title">🎯 Assistant création d'objectif</p>
      <p class="goal-assistant__subtitle">Décrivez votre projet en quelques mots, l'IA vous guidera.</p>

      <div class="goal-assistant__field">
        <label class="goal-assistant__label">Votre projet</label>
        <textarea
          v-model="userIntent"
          class="goal-assistant__textarea"
          placeholder="Ex : Je veux partir au Japon l'été prochain..."
          rows="3"
        />
      </div>

      <p v-if="error" class="goal-assistant__error">{{ error }}</p>

      <button
        class="goal-assistant__btn"
        type="button"
        :disabled="loading || !userIntent.trim()"
        @click="analyzeIntent"
      >
        {{ loading ? 'Analyse en cours...' : '🔍 Analyser mon projet' }}
      </button>
    </div>

    <!-- Step: Questions -->
    <div v-if="step === 'questions'" class="goal-assistant__card">
      <p class="goal-assistant__title">🤔 Questions de clarification</p>
      <div class="goal-assistant__questions">
        <p v-for="(q, i) in questions" :key="i" class="goal-assistant__question">{{ q }}</p>
      </div>

      <div class="goal-assistant__field">
        <label class="goal-assistant__label">Vos réponses</label>
        <textarea
          v-model="userAnswers"
          class="goal-assistant__textarea"
          placeholder="Répondez aux questions ci-dessus..."
          rows="4"
        />
      </div>

      <p v-if="error" class="goal-assistant__error">{{ error }}</p>

      <button
        class="goal-assistant__btn"
        type="button"
        :disabled="loading || !userAnswers.trim()"
        @click="sendAnswers"
      >
        {{ loading ? 'Génération de la proposition...' : '📤 Envoyer' }}
      </button>
    </div>

    <!-- Step: Proposal -->
    <div v-if="step === 'proposal' && proposal" class="goal-assistant__card">
      <p class="goal-assistant__title">📋 Proposition d'objectif</p>

      <div class="goal-assistant__proposal">
        <div class="goal-assistant__row">
          <span class="goal-assistant__row-label">Nom</span>
          <span class="goal-assistant__row-value">{{ proposal.goalName }}</span>
        </div>
        <div class="goal-assistant__row">
          <span class="goal-assistant__row-label">Type</span>
          <span class="goal-assistant__row-value">{{ (GOAL_TYPE_LABELS as Record<string, string>)[proposal.goalType] ?? proposal.goalType }}</span>
        </div>
        <div class="goal-assistant__row">
          <span class="goal-assistant__row-label">Montant cible</span>
          <span class="goal-assistant__row-value">{{ proposal.targetAmount.toLocaleString('fr-FR') }} €</span>
        </div>
        <div class="goal-assistant__row">
          <span class="goal-assistant__row-label">Date cible</span>
          <span class="goal-assistant__row-value">{{ proposal.targetDate }}</span>
        </div>
        <div class="goal-assistant__row">
          <span class="goal-assistant__row-label">Contribution mensuelle</span>
          <span class="goal-assistant__row-value">{{ proposal.monthlyContribution.toLocaleString('fr-FR') }} €/mois</span>
        </div>
      </div>

      <div class="goal-assistant__feasibility">
        <p class="goal-assistant__feasibility-label">
          Faisabilité : {{ proposal.feasibilityPercent }}%
        </p>
        <div class="goal-assistant__feasibility-bar">
          <div
            class="goal-assistant__feasibility-fill"
            :style="{ width: proposal.feasibilityPercent + '%', backgroundColor: feasibilityColor }"
          />
        </div>
        <p class="goal-assistant__feasibility-text">{{ proposal.feasibilityAssessment }}</p>
      </div>

      <p v-if="error" class="goal-assistant__error">{{ error }}</p>

      <div class="goal-assistant__actions">
        <button class="goal-assistant__btn goal-assistant__btn--secondary" type="button" @click="emit('cancel')">
          Annuler
        </button>
        <button class="goal-assistant__btn goal-assistant__btn--secondary" type="button" @click="startEditing">
          Modifier
        </button>
        <button class="goal-assistant__btn" type="button" :disabled="loading" @click="confirmGoal">
          {{ loading ? 'Création...' : 'Confirmer' }}
        </button>
      </div>
    </div>

    <!-- Step: Editing -->
    <div v-if="step === 'editing'" class="goal-assistant__card">
      <p class="goal-assistant__title">✏️ Modifier la proposition</p>

      <div class="goal-assistant__field">
        <label class="goal-assistant__label">Nom de l'objectif</label>
        <input v-model="editForm.goalName" class="goal-assistant__input" type="text" required />
      </div>
      <div class="goal-assistant__field">
        <label class="goal-assistant__label">Type</label>
        <select v-model="editForm.goalType" class="goal-assistant__input">
          <option v-for="[value, label] in goalTypes" :key="value" :value="value">{{ label }}</option>
        </select>
      </div>
      <div class="goal-assistant__field">
        <label class="goal-assistant__label">Montant cible (€)</label>
        <input v-model="editForm.targetAmount" class="goal-assistant__input" type="number" min="0.01" step="0.01" />
      </div>
      <div class="goal-assistant__field">
        <label class="goal-assistant__label">Date cible</label>
        <input v-model="editForm.targetDate" class="goal-assistant__input" type="date" />
      </div>
      <div class="goal-assistant__field">
        <label class="goal-assistant__label">Contribution mensuelle (€)</label>
        <input v-model="editForm.monthlyContribution" class="goal-assistant__input" type="number" min="0.01" step="0.01" />
      </div>

      <div class="goal-assistant__actions">
        <button class="goal-assistant__btn goal-assistant__btn--secondary" type="button" @click="step = 'proposal'">
          Retour
        </button>
        <button class="goal-assistant__btn" type="button" @click="applyEdits">
          Appliquer
        </button>
      </div>
    </div>

    <!-- Step: Success -->
    <div v-if="step === 'success'" class="goal-assistant__card goal-assistant__card--success">
      <p class="goal-assistant__title">🎉 Objectif créé !</p>
      <p class="goal-assistant__subtitle">Votre objectif a été ajouté avec succès. Bonne épargne !</p>
      <button class="goal-assistant__btn" type="button" @click="resetAll">
        Créer un autre objectif
      </button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.goal-assistant {
  &__card {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-left: 3px solid #7c3aed;
    border-radius: $radius-lg;
    padding: $spacing-md;
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;

    &--success {
      border-left-color: #22c55e;
    }
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

  &__textarea,
  &__input {
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

  &__input {
    resize: initial;
  }

  &__questions {
    background-color: #f5f3ff;
    border-radius: $radius-md;
    padding: $spacing-sm;
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__question {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.5;
  }

  &__proposal {
    background-color: #f5f3ff;
    border-radius: $radius-md;
    padding: $spacing-sm;
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  &__row {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  &__row-label {
    font-size: $font-size-sm;
    color: $color-text-muted;
    font-weight: 500;
  }

  &__row-value {
    font-size: $font-size-sm;
    color: $color-text;
    font-weight: 600;
  }

  &__feasibility {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__feasibility-label {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-text;
  }

  &__feasibility-bar {
    height: 8px;
    background-color: #e2e8f0;
    border-radius: 4px;
    overflow: hidden;
  }

  &__feasibility-fill {
    height: 100%;
    border-radius: 4px;
    transition: width 0.3s ease;
  }

  &__feasibility-text {
    font-size: $font-size-sm;
    color: $color-text-muted;
    line-height: 1.5;
  }

  &__error {
    font-size: $font-size-sm;
    color: $color-danger;
  }

  &__actions {
    display: flex;
    gap: $spacing-sm;
  }

  &__btn {
    flex: 1;
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

    &--secondary {
      background: none;
      border: 1px solid $color-border;
      color: $color-text-muted;
    }
  }
}
</style>
