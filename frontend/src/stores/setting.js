import { defineStore } from 'pinia'
export const useSettingsStore = defineStore('settings', {
  state: () => ({ voiceId: 'zh-CN-XiaoyiNeural', rate: 'medium', saveHistory: true })
})
