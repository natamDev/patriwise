<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { profileApi } from '@/api/profile.api'
import type {
  EmploymentStatus,
  FinancialExperienceLevel,
  UpdateFinancialProfilePayload,
} from '@/types/profile.types'

const loading = ref(false)
const saving = ref(false)
const error = ref<string | null>(null)
const success = ref(false)

const form = reactive<UpdateFinancialProfilePayload>({
  monthlyIncome: undefined,
  employmentStatus: undefined,
  age: undefined,
  financialExperienceLevel: undefined,
  currency: undefined,
})

const employmentOptions: { value: EmploymentStatus; label: string }[] = [
  { value: 'STUDENT', label: 'Étudiant' },
  { value: 'EMPLOYEE', label: 'Salarié' },
  { value: 'FREELANCER', label: 'Freelance' },
  { value: 'UNEMPLOYED', label: 'Sans emploi' },
]

const experienceOptions: { value: FinancialExperienceLevel; label: string }[] = [
  { value: 'BEGINNER', label: 'Débutant' },
  { value: 'INTERMEDIATE', label: 'Intermédiaire' },
  { value: 'ADVANCED', label: 'Avancé' },
]

const currencyOptions = ['EUR', 'USD', 'GBP', 'CHF']

onMounted(async () => {
  loading.value = true
  try {
    const profile = await profileApi.get()
    form.monthlyIncome = profile.monthlyIncome
    form.employmentStatus = profile.employmentStatus
    form.age = profile.age
    form.financialExperienceLevel = profile.financialExperienceLevel
    form.currency = profile.currency
  } catch {
    error.value = 'Impossible de charger le profil.'
  } finally {
    loading.value = false
  }
})

async function handleSubmit() {
  saving.value = true
  error.value = null
  success.value = false
  try {
    await profileApi.update({ ...form })
    success.value = true
  } catch {
    error.value = 'Erreur lors de la mise à jour.'
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="profile-settings">
    <h2 class="profile-settings__title">Mon profil financier</h2>

    <div v-if="loading" class="profile-settings__loading">Chargement...</div>

    <template v-else>
      <div v-if="error" class="profile-settings__error">{{ error }}</div>
      <div v-if="success" class="profile-settings__success">Profil mis à jour.</div>

      <form class="profile-settings__form" @submit.prevent="handleSubmit">
        <div class="profile-settings__field">
          <label class="profile-settings__label" for="ps-income">Revenu mensuel net</label>
          <input
            id="ps-income"
            v-model.number="form.monthlyIncome"
            class="profile-settings__input"
            type="number"
            min="0"
            step="0.01"
          />
        </div>

        <div class="profile-settings__field">
          <label class="profile-settings__label" for="ps-status">Statut professionnel</label>
          <select id="ps-status" v-model="form.employmentStatus" class="profile-settings__select">
            <option v-for="opt in employmentOptions" :key="opt.value" :value="opt.value">
              {{ opt.label }}
            </option>
          </select>
        </div>

        <div class="profile-settings__field">
          <label class="profile-settings__label" for="ps-age">Âge</label>
          <input
            id="ps-age"
            v-model.number="form.age"
            class="profile-settings__input"
            type="number"
            min="16"
            max="120"
          />
        </div>

        <div class="profile-settings__field">
          <label class="profile-settings__label" for="ps-level">Niveau en finance</label>
          <select id="ps-level" v-model="form.financialExperienceLevel" class="profile-settings__select">
            <option v-for="opt in experienceOptions" :key="opt.value" :value="opt.value">
              {{ opt.label }}
            </option>
          </select>
        </div>

        <div class="profile-settings__field">
          <label class="profile-settings__label" for="ps-currency">Devise</label>
          <select id="ps-currency" v-model="form.currency" class="profile-settings__select">
            <option v-for="c in currencyOptions" :key="c" :value="c">{{ c }}</option>
          </select>
        </div>

        <button class="profile-settings__submit" type="submit" :disabled="saving">
          {{ saving ? 'Enregistrement...' : 'Mettre à jour' }}
        </button>
      </form>
    </template>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.profile-settings {
  padding: $spacing-xl $spacing-lg;

  &__title {
    font-size: $font-size-lg;
    font-weight: 700;
    margin-bottom: $spacing-xl;
  }

  &__loading {
    color: $color-text-muted;
    font-size: $font-size-sm;
  }

  &__error {
    padding: $spacing-sm $spacing-md;
    background-color: #fef2f2;
    color: $color-danger;
    border-radius: $radius-sm;
    font-size: $font-size-sm;
    margin-bottom: $spacing-lg;
  }

  &__success {
    padding: $spacing-sm $spacing-md;
    background-color: #f0fdf4;
    color: $color-success;
    border-radius: $radius-sm;
    font-size: $font-size-sm;
    margin-bottom: $spacing-lg;
  }

  &__form {
    display: flex;
    flex-direction: column;
    gap: $spacing-lg;
  }

  &__field {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }

  &__label {
    font-size: $font-size-sm;
    font-weight: 500;
    color: $color-text-muted;
  }

  &__input,
  &__select {
    padding: $spacing-sm $spacing-md;
    border: 1px solid $color-border;
    border-radius: $radius-sm;
    font-size: $font-size-base;
    color: $color-text;
    background-color: $color-surface;

    &:focus {
      outline: none;
      border-color: $color-primary;
    }
  }

  &__submit {
    margin-top: $spacing-sm;
    padding: $spacing-md;
    background-color: $color-primary;
    color: #fff;
    border: none;
    border-radius: $radius-md;
    font-size: $font-size-base;
    font-weight: 600;
    cursor: pointer;

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }
}
</style>
