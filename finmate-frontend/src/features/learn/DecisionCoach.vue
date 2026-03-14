<script setup lang="ts">
import { computed, ref } from 'vue'

interface Answer {
  whyInvesting: string
  horizon: string
  riskTolerance: string
  financialGoal: string
}

const step = ref(0)
const answers = ref<Partial<Answer>>({})
const done = ref(false)

const questions = [
  {
    key: 'whyInvesting' as keyof Answer,
    question: 'Pourquoi veux-tu investir maintenant ?',
    options: [
      { value: 'hype', label: "Tout le monde en parle, je ne veux pas rater ça", warning: true },
      { value: 'growth', label: "Faire fructifier mon épargne sur le long terme", warning: false },
      { value: 'goal', label: "Financer un projet précis (maison, retraite...)", warning: false },
      { value: 'quick', label: "Gagner de l'argent rapidement", warning: true },
    ],
  },
  {
    key: 'horizon' as keyof Answer,
    question: "Sur quel horizon de temps comptes-tu investir ?",
    options: [
      { value: 'less1', label: "Moins d'1 an", warning: true },
      { value: '1to3', label: "1 à 3 ans", warning: true },
      { value: '3to10', label: "3 à 10 ans", warning: false },
      { value: 'more10', label: "Plus de 10 ans", warning: false },
    ],
  },
  {
    key: 'riskTolerance' as keyof Answer,
    question: "Si ton investissement perd 20% en 1 mois, tu fais quoi ?",
    options: [
      { value: 'sell_all', label: "Je vends tout immédiatement", warning: true },
      { value: 'sell_part', label: "Je vends une partie pour limiter les pertes", warning: true },
      { value: 'hold', label: "Je garde et j'attends que ça remonte", warning: false },
      { value: 'buy_more', label: "J'en rachète, c'est une opportunité", warning: false },
    ],
  },
  {
    key: 'financialGoal' as keyof Answer,
    question: "Quel est ton objectif financier principal ?",
    options: [
      { value: 'retire', label: "Préparer ma retraite", warning: false },
      { value: 'emergency', label: "Constituer une épargne de sécurité", warning: false },
      { value: 'speculate', label: "Spéculer sur des opportunités", warning: true },
      { value: 'project', label: "Financer un projet de vie", warning: false },
    ],
  },
]

function select(value: string) {
  const q = questions[step.value]
  answers.value[q.key] = value
  if (step.value < questions.length - 1) {
    step.value++
  } else {
    done.value = true
  }
}

const warningCount = computed(() => {
  let count = 0
  for (const q of questions) {
    const answer = answers.value[q.key]
    const opt = q.options.find((o) => o.value === answer)
    if (opt?.warning) count++
  }
  return count
})

const recommendation = computed(() => {
  if (warningCount.value >= 3) {
    return {
      level: 'danger',
      title: 'Attention — décision à risque élevé',
      message:
        "Tes réponses indiquent plusieurs signaux d'alerte : horizon court, motivation émotionnelle, faible tolérance au risque. Attends 48h avant de prendre toute décision. Relis la leçon sur le FOMO et les biais cognitifs.",
      advice: [
        "Attends au moins 48h avant d'agir",
        "Investis uniquement ce que tu peux te permettre de perdre",
        "Commence par un ETF diversifié plutôt qu'un actif unique",
        "Mets en place un investissement automatique mensuel",
      ],
    }
  }
  if (warningCount.value >= 1) {
    return {
      level: 'warning',
      title: 'Quelques points à vérifier',
      message:
        "Ta démarche est globalement solide, mais quelques réponses méritent réflexion. Assure-toi que ton horizon de temps et ta tolérance au risque sont bien alignés avec ton choix d'investissement.",
      advice: [
        "Vérifie que tu as une épargne de sécurité avant d'investir",
        "Privilégie les ETF diversifiés pour commencer",
        "Investis régulièrement plutôt qu'en une seule fois",
      ],
    }
  }
  return {
    level: 'success',
    title: 'Bonne approche !',
    message:
      "Tes réponses montrent une démarche rationnelle et long terme. Tu sembles prêt à investir de façon disciplinée. Continue sur cette voie.",
    advice: [
      "Mets en place un virement automatique mensuel",
      "Diversifie avec un ETF Monde comme base de ton portefeuille",
      "Ne regarde pas ton portefeuille tous les jours",
    ],
  }
})

