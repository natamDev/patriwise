<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { goalsApi } from '@/api/goals.api'
import { streakApi } from '@/api/streak.api'
import type { Goal, GoalProgress, CreateGoalPayload } from '@/types/goal.types'
import type { SavingStreak } from '@/types/streak.types'
import GoalForm from '@/features/goals/GoalForm.vue'
import GoalList from '@/features/goals/GoalList.vue'
import StreakCard from '@/features/goals/StreakCard.vue'

const goals = ref<Goal[]>([])
const progress = ref<Record<string, GoalProgress>>({})
const streak = ref<SavingStreak | null>(null)
const loading = ref(false)
const adding = ref(false)
const deleting = ref<string | null>(null)
const contributing = ref<string | null>(null)
const error = ref<string | null>(null)
const showForm = ref(false)

async function loadGoals() {
  loading.value = true
  try {
    const [goalList, streakData] = await Promise.allSettled([
      goalsApi.list(),
      streakApi.getStreak(),
    ])
    if (goalList.status === 'fulfilled') goals.value = goalList.value
    if (streakData.status === 'fulfilled') streak.value = streakData.value
    await loadAllProgress()
  } catch {
    error.value = 'Impossible de charger les objectifs.'
  } finally {
    loading.value = false
  }
}

async function loadAllProgress() {
  const results = await Promise.allSettled(
    goals.value.map((g) => goalsApi.getProgress(g.id).then((p) => ({ id: g.id, p }))),
  )
  for (const r of results) {
    if (r.status === 'fulfilled') {
      progress.value[r.value.id] = r.value.p
    }
  }
}

async function handleAdd(payload: CreateGoalPayload) {
  adding.value = true
  error.value = null
  try {
    await goalsApi.create(payload)
    showForm.value = false
    await loadGoals()
  } catch (e: any) {
    error.value = e.response?.data?.error ?? "Erreur lors de la création."
  } finally {
    adding.value = false
  }
}

async function handleDelete(id: string) {
  deleting.value = id
  try {
    await goalsApi.delete(id)
    delete progress.value[id]
    await loadGoals()
  } catch {
    error.value = 'Erreur lors de la suppression.'
  } finally {
    deleting.value = null
  }
}

async function handleContribute(id: string, amount: number) {
  contributing.value = id
  error.value = null
  try {
    const [updated, streakData] = await Promise.all([
      goalsApi.addContribution(id, amount),
      streakApi.getStreak(),
    ])
    progress.value[id] = updated
    streak.value = streakData
  } catch (e: any) {
    error.value = e.response?.data?.error ?? 'Erreur lors de la contribution.'
  } finally {
    contributing.value = null
  }
}

onMounted(loadGoals)
</script>

<template>
  <div class="page goals">
    <h1 class="goals__title">Objectifs</h1>

    <StreakCard v-if="streak" :streak="streak" />

    <div v-if="error" class="goals__error">{{ error }}</div>

    <button
      v-if="!showForm"
      class="goals__add-btn"
      type="button"
      @click="showForm = true"
    >
      + Nouvel objectif
    </button>

    <GoalForm v-if="showForm" :loading="adding" @submit="handleAdd" @cancel="showForm = false" />

    <div v-if="loading" class="goals__loading">Chargement...</div>
    <GoalList
      v-else
      :goals="goals"
      :progress="progress"
      :deleting="deleting"
      :contributing="contributing"
      @delete="handleDelete"
      @contribute="handleContribute"
    />
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.goals {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  &__title {
    font-size: $font-size-xl;
    font-weight: 700;
  }

  &__error {
    padding: $spacing-sm $spacing-md;
    background-color: #fef2f2;
    color: $color-danger;
    border-radius: $radius-sm;
    font-size: $font-size-sm;
  }

  &__add-btn {
    padding: $spacing-sm $spacing-md;
    background-color: $color-primary;
    color: #fff;
    border: none;
    border-radius: $radius-md;
    font-size: $font-size-sm;
    font-weight: 600;
    cursor: pointer;
  }

  &__loading {
    color: $color-text-muted;
    font-size: $font-size-sm;
    text-align: center;
    padding: $spacing-lg 0;
  }
}
</style>
