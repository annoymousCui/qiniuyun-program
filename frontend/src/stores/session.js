import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useSessionStore = defineStore('session', () => {
  const user = ref(null)
  const token = ref(null)
  const loading = ref(false)

  function setToken(t) {
    token.value = t
    try { localStorage.setItem('token', t) } catch (e) { /* ignore */ }
  }

  function clearToken() {
    token.value = null
    try { localStorage.removeItem('token') } catch (e) { /* ignore */ }
  }

  async function fetchCurrentUser() {
    loading.value = true
    try {
      // Try to use stored token if any
      const local = localStorage.getItem('token')
      if (local) token.value = local

      const headers = {}
      if (token.value) headers['Authorization'] = `Bearer ${token.value}`

      const res = await fetch('/api/user/me', { headers, credentials: 'include' })
      const body = await res.json().catch(() => null)
      if (!res.ok || !body) {
        user.value = null
        return null
      }
      // Accept both { code:200, data:user } or direct user object
      if (body.code === 200 && body.data) {
        user.value = body.data.user || body.data
        // if backend returned token here, store it
        if (body.data.token) setToken(body.data.token)
      } else if (body.username) {
        user.value = body
      } else {
        user.value = null
      }
      return user.value
    } catch (e) {
      user.value = null
      return null
    } finally {
      loading.value = false
    }
  }

  async function logout() {
    try {
      await fetch('/api/user/logout', { method: 'POST', credentials: 'include' })
    } catch (e) {
      // ignore
    }
    clearToken()
    user.value = null
  }

  return { user, token, loading, setToken, clearToken, fetchCurrentUser, logout }
})