function restart() {
  step.value = 0
  answers.value = {}
  done.value = false
}
</script>

<template>
  <div class="coach">
    <!-- Résultat -->
    <template v-if="done">
      <div class="coach__result" :class="`coach__result--${recommendation.level}`">
        <p class="coach__result-title">{{ recommendation.title }}</p>
        <p class="coach__result-message">{{ recommendation.message }}</p>
      </div>

      <div class="coach__advice">
        <p class="coach__advice-label">Conseils</p>
        <ul class="coach__advice-list">
          <li v-for="a in recommendation.advice" :key="a">{{ a }}</li>
        </ul>
      </div>

      <button class="coach__restart" type="button" @click="restart">
        Recommencer
      </button>
    </template>

    <!-- Questionnaire -->
    <template v-else>
      <div class="coach__progress">
        <div
          class="coach__progress-fill"
          :style="{ width: ((step / questions.length) * 100) + '%' }"
        />
      </div>
      <p class="coach__step-label">Question {{ step + 1 }} / {{ questions.length }}</p>

      <p class="coach__question">{{ questions[step].question }}</p>

      <div class="coach__options">
        <button
          v-for="opt in questions[step].options"
          :key="opt.value"
          class="coach__option"
          type="button"
          @click="select(opt.value)"
        >
          {{ opt.label }}
        </button>
      </div>
    </template>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.coach {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  &__progress {
    height: 4px;
    background-color: $color-border;
    border-radius: 2px;
    overflow: hidden;
  }

  &__progress-fill {
    height: 100%;
    background-color: $color-primary;
    border-radius: 2px;
    transition: width 0.3s ease;
  }

  &__step-label {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__question {
    font-size: $font-size-base;
    font-weight: 600;
    color: $color-text;
    line-height: 1.4;
  }

  &__options {
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__option {
    padding: $spacing-sm $spacing-md;
    background-color: $color-background;
    border: 1px solid $color-border;
    border-radius: $radius-md;
    font-size: $font-size-sm;
    color: $color-text;
    text-align: left;
    cursor: pointer;
    line-height: 1.4;
    transition: border-color 0.15s, background-color 0.15s;

    &:hover {
      border-color: $color-primary;
      background-color: $color-surface;
    }
  }

  &__result {
    border-radius: $radius-lg;
    padding: $spacing-md $spacing-lg;
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;

    &--success {
      background-color: #f0fdf4;
      border: 1px solid #86efac;
    }
    &--warning {
      background-color: #fefce8;
      border: 1px solid #fde047;
    }
    &--danger {
      background-color: #fef2f2;
      border: 1px solid #fca5a5;
    }
  }

  &__result-title {
    font-size: $font-size-base;
    font-weight: 700;
    color: $color-text;
  }

  &__result-message {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.5;
  }

  &__advice {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }

  &__advice-label {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $color-text-muted;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  &__advice-list {
    list-style: none;
    padding: 0;
    display: flex;
    flex-direction: column;
    gap: 6px;

    li {
      font-size: $font-size-sm;
      color: $color-text;
      line-height: 1.5;
      padding-left: $spacing-md;
      position: relative;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 7px;
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background-color: $color-secondary;
      }
    }
  }

  &__restart {
    padding: $spacing-sm $spacing-md;
    background: none;
    border: 1px solid $color-border;
    border-radius: $radius-md;
    font-size: $font-size-sm;
    color: $color-text-muted;
    cursor: pointer;
    align-self: center;
  }
}
</style>
