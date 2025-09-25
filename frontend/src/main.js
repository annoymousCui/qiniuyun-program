import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
import { useSessionStore } from './stores/session'

async function bootstrap() {
  const app = createApp(App)
  const pinia = createPinia()
  app.use(pinia)
  app.use(router)

  // Try to restore session before mounting (best-effort)
  const session = useSessionStore(pinia)
  try {
    await session.fetchCurrentUser()
  } catch (e) {
    // ignore - allow app to mount even if session restore fails
    console.warn('session restore failed', e)
  }

  app.mount('#app')
}

bootstrap()

