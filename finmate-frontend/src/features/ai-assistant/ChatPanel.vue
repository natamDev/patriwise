<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { assistantApi } from '@/api/assistant.api'
import type { ChatMessage } from '@/types/assistant.types'

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
      biasAlert: res.biasAlert ?? undefined,
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
  <div class="chat-panel">
    <div class="chat-panel__messages" ref="messagesEl">
      <template v-for="(msg, i) in messages" :key="i">
        <div
          class="chat-panel__bubble"
          :class="msg.role === 'user' ? 'chat-panel__bubble--user' : 'chat-panel__bubble--bot'"
        >
          <p class="chat-panel__bubble-text">{{ msg.content }}</p>
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

        <!-- Alerte biais comportemental -->
        <div v-if="msg.biasAlert" class="bias-alert">
          <p class="bias-alert__title">🧠 {{ msg.biasAlert.biasLabel }} détecté</p>
          <p class="bias-alert__explanation">{{ msg.biasAlert.explanation }}</p>
          <div class="bias-alert__alternative">
            <p class="bias-alert__alternative-label">Approche rationnelle</p>
            <p class="bias-alert__alternative-text">{{ msg.biasAlert.alternative }}</p>
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

      <div v-if="loading" class="chat-panel__bubble chat-panel__bubble--bot">
        <span class="chat-panel__typing">
          <span /><span /><span />
        </span>
      </div>
    </div>

    <div v-if="error" class="chat-panel__error">{{ error }}</div>

    <div class="chat-panel__input-row">
      <textarea
        ref="inputEl"
        v-model="input"
        class="chat-panel__input"
        placeholder="Envoie un message..."
        rows="1"
        :disabled="loading"
        @keydown="onKeydown"
      />
      <button
        class="chat-panel__send"
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

.chat-panel {
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;
  flex: 1;
  min-height: 0;
  overflow: hidden;

  &__messages {
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

    .chat-panel__bubble--user & {
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
    flex-shrink: 0;
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

.bias-alert {
  align-self: flex-start;
  width: 100%;
  background-color: #f5f3ff;
  border: 1px solid #c4b5fd;
  border-left: 3px solid #7c3aed;
  border-radius: $radius-lg;
  padding: $spacing-md;
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;

  &__title {
    font-size: $font-size-sm;
    font-weight: 700;
    color: #7c3aed;
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
