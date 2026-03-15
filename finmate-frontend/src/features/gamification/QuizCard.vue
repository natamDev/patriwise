<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { quizApi } from '@/api/quiz.api'
import type { QuizQuestion, AnswerResult } from '@/types/quiz.types'

const questions = ref<QuizQuestion[]>([])
const currentIndex = ref(0)
const selectedOption = ref<number | null>(null)
const result = ref<AnswerResult | null>(null)
const loading = ref(false)
const finished = ref(false)
const correctCount = ref(0)

onMounted(async () => {
  questions.value = await quizApi.getAll()
})

const current = computed(() => questions.value[currentIndex.value])
const progress = computed(() =>
  questions.value.length ? (currentIndex.value / questions.value.length) * 100 : 0
)

async function select(index: number) {
  if (result.value !== null) return
  selectedOption.value = index
  loading.value = true
  try {
    result.value = await quizApi.answer(current.value.id, index)
    if (result.value.correct) correctCount.value++
  } finally {
    loading.value = false
  }
}

function next() {
  if (currentIndex.value < questions.value.length - 1) {
    currentIndex.value++
    selectedOption.value = null
    result.value = null
  } else {
    finished.value = true
  }
}

function restart() {
  currentIndex.value = 0
  selectedOption.value = null
  result.value = null
  finished.value = false
  correctCount.value = 0
}

function optionClass(index: number): string {
  if (result.value === null) return ''
  if (index === result.value.correctAnswer) return 'quiz__option--correct'
  if (index === selectedOption.value && !result.value.correct) return 'quiz__option--wrong'
  return 'quiz__option--muted'
}
</script>

<template>
  <div class="quiz">
    <!-- Résultat final -->
    <template v-if="finished">
      <div class="quiz__final">
        <p class="quiz__final-score">{{ correctCount }} / {{ questions.length }}</p>
        <p class="quiz__final-label">
          {{ correctCount === questions.length ? '🏆 Parfait !' : correctCount >= questions.length / 2 ? '👍 Bien joué !' : '📚 Continue à apprendre !' }}
        </p>
        <p class="quiz__final-sub">
          Tu as répondu correctement à {{ correctCount }} question{{ correctCount > 1 ? 's' : '' }} sur {{ questions.length }}.
        </p>
        <button class="quiz__btn" type="button" @click="restart">Recommencer</button>
      </div>
    </template>

    <!-- Quiz en cours -->
    <template v-else-if="current">
      <!-- Progression -->
      <div class="quiz__progress">
        <div class="quiz__progress-fill" :style="{ width: progress + '%' }" />
      </div>
      <p class="quiz__counter">Question {{ currentIndex + 1 }} / {{ questions.length }}</p>

      <p class="quiz__question">{{ current.question }}</p>

      <div class="quiz__options">
        <button
          v-for="(opt, i) in current.options"
          :key="i"
          class="quiz__option"
          :class="optionClass(i)"
          type="button"
          :disabled="result !== null"
          @click="select(i)"
        >
          {{ opt }}
        </button>
      </div>

      <!-- Explication -->
      <div v-if="result" class="quiz__explanation" :class="result.correct ? 'quiz__explanation--correct' : 'quiz__explanation--wrong'">
        <p class="quiz__explanation-icon">{{ result.correct ? '✅' : '❌' }}</p>
        <p class="quiz__explanation-text">{{ result.explanation }}</p>
      </div>

      <button v-if="result" class="quiz__btn" type="button" @click="next">
        {{ currentIndex < questions.length - 1 ? 'Question suivante →' : 'Voir mon score' }}
      </button>
    </template>

    <p v-else class="quiz__loading">Chargement des questions…</p>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.quiz {
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

  &__counter {
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

    &:hover:not(:disabled) {
      border-color: $color-primary;
      background-color: $color-surface;
    }

    &:disabled { cursor: default; }

    &--correct {
      border-color: $color-secondary;
      background-color: #f0fdf4;
      color: $color-text;
    }

    &--wrong {
      border-color: $color-danger;
      background-color: #fef2f2;
      color: $color-text;
    }

    &--muted {
      opacity: 0.5;
    }
  }

  &__explanation {
    border-radius: $radius-md;
    padding: $spacing-sm $spacing-md;
    display: flex;
    gap: $spacing-sm;
    align-items: flex-start;

    &--correct {
      background-color: #f0fdf4;
      border: 1px solid #86efac;
    }

    &--wrong {
      background-color: #fef2f2;
      border: 1px solid #fca5a5;
    }
  }

  &__explanation-icon {
    font-size: 16px;
    flex-shrink: 0;
  }

  &__explanation-text {
    font-size: $font-size-sm;
    color: $color-text;
    line-height: 1.5;
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
    align-self: flex-start;
  }

  &__final {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: $spacing-sm;
    padding: $spacing-md 0;
    text-align: center;
  }

  &__final-score {
    font-size: 48px;
    font-weight: 800;
    color: $color-primary;
    line-height: 1;
  }

  &__final-label {
    font-size: $font-size-xl;
    font-weight: 700;
    color: $color-text;
  }

  &__final-sub {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }

  &__loading {
    font-size: $font-size-sm;
    color: $color-text-muted;
  }
}
</style>
