import api from '@/config'
import type { AuthPayload, TokenResponse } from '@/types/auth.types'

export const authApi = {
  register(payload: AuthPayload): Promise<TokenResponse> {
    return api.post<TokenResponse>('/api/auth/register', payload).then((r) => r.data)
  },

  login(payload: AuthPayload): Promise<TokenResponse> {
    return api.post<TokenResponse>('/api/auth/login', payload).then((r) => r.data)
  },
}
