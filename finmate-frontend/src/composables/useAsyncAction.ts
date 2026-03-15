import { ref } from 'vue'

export function useAsyncAction<T>() {
  const data = ref<T | null>(null) as import('vue').Ref<T | null>
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function execute(fn: () => Promise<T>, errorMessage: string) {
    loading.value = true
    error.value = null
    try {
      data.value = await fn()
    } catch {
      error.value = errorMessage
    } finally {
      loading.value = false
    }
  }

  function reset() {
    data.value = null
    loading.value = false
    error.value = null
  }

  return { data, loading, error, execute, reset }
}
