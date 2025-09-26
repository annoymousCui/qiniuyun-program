
<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useSessionStore } from '@/stores/session'

const router = useRouter()
const session = useSessionStore()

const mock = ref(localStorage.getItem('mock') === '1')
const ttsLang = ref(localStorage.getItem('ttsLang') || 'zh')

function toggleMock() {
  mock.value = !mock.value
  try { localStorage.setItem('mock', mock.value ? '1' : '0') } catch (e) {}
}

function saveTtsLang() {
  try { localStorage.setItem('ttsLang', ttsLang.value) } catch (e) {}
  // 简单提示，项目中可替换为更友好的通知组件
  alert('已保存')
}

function clearLocal() {
  try {
    localStorage.removeItem('mock')
    localStorage.removeItem('ttsLang')
    localStorage.removeItem('token')
  } catch (e) {}
  alert('本地数据已清除')
}

async function doLogout() {
  await session.logout()
  router.push('/')
}

// 如果其他地方修改了 localStorage，也同步到 UI
window.addEventListener('storage', (e) => {
  if (e.key === 'mock') mock.value = e.newValue === '1'
  if (e.key === 'ttsLang') ttsLang.value = e.newValue || 'zh'
})
</script>

<template>
  <div class="settings-root">
    <h2>设置</h2>

    <div class="row">
      <label>Mock 模式</label>
      <div>
        <input type="checkbox" v-model="mock" @change="toggleMock" />
        <span class="hint">启用后聊天页面使用本地模拟数据（开发用）</span>
      </div>
    </div>

    <div class="row">
      <label>TTS 语言</label>
      <div>
        <select v-model="ttsLang">
          <option value="zh">中文 (zh)</option>
          <option value="en">English (en)</option>
        </select>
        <button class="btn" @click="saveTtsLang">保存</button>
      </div>
    </div>

    <div class="row">
      <label>本地数据</label>
      <div>
        <button class="btn" @click="clearLocal">清除本地数据</button>
      </div>
    </div>

    <div class="row">
      <label>账户</label>
      <div>
        <button class="btn" @click="doLogout">登出</button>
      </div>
    </div>

  </div>
</template>

<style scoped>
.settings-root { 
  max-width:640px; 
  margin:24px auto; 
  padding:20px; 
  background:#fff; 
  border-radius:12px; 
  box-shadow:0 6px 20px rgba(10,20,40,0.04) 
}
.row { 
  display:flex; 
  align-items:center; 
  gap:12px; 
  margin-bottom:14px 
}
.row label { 
  width:120px; 
  font-weight:600 
}
.hint { 
  color:#6b7280; 
  font-size:13px; 
  margin-left:8px 
}
.btn { 
  padding:8px 12px; 
  border-radius:8px; 
  border:1px solid #e5e7eb; 
  background:transparent; 
  cursor:pointer 
}
</style>
