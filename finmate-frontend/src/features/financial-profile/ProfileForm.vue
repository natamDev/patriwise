<script setup lang="ts">
import { reactive, ref } from 'vue'
import type {
  CreateFinancialProfilePayload,
  EmploymentStatus,
  FinancialExperienceLevel,
} from '@/types/profile.types'

const props = defineProps<{
  loading: boolean
}>()

const emit = defineEmits<{
  submit: [payload: CreateFinancialProfilePayload]
}>()

const form = reactive<CreateFinancialProfilePayload>({
  monthlyIncome: 0,
  employmentStatus: 'EMPLOYEE',
  age: 0,
  financialExperienceLevel: 'BEGINNER',
  currency: 'EUR',
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

function handleSubmit() {
  emit('submit', { ...form })
}
</script>

<template>
  <form class="profile-form" @submit.prevent="handleSubmit">
    <div class="profile-form__field">
      <label class="profile-form__label" for="monthlyIncome">Revenu mensuel net</label>
      <input
        id="monthlyIncome"
        v-model.number="form.monthlyIncome"
        class="profile-form__input"
        type="number"
        min="0"
        step="0.01"
        required
      />
    </div>

    <div class="profile-form__field">
      <label class="profile-form__label" for="employmentStatus">Statut professionnel</label>
      <select id="employmentStatus" v-model="form.employmentStatus" class="profile-form__select">
        <option v-for="option in employmentOptions" :key="option.value" :value="option.value">
          {{ option.label }}
        </option>
      </select>
    </div>

    <div class="profile-form__field">
      <label class="profile-form__label" for="age">Âge</label>
      <input
        id="age"
        v-model.number="form.age"
        class="profile-form__input"
        type="number"
        min="16"
        max="120"
        required
      />
    </div>

    <div class="profile-form__field">
      <label class="profile-form__label" for="financialExperienceLevel">
        Niveau en finance
      </label>
      <select
        id="financialExperienceLevel"
        v-model="form.financialExperienceLevel"
        class="profile-form__select"
      >
        <option v-for="option in experienceOptions" :key="option.value" :value="option.value">
          {{ option.label }}
        </option>
      </select>
    </div>

    <div class="profile-form__field">
      <label class="profile-form__label" for="currency">Devise</label>
      <select id="currency" v-model="form.currency" class="profile-form__select">
        <option v-for="c in currencyOptions" :key="c" :value="c">{{ c }}</option>
      </select>
    </div>

    <button class="profile-form__submit" type="submit" :disabled="props.loading">
      {{ props.loading ? 'Enregistrement...' : 'Enregistrer' }}
    </button>
  </form>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.profile-form {
  display: flex;
  flex-direction: column;
  gap: $spacing-lg;

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
