import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '@/api/auth.api'
import type { AuthPayload } from '@/types/auth.types'

const TOKEN_KEY = 'finmate_token'

const token = ref<string | null>(localStorage.getItem(TOKEN_KEY))

export function useAuth() {
  const router = useRouter()

  const isAuthenticated = computed(() => token.value !== null)

  function getToken(): string | null {
    return token.value
  }

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem(TOKEN_KEY, newToken)
  }

  function clearToken() {
    token.value = null
    localStorage.removeItem(TOKEN_KEY)
  }

  async function register(payload: AuthPayload) {
    const response = await authApi.register(payload)
    setToken(response.token)
    await router.push('/profile')
  }

  async function login(payload: AuthPayload) {
    const response = await authApi.login(payload)
    setToken(response.token)
    await router.push('/')
  }

  function logout() {
    clearToken()
    router.push('/login')
  }

  return { isAuthenticated, getToken, register, login, logout }
}
