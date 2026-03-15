<script setup lang="ts">
import { ref } from 'vue'
import ChatPanel from '@/features/ai-assistant/ChatPanel.vue'
import CoachingPanel from '@/features/ai-assistant/CoachingPanel.vue'
import FinancialAnalysisPanel from '@/features/ai-assistant/FinancialAnalysisPanel.vue'
import SavingsCoachingPanel from '@/features/ai-assistant/SavingsCoachingPanel.vue'
import MotivationPanel from '@/features/ai-assistant/MotivationPanel.vue'
import ProjectionPanel from '@/features/ai-assistant/ProjectionPanel.vue'
import SimulatorPanel from '@/features/ai-assistant/SimulatorPanel.vue'
import DecisionCoachingPanel from '@/features/ai-assistant/DecisionCoachingPanel.vue'

const activeTab = ref<'chat' | 'outils'>('chat')
const openSection = ref<string | null>(null)

function toggleSection(key: string) {
  openSection.value = openSection.value === key ? null : key
}
</script>

<template>
  <div class="page assistant">
    <h1 class="assistant__title">Assistant FinMate</h1>

    <!-- Tab switcher -->
    <div class="assistant__tabs">
      <button
        class="assistant__tab"
        :class="{ 'assistant__tab--active': activeTab === 'chat' }"
        type="button"
        @click="activeTab = 'chat'"
      >
        💬 Chat
      </button>
      <button
        class="assistant__tab"
        :class="{ 'assistant__tab--active': activeTab === 'outils' }"
        type="button"
        @click="activeTab = 'outils'"
      >
        🛠 Outils
      </button>
    </div>

    <!-- Tab Chat -->
    <div v-show="activeTab === 'chat'" class="assistant__tab-content assistant__tab-content--chat">
      <ChatPanel />
    </div>

    <!-- Tab Outils -->
    <div v-show="activeTab === 'outils'" class="assistant__tab-content assistant__tab-content--outils">

      <!-- Accordion: Mon profil financier -->
      <div class="accordion">
        <button class="accordion__header" type="button" @click="toggleSection('profil')">
          <span>📊 Mon profil financier</span>
          <span class="accordion__chevron" :class="{ 'accordion__chevron--open': openSection === 'profil' }">▾</span>
        </button>
        <div v-show="openSection === 'profil'" class="accordion__body">
          <CoachingPanel />
          <FinancialAnalysisPanel />
          <SavingsCoachingPanel />
          <MotivationPanel />
          <ProjectionPanel />
        </div>
      </div>

      <!-- Accordion: Simuler & Décider -->
      <div class="accordion">
        <button class="accordion__header" type="button" @click="toggleSection('simuler')">
          <span>🔢 Simuler & Décider</span>
          <span class="accordion__chevron" :class="{ 'accordion__chevron--open': openSection === 'simuler' }">▾</span>
        </button>
        <div v-show="openSection === 'simuler'" class="accordion__body">
          <SimulatorPanel />
          <DecisionCoachingPanel />
        </div>
      </div>

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

  &__tabs {
    display: flex;
    gap: 0;
    border-bottom: 2px solid $color-border;
    flex-shrink: 0;
  }

  &__tab {
    flex: 1;
    padding: $spacing-sm $spacing-md;
    background: none;
    border: none;
    border-bottom: 2px solid transparent;
    margin-bottom: -2px;
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-text-muted;
    cursor: pointer;
    transition: color 0.15s, border-color 0.15s;

    &--active {
      color: $color-primary;
      border-bottom-color: $color-primary;
    }
  }

  &__tab-content {
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
    flex: 1;
    min-height: 0;

    &--chat {
      overflow: hidden;
    }

    &--outils {
      overflow-y: auto;
    }
  }
}

.accordion {
  border: 1px solid $color-border;
  border-radius: $radius-lg;
  overflow: hidden;

  &__header {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: $spacing-sm $spacing-md;
    background-color: $color-surface;
    border: none;
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-text;
    cursor: pointer;
    text-align: left;
    transition: background-color 0.15s;

    &:hover {
      background-color: #f8fafc;
    }
  }

  &__chevron {
    font-size: 16px;
    color: $color-text-muted;
    transition: transform 0.2s;
    flex-shrink: 0;

    &--open {
      transform: rotate(180deg);
    }
  }

  &__body {
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
    padding: $spacing-sm $spacing-md $spacing-md;
    border-top: 1px solid $color-border;
  }
}
</style>
