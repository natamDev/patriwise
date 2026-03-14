<script setup lang="ts">
import axios from 'axios'
import { reactive, ref } from 'vue'
import { useAuth } from '@/composables/useAuth'

const { login, register } = useAuth()

const mode = ref<'login' | 'register'>('login')
const loading = ref(false)
const error = ref<string | null>(null)

const form = reactive({ email: '', password: '' })

async function handleSubmit() {
  loading.value = true
  error.value = null
  try {
    if (mode.value === 'login') {
      await login({ email: form.email, password: form.password })
    } else {
      await register({ email: form.email, password: form.password })
    }
  } catch (e: unknown) {
    if (axios.isAxiosError(e) && e.response?.data?.error) {
      error.value = e.response.data.error
    } else {
      error.value = 'Une erreur est survenue. Réessaie.'
    }
  } finally {
    loading.value = false
  }
}

function toggleMode() {
  mode.value = mode.value === 'login' ? 'register' : 'login'
  error.value = null
}
</script>

<template>
  <div class="login">
    <div class="login__header">
      <h1 class="login__title">FinMate</h1>
      <p class="login__subtitle">
        {{ mode === 'login' ? 'Connecte-toi à ton compte' : 'Crée ton compte' }}
      </p>
    </div>

    <div v-if="error" class="login__error">{{ error }}</div>

    <form class="login__form" @submit.prevent="handleSubmit">
      <div class="login__field">
        <label class="login__label" for="email">Email</label>
        <input
          id="email"
          v-model="form.email"
          class="login__input"
          type="email"
          autocomplete="email"
          required
        />
      </div>

      <div class="login__field">
        <label class="login__label" for="password">Mot de passe</label>
        <input
          id="password"
          v-model="form.password"
          class="login__input"
          type="password"
          autocomplete="current-password"
          minlength="8"
          required
        />
      </div>

      <button class="login__submit" type="submit" :disabled="loading">
        {{ loading ? '...' : mode === 'login' ? 'Se connecter' : 'Créer un compte' }}
      </button>
    </form>

    <button class="login__toggle" type="button" @click="toggleMode">
      {{ mode === 'login' ? 'Pas encore de compte ? S\'inscrire' : 'Déjà un compte ? Se connecter' }}
    </button>
  </div>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.login {
  min-height: 100dvh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: $spacing-xl $spacing-lg;

  &__header {
    margin-bottom: $spacing-xl;
    text-align: center;
  }

  &__title {
    font-size: $font-size-xxl;
    font-weight: 700;
    color: $color-primary;
    margin-bottom: $spacing-sm;
  }

  &__subtitle {
    font-size: $font-size-base;
    color: $color-text-muted;
  }

  &__error {
    padding: $spacing-sm $spacing-md;
    background-color: #fef2f2;
    color: $color-danger;
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

  &__input {
    padding: $spacing-sm $spacing-md;
    border: 1px solid $color-border;
    border-radius: $radius-sm;
    font-size: $font-size-base;
    color: $color-text;

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
    }
  }

  &__toggle {
    margin-top: $spacing-lg;
    background: none;
    border: none;
    color: $color-secondary;
    font-size: $font-size-sm;
    font-weight: 500;
    cursor: pointer;
    text-align: center;
  }
}
</style>
