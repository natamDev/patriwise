<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { assistantApi, categoryLabel, type ChatMessage, type FinancialAnalysis, type GoalSummary, type Recommendation, type SavingsCoaching } from '@/api/assistant.api'

const messages = ref<ChatMessage[]>([
  {
    role: 'assistant',
    content:
      "Bonjour ! Je suis FinMate, ton assistant financier. Je suis là pour t'aider à comprendre l'investissement, les ETF, la gestion du risque et bien plus encore. Pose-moi ta question !",
  },
])
const input = ref('')
const loading = ref(false)
const conversationId = ref<string | null>(null)
const messagesEl = ref<HTMLElement | null>(null)
const inputEl = ref<HTMLTextAreaElement | null>(null)
const error = ref<string | null>(null)
const coaching = ref<Recommendation | null>(null)
const coachingLoading = ref(false)
const analysis = ref<FinancialAnalysis | null>(null)
const analysisLoading = ref(false)
const analysisError = ref<string | null>(null)
const savingsCoaching = ref<SavingsCoaching | null>(null)
const savingsLoading = ref(false)
const savingsError = ref<string | null>(null)

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

async function loadAnalysis() {
  analysisLoading.value = true
  analysisError.value = null
  try {
    analysis.value = await assistantApi.financialAnalysis()
  } catch {
    analysisError.value = 'Impossible de générer l\'analyse. Vérifie ta connexion et réessaie.'
  } finally {
    analysisLoading.value = false
  }
}

async function loadSavingsCoaching() {
  savingsLoading.value = true
  savingsError.value = null
  try {
    savingsCoaching.value = await assistantApi.savingsCoaching()
  } catch {
    savingsError.value = 'Impossible de générer le coaching. Vérifie ta connexion et réessaie.'
  } finally {
    savingsLoading.value = false
  }
}

function goalTypeLabel(type: string): string {
  const labels: Record<string, string> = {
    TRAVEL: 'Voyage',
    EMERGENCY_FUND: "Fonds d'urgence",
    INVESTMENT: 'Investissement',
    PURCHASE: 'Achat',
    OTHER: 'Autre',
  }
  return labels[type] ?? type
}

async function send() {
  const text = input.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', content: text })
  input.value = ''
  error.value = null
  loading.value = true
  await scrollToBottom()

  try {
    const res = await assistantApi.chat(conversationId.value, text)
    conversationId.value = res.conversationId
    messages.value.push({
      role: 'assistant',
      content: res.reply,
      conceptCard: res.conceptCard ?? undefined,
      fomoAlert: res.fomoAlert ?? undefined,
    })
  } catch {
    error.value = "Une erreur est survenue. Vérifie ta connexion et réessaie."
    messages.value.pop()
    input.value = text
  } finally {
    loading.value = false
    await scrollToBottom()
    inputEl.value?.focus()
  }
}

function onKeydown(e: KeyboardEvent) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    send()
  }
}

async function scrollToBottom() {
  await nextTick()
  if (messagesEl.value) {
    messagesEl.value.scrollTop = messagesEl.value.scrollHeight
  }
}
</script>

