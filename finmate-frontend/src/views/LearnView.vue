<script setup lang="ts">
import { ref } from 'vue'
import { lessons } from '@/features/learn/lessons.data'
import { biases } from '@/features/learn/biases.data'
import type { Lesson } from '@/features/learn/lessons.data'
import type { CognitiveBias } from '@/features/learn/biases.data'
import LessonViewer from '@/features/learn/LessonViewer.vue'
import BiasViewer from '@/features/learn/BiasViewer.vue'
import DecisionCoach from '@/features/learn/DecisionCoach.vue'
import PortfolioAllocationChart from '@/features/learn/PortfolioAllocationChart.vue'
import InvestmentPlanCalculator from '@/features/learn/InvestmentPlanCalculator.vue'
import CompoundInterestChart from '@/features/learn/CompoundInterestChart.vue'
import MarketCrashChart from '@/features/learn/MarketCrashChart.vue'
import DiversificationComparisonChart from '@/features/learn/DiversificationComparisonChart.vue'

const activeLesson = ref<Lesson | null>(null)
const activeBias = ref<CognitiveBias | null>(null)
const openSection = ref<'passive' | 'risk' | 'psychology' | null>('passive')

const passiveLessons = lessons.filter((l) => l.category !== 'Gestion du risque')
const riskLessons = lessons.filter((l) => l.category === 'Gestion du risque')

function toggle(section: 'passive' | 'risk' | 'psychology') {
  openSection.value = openSection.value === section ? null : section
}
</script>

