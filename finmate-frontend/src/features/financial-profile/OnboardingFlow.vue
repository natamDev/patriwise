<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { profileApi } from '@/api/profile.api'
import type { CreateFinancialProfilePayload } from '@/types/profile.types'
import ProfileForm from './ProfileForm.vue'

const router = useRouter()
const loading = ref(false)
const error = ref<string | null>(null)

async function handleSubmit(payload: CreateFinancialProfilePayload) {
  loading.value = true
  error.value = null
  try {
    await profileApi.create(payload)
    router.push('/')
  } catch {
    error.value = 'Une erreur est survenue. Veuillez réessayer.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="onboarding">
    <div class="onboarding__header">
      <h1 class="onboarding__title">Bienvenue sur FinMate</h1>
      <p class="onboarding__subtitle">
        Quelques informations pour personnaliser ton expérience financière.
      </p>
    </div>

    <div v-if="error" class="onboarding__error">{{ error }}</div>

    <ProfileForm :loading="loading" @submit="handleSubmit" />
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.onboarding {
  padding: $spacing-xl $spacing-lg;

  &__header {
    margin-bottom: $spacing-xl;
  }

  &__title {
    font-size: $font-size-xl;
    font-weight: 700;
    color: $color-text;
    margin-bottom: $spacing-sm;
  }

  &__subtitle {
    font-size: $font-size-base;
    color: $color-text-muted;
    line-height: 1.5;
  }

  &__error {
    padding: $spacing-sm $spacing-md;
    background-color: #fef2f2;
    color: $color-danger;
    border-radius: $radius-sm;
    font-size: $font-size-sm;
    margin-bottom: $spacing-lg;
  }
}
</style>
