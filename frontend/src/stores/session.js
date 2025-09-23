import { defineStore } from 'pinia'
export const useSessionStore = defineStore('session', {
  state: () => ({
    sessionId: null,
    roleId: null,
    messages: []
  }),
  actions: {
    reset(roleId) {
      this.sessionId = crypto.randomUUID();
      this.roleId = roleId;
      this.messages = []
    },
    push(role, content) {
      this.messages.push({ role, content, ts: Date.now() })
    }
  }
})