<template>
  <div class="page learn">
    <template v-if="activeLesson">
      <LessonViewer :lesson="activeLesson" @close="activeLesson = null" />
    </template>

    <template v-else-if="activeBias">
      <BiasViewer :bias="activeBias" @close="activeBias = null" />
    </template>

    <template v-else-if="!activeLesson && !activeBias">
      <h1 class="learn__title">Apprendre</h1>

      <!-- Section 1 — Investissement passif -->
      <div class="learn__section">
        <button class="learn__section-header" type="button" @click="toggle('passive')">
          <div class="learn__section-info">
            <span class="learn__section-icon">📈</span>
            <div>
              <p class="learn__section-title">Investissement passif</p>
              <p class="learn__section-subtitle">ETF, DCA, intérêts composés</p>
            </div>
          </div>
          <span class="learn__chevron">{{ openSection === 'passive' ? '▲' : '▼' }}</span>
        </button>

        <div v-if="openSection === 'passive'" class="learn__section-body">
          <!-- Leçons -->
          <p class="learn__group-label">Leçons</p>
          <button
            v-for="lesson in passiveLessons"
            :key="lesson.id"
            class="learn__lesson-card"
            type="button"
            @click="activeLesson = lesson"
          >
            <div class="learn__lesson-info">
              <span class="learn__lesson-title">{{ lesson.title }}</span>
              <span class="learn__lesson-category">{{ lesson.category }}</span>
            </div>
            <span class="learn__lesson-arrow">→</span>
          </button>

          <!-- Portefeuille exemple -->
          <p class="learn__group-label">Portefeuille exemple</p>
          <div class="learn__tool-card">
            <p class="learn__tool-desc">
              Un portefeuille simple suffit souvent à battre 90% des investisseurs actifs.
              Appuie sur un segment pour comprendre chaque allocation.
            </p>
            <PortfolioAllocationChart />
          </div>

          <!-- Plan DCA -->
          <p class="learn__group-label">Plan d'investissement mensuel (DCA)</p>
          <div class="learn__tool-card">
            <p class="learn__tool-desc">
              Investir un montant fixe chaque mois, quelles que soient les conditions de marché.
              Ajuste les curseurs pour voir l'impact sur le long terme.
            </p>
            <InvestmentPlanCalculator />
          </div>

          <!-- Intérêts composés -->
          <p class="learn__group-label">Simulateur d'intérêts composés</p>
          <div class="learn__tool-card">
            <p class="learn__tool-desc">
              Chaque année, tes gains génèrent eux-mêmes des gains.
              Visualise l'effet exponentiel du temps sur ton capital.
            </p>
            <CompoundInterestChart />
          </div>
        </div>
      </div>

      <!-- Section 2 — Gestion du risque -->
      <div class="learn__section">
        <button class="learn__section-header" type="button" @click="toggle('risk')">
          <div class="learn__section-info">
            <span class="learn__section-icon">🛡️</span>
            <div>
              <p class="learn__section-title">Gestion du risque</p>
              <p class="learn__section-subtitle">Volatilité, crises, diversification</p>
            </div>
          </div>
          <span class="learn__chevron">{{ openSection === 'risk' ? '▲' : '▼' }}</span>
        </button>

        <div v-if="openSection === 'risk'" class="learn__section-body">
          <!-- Leçons -->
          <p class="learn__group-label">Leçons</p>
          <button
            v-for="lesson in riskLessons"
            :key="lesson.id"
            class="learn__lesson-card"
            type="button"
            @click="activeLesson = lesson"
          >
            <div class="learn__lesson-info">
              <span class="learn__lesson-title">{{ lesson.title }}</span>
              <span class="learn__lesson-category">{{ lesson.category }}</span>
            </div>
            <span class="learn__lesson-arrow">→</span>
          </button>

          <!-- Simulateur de crise -->
          <p class="learn__group-label">Simulateur de crise financière</p>
          <div class="learn__tool-card">
            <p class="learn__tool-desc">
              Simule une crise boursière et observe comment le marché se récupère sur le long terme.
            </p>
            <MarketCrashChart />
          </div>

          <!-- Diversification -->
          <p class="learn__group-label">Diversification vs concentration</p>
          <div class="learn__tool-card">
            <p class="learn__tool-desc">
              Mono-actif ou ETF monde — lequel résiste mieux sur 10 ans ?
            </p>
            <DiversificationComparisonChart />
          </div>
        </div>
      </div>
      <!-- Section 3 — Psychologie & biais -->
      <div class="learn__section">
        <button class="learn__section-header" type="button" @click="toggle('psychology')">
          <div class="learn__section-info">
            <span class="learn__section-icon">🧠</span>
            <div>
              <p class="learn__section-title">Psychologie & biais cognitifs</p>
              <p class="learn__section-subtitle">FOMO, biais de confirmation, excès de confiance, aversion aux pertes</p>
            </div>
          </div>
          <span class="learn__chevron">{{ openSection === 'psychology' ? '▲' : '▼' }}</span>
        </button>

        <div v-if="openSection === 'psychology'" class="learn__section-body">
          <button
            v-for="bias in biases"
            :key="bias.id"
            class="learn__lesson-card"
            type="button"
            @click="activeBias = bias"
          >
            <div class="learn__lesson-info">
              <span class="learn__lesson-title">{{ bias.icon }} {{ bias.name }}</span>
              <span class="learn__lesson-category">Biais cognitif</span>
            </div>
            <span class="learn__lesson-arrow">→</span>
          </button>

          <p class="learn__group-label">Coaching décision</p>
          <div class="learn__tool-card">
            <p class="learn__tool-desc">
              Avant de prendre une décision financière importante, réponds à ces 4 questions.
              L'outil détecte les biais et te donne une recommandation personnalisée.
            </p>
            <DecisionCoach />
          </div>
        </div>
      </div>

    </template>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.learn {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  &__title {
    font-size: $font-size-xl;
    font-weight: 700;
  }

  &__section {
    background-color: $color-surface;
    border: 1px solid $color-border;
    border-radius: $radius-lg;
    overflow: hidden;
  }

  &__section-header {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $spacing-md $spacing-lg;
    background: none;
    border: none;
    cursor: pointer;
    text-align: left;
  }

  &__section-info {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
  }

  &__section-icon {
    font-size: 24px;
  }

  &__section-title {
    font-size: $font-size-base;
    font-weight: 700;
    color: $color-text;
  }

  &__section-subtitle {
    font-size: $font-size-sm;
    color: $color-text-muted;
    margin-top: 2px;
  }

  &__chevron {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__section-body {
    border-top: 1px solid $color-border;
    padding: $spacing-md $spacing-lg;
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__group-label {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-text-muted;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-top: $spacing-xs;
  }

  &__lesson-card {
    display: flex;
    align-items: center;
    width: 100%;
    padding: $spacing-sm $spacing-md;
    background-color: $color-background;
    border: 1px solid $color-border;
    border-radius: $radius-md;
    cursor: pointer;
    text-align: left;
  }

  &__lesson-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  &__lesson-title {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-text;
  }

  &__lesson-category {
    font-size: 12px;
    color: $color-text-muted;
  }

  &__lesson-arrow {
    color: $color-primary;
  }

  &__tool-card {
    background-color: $color-background;
    border: 1px solid $color-border;
    border-radius: $radius-md;
    padding: $spacing-md;
    display: flex;
    flex-direction: column;
    gap: $spacing-md;
  }

  &__tool-desc {
    font-size: $font-size-sm;
    color: $color-text-muted;
    line-height: 1.5;
  }
}
</style>