<template>
  <div class="page assistant">
    <h1 class="assistant__title">Assistant FinMate</h1>
    <p class="assistant__subtitle">Pose tes questions sur l'investissement et la finance personnelle.</p>

    <!-- Coaching personnalisé -->
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

    <!-- Analyse financière personnalisée -->
    <div class="analysis">
      <div v-if="!analysis" class="analysis__trigger">
        <p class="analysis__trigger-text">Obtenez une analyse complète de votre situation financière.</p>
        <p v-if="analysisError" class="analysis__error">{{ analysisError }}</p>
        <button
          class="analysis__btn"
          type="button"
          :disabled="analysisLoading"
          @click="loadAnalysis"
        >
          {{ analysisLoading ? 'Analyse en cours...' : '📊 Analyser ma situation' }}
        </button>
      </div>

      <div v-else class="analysis__card">
        <p class="analysis__title">📊 Analyse de ta situation financière</p>
        <p class="analysis__text">{{ analysis.analysis }}</p>

        <div v-if="analysis.spendingAlerts.length" class="analysis__alerts">
          <p class="analysis__alerts-label">⚠️ Alertes dépenses</p>
          <ul class="analysis__alerts-list">
            <li v-for="alert in analysis.spendingAlerts" :key="alert" class="analysis__alert-item">
              {{ alert }}
            </li>
          </ul>
        </div>

        <div v-if="Object.keys(analysis.expenseByCategory).length" class="analysis__breakdown">
          <p class="analysis__breakdown-label">Répartition des dépenses</p>
          <div
            v-for="(pct, cat) in analysis.expenseByCategory"
            :key="cat"
            class="analysis__breakdown-row"
          >
            <span class="analysis__breakdown-cat">{{ categoryLabel(String(cat)) }}</span>
            <div class="analysis__breakdown-bar">
              <div class="analysis__breakdown-fill" :style="{ width: Math.min(pct, 100) + '%' }" />
            </div>
            <span class="analysis__breakdown-pct">{{ pct }}%</span>
          </div>
        </div>

        <div class="analysis__saving">
          <span class="analysis__saving-label">Capacité d'épargne</span>
          <span class="analysis__saving-value">{{ analysis.savingCapacity.toFixed(0) }} €/mois</span>
        </div>

        <button class="analysis__refresh" type="button" @click="loadAnalysis">
          Actualiser
        </button>
      </div>
    </div>

    <!-- Coaching épargne -->
    <div class="savings-coaching">
      <div v-if="!savingsCoaching" class="savings-coaching__trigger">
        <p class="savings-coaching__trigger-text">Analysez vos objectifs d'épargne et recevez des conseils personnalisés.</p>
        <p v-if="savingsError" class="savings-coaching__error">{{ savingsError }}</p>
        <button
          class="savings-coaching__btn"
          type="button"
          :disabled="savingsLoading"
          @click="loadSavingsCoaching"
        >
          {{ savingsLoading ? 'Analyse en cours...' : '🎯 Mon coaching épargne' }}
        </button>
      </div>

      <div v-else class="savings-coaching__card">
        <p class="savings-coaching__title">🎯 Coaching épargne</p>
        <p class="savings-coaching__text">{{ savingsCoaching.coaching }}</p>

        <div v-if="savingsCoaching.goals.length" class="savings-coaching__goals">
          <div
            v-for="goal in savingsCoaching.goals"
            :key="goal.goalId"
            class="savings-coaching__goal"
          >
            <div class="savings-coaching__goal-header">
              <span class="savings-coaching__goal-name">{{ goal.goalName }}</span>
              <span class="savings-coaching__goal-type">{{ goalTypeLabel(goal.goalType) }}</span>
            </div>

            <div class="savings-coaching__goal-bar">
              <div
                class="savings-coaching__goal-fill"
                :style="{ width: goal.percent + '%' }"
              />
            </div>

            <div class="savings-coaching__goal-stats">
              <span>{{ goal.savedAmount.toFixed(0) }} € / {{ goal.targetAmount.toFixed(0) }} €</span>
              <span class="savings-coaching__goal-pct">{{ goal.percent }}%</span>
            </div>

            <div v-if="goal.monthsNeeded" class="savings-coaching__goal-estimate">
              {{ goal.monthsNeeded }} mois restants
              <template v-if="goal.estimatedCompletionDate">
                · {{ new Date(goal.estimatedCompletionDate).toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' }) }}
              </template>
            </div>

            <div v-if="goal.optimizedContribution" class="savings-coaching__goal-tip">
              💡 Pour atteindre ta date cible : <strong>{{ goal.optimizedContribution.toFixed(0) }} €/mois</strong>
              (actuel : {{ goal.monthlyContribution.toFixed(0) }} €/mois)
            </div>
          </div>
        </div>

        <button class="savings-coaching__refresh" type="button" @click="loadSavingsCoaching">
          Actualiser
        </button>
      </div>
    </div>

    <div class="assistant__chat" ref="messagesEl">
      <template v-for="(msg, i) in messages" :key="i">
        <div
          class="assistant__bubble"
          :class="msg.role === 'user' ? 'assistant__bubble--user' : 'assistant__bubble--bot'"
        >
          <p class="assistant__bubble-text">{{ msg.content }}</p>
        </div>

        <!-- Alerte FOMO -->
        <div v-if="msg.fomoAlert" class="fomo-alert">
          <p class="fomo-alert__title">🚨 Détection FOMO</p>
          <p class="fomo-alert__explanation">{{ msg.fomoAlert.explanation }}</p>
          <div class="fomo-alert__alternative">
            <p class="fomo-alert__alternative-label">Alternative recommandée</p>
            <p class="fomo-alert__alternative-text">{{ msg.fomoAlert.alternative }}</p>
          </div>
        </div>

        <!-- Carte éducative -->
        <div v-if="msg.conceptCard" class="concept-card">
          <p class="concept-card__name">📚 {{ msg.conceptCard.name }}</p>

          <div class="concept-card__section">
            <p class="concept-card__label">Définition</p>
            <p class="concept-card__text">{{ msg.conceptCard.definition }}</p>
          </div>

          <div class="concept-card__section">
            <p class="concept-card__label">Exemple concret</p>
            <div class="concept-card__example">
              <p class="concept-card__text">{{ msg.conceptCard.example }}</p>
            </div>
          </div>

          <div class="concept-card__section">
            <p class="concept-card__label">Risques</p>
            <div class="concept-card__risk">
              <p class="concept-card__text">{{ msg.conceptCard.risk }}</p>
            </div>
          </div>

          <div class="concept-card__summary">
            <p class="concept-card__text">💡 {{ msg.conceptCard.simpleSummary }}</p>
          </div>
        </div>
      </template>

      <div v-if="loading" class="assistant__bubble assistant__bubble--bot">
        <span class="assistant__typing">
          <span /><span /><span />
        </span>
      </div>
    </div>

    <div v-if="error" class="assistant__error">{{ error }}</div>

    <div class="assistant__input-row">
      <textarea
        ref="inputEl"
        v-model="input"
        class="assistant__input"
        placeholder="Envoie un message..."
        rows="1"
        :disabled="loading"
        @keydown="onKeydown"
      />
      <button
        class="assistant__send"
        type="button"
        :disabled="!input.trim() || loading"
        @click="send"
      >
        ➤
      </button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.assistant {
  display: flex;
  flex-direction: column;
  height: 100%;
  gap: $spacing-sm;

  &__title {
    font-size: $font-size-xl;
    font-weight: 700;
  }

  &__subtitle {
    font-size: $font-size-sm;
    color: $color-text-muted;
    margin-top: -$spacing-xs;
  }

  &__chat {
    flex: 1;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
    padding: $spacing-sm 0;
    min-height: 0;
  }

  &__bubble {
    max-width: 85%;
    padding: $spacing-sm $spacing-md;
    border-radius: $radius-lg;
    line-height: 1.5;

    &--bot {
      align-self: flex-start;
      background-color: $color-surface;
      border: 1px solid $color-border;
      border-bottom-left-radius: $radius-sm;
    }

    &--user {
      align-self: flex-end;
      background-color: $color-primary;
      border-bottom-right-radius: $radius-sm;
    }
  }

  &__bubble-text {
    font-size: $font-size-sm;
    color: $color-text;
    white-space: pre-wrap;
    word-break: break-word;

    .assistant__bubble--user & {
      color: #fff;
    }
  }

  &__typing {
    display: flex;
    gap: 4px;
    align-items: center;
    height: 18px;

    span {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background-color: $color-text-muted;
      animation: bounce 1.2s infinite ease-in-out;

      &:nth-child(2) { animation-delay: 0.2s; }
      &:nth-child(3) { animation-delay: 0.4s; }
    }
  }

  &__error {
    font-size: $font-size-sm;
    color: $color-danger;
    padding: $spacing-xs $spacing-sm;
    background-color: #fef2f2;
    border-radius: $radius-md;
    border: 1px solid #fca5a5;
  }

  &__input-row {
    display: flex;
    gap: $spacing-sm;
    align-items: flex-end;
  }

  &__input {
    flex: 1;
    padding: $spacing-sm $spacing-md;
    border: 1px solid $color-border;
    border-radius: $radius-lg;
    background-color: $color-surface;
    font-size: $font-size-sm;
    color: $color-text;
    resize: none;
    outline: none;
    font-family: inherit;
    line-height: 1.5;
    max-height: 120px;
    overflow-y: auto;

    &:focus {
      border-color: $color-primary;
    }

    &:disabled {
      opacity: 0.6;
    }
  }

  &__send {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background-color: $color-primary;
    border: none;
    color: #fff;
    font-size: 16px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    transition: opacity 0.15s;

    &:disabled {
      opacity: 0.4;
      cursor: default;
    }
  }
}

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

.analysis {
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
    border-left: 3px solid $color-primary;
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

  &__text {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.6;
    white-space: pre-wrap;
  }

  &__alerts {
    background-color: #fef2f2;
    border-radius: $radius-md;
    padding: $spacing-xs $spacing-sm;
  }

  &__alerts-label {
    font-size: 11px;
    font-weight: 600;
    color: $color-danger;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-bottom: 4px;
  }

  &__alerts-list {
    margin: 0;
    padding-left: $spacing-md;
  }

  &__alert-item {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.5;
  }

  &__breakdown {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  &__breakdown-label {
    font-size: 11px;
    font-weight: 600;
    color: $color-text-muted;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  &__breakdown-row {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
  }

  &__breakdown-cat {
    font-size: $font-size-sm;
    color: $color-text;
    width: 100px;
    flex-shrink: 0;
  }

  &__breakdown-bar {
    flex: 1;
    height: 6px;
    background-color: $color-border;
    border-radius: 3px;
    overflow: hidden;
  }

  &__breakdown-fill {
    height: 100%;
    background-color: $color-primary;
    border-radius: 3px;
    transition: width 0.3s ease;
  }

  &__breakdown-pct {
    font-size: 12px;
    color: $color-text-muted;
    width: 36px;
    text-align: right;
    flex-shrink: 0;
  }

  &__saving {
    background-color: #f0fdf4;
    border-radius: $radius-md;
    padding: $spacing-xs $spacing-sm;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  &__saving-label {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__saving-value {
    font-size: $font-size-sm;
    font-weight: 700;
    color: $color-secondary;
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

.savings-coaching {
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
    background-color: $color-surface;
    border: 1px solid $color-border;
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

  &__text {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.6;
    white-space: pre-wrap;
  }

  &__goals {
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__goal {
    background-color: #f8fafc;
    border: 1px solid $color-border;
    border-radius: $radius-md;
    padding: $spacing-sm;
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  &__goal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  &__goal-name {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-text;
  }

  &__goal-type {
    font-size: 11px;
    color: $color-text-muted;
    background-color: $color-border;
    border-radius: $radius-sm;
    padding: 2px 6px;
  }

  &__goal-bar {
    height: 6px;
    background-color: $color-border;
    border-radius: 3px;
    overflow: hidden;
  }

  &__goal-fill {
    height: 100%;
    background-color: $color-secondary;
    border-radius: 3px;
    transition: width 0.3s ease;
  }

  &__goal-stats {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: $color-text-muted;
  }

  &__goal-pct {
    font-weight: 600;
    color: $color-secondary;
  }

  &__goal-estimate {
    font-size: 12px;
    color: $color-text-muted;
  }

  &__goal-tip {
    font-size: $font-size-sm;
    color: $color-text;
    background-color: #f0fdf4;
    border-radius: $radius-sm;
    padding: 4px $spacing-sm;
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

.fomo-alert {
  align-self: flex-start;
  width: 100%;
  background-color: #fef2f2;
  border: 1px solid #fca5a5;
  border-left: 3px solid $color-danger;
  border-radius: $radius-lg;
  padding: $spacing-md;
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;

  &__title {
    font-size: $font-size-sm;
    font-weight: 700;
    color: $color-danger;
  }

  &__explanation {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.5;
  }

  &__alternative {
    background-color: #f0fdf4;
    border-radius: $radius-md;
    padding: $spacing-xs $spacing-sm;
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  &__alternative-label {
    font-size: 11px;
    font-weight: 600;
    color: $color-secondary;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  &__alternative-text {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.5;
  }
}

.concept-card {
  align-self: flex-start;
  width: 100%;
  background-color: $color-surface;
  border: 1px solid $color-border;
  border-left: 3px solid $color-primary;
  border-radius: $radius-lg;
  padding: $spacing-md;
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;

  &__name {
    font-size: $font-size-base;
    font-weight: 700;
    color: $color-text;
  }

  &__section {
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

  &__text {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.5;
  }

  &__example {
    background-color: #fef3c7;
    border-left: 3px solid #F59E0B;
    border-radius: 0 $radius-sm $radius-sm 0;
    padding: $spacing-xs $spacing-sm;
  }

  &__risk {
    background-color: #fef2f2;
    border-left: 3px solid $color-danger;
    border-radius: 0 $radius-sm $radius-sm 0;
    padding: $spacing-xs $spacing-sm;
  }

  &__summary {
    background-color: #f0fdf4;
    border-radius: $radius-md;
    padding: $spacing-xs $spacing-sm;
  }
}

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.8); opacity: 0.4; }
  40% { transform: scale(1.1); opacity: 1; }
}
</style>
