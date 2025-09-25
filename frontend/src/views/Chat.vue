<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, computed } from 'vue'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const roleId = route.params.roleId ?? 'unknown'

// mock mode: enable via ?mock=1 or saved localStorage 'mock' key or by toggle in UI
const mockMode = ref((route.query.mock === '1') || (localStorage.getItem('mock') === '1'))
function toggleMock() {
  mockMode.value = !mockMode.value
  try { localStorage.setItem('mock', mockMode.value ? '1' : '0') } catch (e) {}
}

// messages: { id, sender: 'user'|'role', type:'audio'|'text', text?, url?, ts }
const messages = ref([])
const recording = ref(false)
const recorder = ref(null)
const mediaStream = ref(null)
const chunks = ref([])
const timer = ref(0)
let timerInterval = null

// audio playback
const currentAudio = ref(null)

async function startRecording() {
  if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
    alert('您的浏览器不支持录音，请使用最新的 Chrome/Edge/Firefox')
    return
  }
  try {
    mediaStream.value = await navigator.mediaDevices.getUserMedia({ audio: true })
    recorder.value = new MediaRecorder(mediaStream.value)
    chunks.value = []
    recorder.value.ondataavailable = e => { if (e.data && e.data.size) chunks.value.push(e.data) }
    recorder.value.onstop = onStopRecord
    recorder.value.start()
    recording.value = true
    timer.value = 0
    timerInterval = setInterval(() => timer.value += 1, 1000)
  } catch (e) {
    console.error(e)
    alert('无法访问麦克风：' + e.message)
  }
}

function stopRecording() {
  if (recorder.value && recorder.value.state !== 'inactive') recorder.value.stop()
  if (timerInterval) { clearInterval(timerInterval); timerInterval = null }
  recording.value = false
  // stop tracks
  if (mediaStream.value) {
    mediaStream.value.getTracks().forEach(t => t.stop())
    mediaStream.value = null
  }
}

async function onStopRecord() {
  const blob = new Blob(chunks.value, { type: 'audio/webm' })
  const url = URL.createObjectURL(blob)
  const id = Date.now() + Math.random()
  // add a user audio message and mark as transcribing
  messages.value.push({ id, sender: 'user', type: 'audio', url, ts: Date.now(), transcript: null, transcribing: true })

  // First: call speech-to-text to get transcript
  try {
    if (mockMode.value) {
      // simulated transcription
      await new Promise(r => setTimeout(r, 600))
      const msg = messages.value.find(m => m.id === id)
      if (msg) { msg.transcript = '（模拟）这是识别出的文字内容：你在干嘛，我在学习代码，我要成为一名很牛的程序员，你觉得我的理想怎么样？你最近在忙什么，魔法学院发生了什么有趣的事情吗，可以给我讲讲吗，我成日里在学校学习敲代码。'; msg.transcribing = false }
    } else {
      const stFd = new FormData()
      stFd.append('file', blob, 'voice.webm')
      const stRes = await fetch('/api/voice/speech-to-text', { method: 'POST', body: stFd })
      const stBody = await stRes.json().catch(() => null)
      const msg = messages.value.find(m => m.id === id)
      if (stRes.ok && stBody && stBody.code === 200 && stBody.data && stBody.data.text) {
        if (msg) { msg.transcript = stBody.data.text; msg.transcribing = false }
      } else {
        if (msg) { msg.transcript = null; msg.transcribing = false }
      }
    }
  } catch (e) {
    console.warn('speech-to-text failed', e)
    const msg = messages.value.find(m => m.id === id)
    if (msg) { msg.transcript = null; msg.transcribing = false }
  }

  // Then: send to chat processing endpoint (keeps original behavior)
  try {
    if (mockMode.value) {
      // simulate chat reply
      await new Promise(r => setTimeout(r, 800))
      const reply = '（模拟）这是角色的自动回复：很高兴和你聊天！'
      messages.value.push({ id: id + '-r', sender: 'role', type: 'text', text: reply, ts: Date.now(), ttsLoading: false, ttsAudioUrl: null })
      // optionally auto-speak in mock mode
      speakText(reply)
      return
    }

    const fd = new FormData()
    fd.append('roleId', roleId)
    fd.append('file', blob, 'voice.webm')
    // optionally include transcript if available
    const u = messages.value.find(m => m.id === id)
    if (u && u.transcript) fd.append('transcript', u.transcript)

    const res = await fetch('/api/chat/voice', { method: 'POST', body: fd })
    if (res.ok) {
      // Expect JSON { code:200, data: { text?, audioUrl? } }
      const body = await res.json().catch(() => null)
      if (body && body.code === 200 && body.data) {
        const r = body.data
        if (r.audioUrl) {
          messages.value.push({ id: id + '-r', sender: 'role', type: 'audio', url: r.audioUrl, ts: Date.now() })
        } else if (r.text) {
          // push text and attach helper fields for TTS
          messages.value.push({ id: id + '-r', sender: 'role', type: 'text', text: r.text, ts: Date.now(), ttsLoading: false, ttsAudioUrl: null })
          speakText(r.text)
        } else {
          messages.value.push({ id: id + '-r', sender: 'role', type: 'text', text: '角色已收到您的消息（无响应）', ts: Date.now(), ttsLoading: false, ttsAudioUrl: null })
        }
        return
      }
    }
    // fallback to TTS if backend not available or returned unexpected
    fallbackReply()
  } catch (e) {
    console.warn('upload failed', e)
    fallbackReply()
  }
}

function fallbackReply() {
  const txt = pickFallbackReply()
  messages.value.push({ id: 'r-' + Date.now(), sender: 'role', type: 'text', text: txt, ts: Date.now() })
  speakText(txt)
}

function pickFallbackReply() {
  const list = [
    '嗯，我听到了。能再说得详细一点吗？',
    '这是个有趣的问题，我想我们可以尝试这样……',
    '好的，我明白了，让我想想。'
  ]
  return list[Math.floor(Math.random() * list.length)]
}

function speakText(text) {
  if (!('speechSynthesis' in window)) return
  const ut = new SpeechSynthesisUtterance(text)
  ut.lang = 'zh-CN'
  window.speechSynthesis.cancel()
  window.speechSynthesis.speak(ut)
}

function playMessage(m) {
  if (m.type === 'audio' && m.url) {
    if (currentAudio.value) { currentAudio.value.pause(); currentAudio.value = null }
    const a = new Audio(m.url)
    currentAudio.value = a
    a.play()
  } else if (m.type === 'text' && m.text) {
    // For text messages, if there's a cached ttsAudioUrl, play it; otherwise use speechSynthesis
    if (m.ttsAudioUrl) {
      if (currentAudio.value) { currentAudio.value.pause(); currentAudio.value = null }
      const a = new Audio(m.ttsAudioUrl)
      currentAudio.value = a
      a.play()
    } else {
      speakText(m.text)
    }
  }
}

async function playTTS(m) {
  if (!m || !m.text) return
  // if already have audio url, play it
  if (m.ttsAudioUrl) { playMessage(m); return }
  // mark loading
  m.ttsLoading = true
  try {
    const params = new URLSearchParams()
    params.append('text', m.text)
    params.append('language', 'zh')
    const res = await fetch('/api/voice/text-to-speech', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params.toString()
    })
    const body = await res.json().catch(() => null)
    if (res.ok && body && body.code === 200 && body.data && body.data.audioUrl) {
      // audioUrl may be relative; resolve if needed
      m.ttsAudioUrl = body.data.audioUrl
      m.ttsLoading = false
      playMessage(m)
    } else {
      m.ttsLoading = false
      // fallback to speechSynthesis
      speakText(m.text)
    }
  } catch (e) {
    console.warn('tts failed', e)
    m.ttsLoading = false
    speakText(m.text)
  }
}

onBeforeUnmount(() => {
  if (timerInterval) clearInterval(timerInterval)
  if (mediaStream.value) mediaStream.value.getTracks().forEach(t => t.stop())
})

</script>

<template>
  <div class="chat-root">
    <header class="chat-header">
      <button class="back" @click="router.push('/')" aria-label="返回首页">
        <ArrowLeft class="back-icon" />
      </button>
      <div class="role-info">
        <div class="role-avatar">{{ roleId.charAt(0).toUpperCase() }}</div>
        <div class="role-meta">
          <div class="role-name">{{ roleId }}</div>
          <div class="role-desc">语音角色，随时陪聊</div>
        </div>
      </div>
      <div class="header-actions">
        <router-link to="/">首页</router-link>
        <button class="mock-toggle" @click="toggleMock" :aria-pressed="mockMode">{{ mockMode ? 'Mock: ON' : 'Mock: OFF' }}</button>
      </div>
    </header>

      <main class="chat-main">
        <ul class="messages">
          <li v-for="m in messages" :key="m.id" :class="['msg', m.sender]">
            <div class="content">
              <div class="bubble" @click="m.type === 'audio' ? playMessage(m) : null">
              <template v-if="m.type === 'audio'">
                <svg class="icon-play" viewBox="0 0 24 24"><path fill="currentColor" d="M8 5v14l11-7z"/></svg>
                <span class="label">语音消息 · {{ new Date(m.ts).toLocaleTimeString() }}</span>
              </template>
              <template v-else>
                <div class="text">{{ m.text }}</div>
                <div class="meta">{{ new Date(m.ts).toLocaleTimeString() }}</div>
              </template>
            </div>

            <!-- transcript for user audio -->
            <div v-if="m.sender === 'user' && (m.transcribing || m.transcript)" class="transcript">
              <template v-if="m.transcribing">识别中…</template>
              <template v-else>{{ m.transcript || '未识别到内容' }}</template>
            </div>

            <!-- role text actions: play TTS -->
            <div v-if="m.sender === 'role' && m.type === 'text'" class="role-actions">
              <button class="play-tts" @click.stop.prevent="playTTS(m)" :disabled="m.ttsLoading">
                <template v-if="m.ttsLoading">生成语音…</template>
                <template v-else>播放语音</template>
              </button>
            </div>
          </div>
          </li>
        </ul>
      </main>

    <footer class="chat-controls">
      <div class="recorder">
        <button class="rec-btn" @mousedown.prevent="startRecording" @mouseup.prevent="stopRecording" @touchstart.prevent="startRecording" @touchend.prevent="stopRecording" :class="{ recording }">
          <svg class="mic" viewBox="0 0 24 24"><path fill="currentColor" d="M12 14a3 3 0 0 0 3-3V5a3 3 0 0 0-6 0v6a3 3 0 0 0 3 3zm5-3a5 5 0 0 1-10 0H5a7 7 0 0 0 14 0h-2zM11 19h2v3h-2z"/></svg>
          <span class="rec-label">{{ recording ? (timer + 's') : '按住 说话' }}</span>
        </button>
      </div>
      <div class="small-actions">
        <button class="btn" @click="fallbackReply">角色回复示例</button>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.chat-root { 
  max-width:900px; 
  margin:20px auto; 
  background:linear-gradient(180deg,#fff,#fbfdff); 
  border-radius:12px; 
  box-shadow:0 10px 30px rgba(18,38,63,0.06); 
  overflow:hidden; 
  display:flex; 
  flex-direction:column; 
}
.chat-header { 
  display:flex; 
  align-items:center; 
  gap:12px; 
  padding:12px 16px; 
  border-bottom:1px solid #eef2ff; 
}
.back { 
  background:transparent;
  border:none;
  padding:6px;
  border-radius:8px;
  display:inline-flex;
  align-items:center;
  justify-content:center;
  cursor:pointer;
  transition:background .15s ease;
}
.back:hover { background:rgba(91,124,250,0.06) }
.back-icon { width:18px; height:18px; color:#374151 }
.role-info { 
  display:flex; 
  gap:12px; 
  align-items:center 
}
.role-avatar { 
  width:56px; 
  height:56px; 
  border-radius:12px; 
  background:linear-gradient(135deg,#7c5cff,#5b7cfa); 
  color:#fff; 
  display:flex; 
  align-items:center; 
  justify-content:center; 
  font-weight:700; 
  font-size:20px 
}
.role-meta .role-name { 
  font-weight:700 
}
.role-meta .role-desc { 
  font-size:12px; 
  color:#6b7280 
}
.header-actions { 
  margin-left:auto 
}

.mock-toggle { margin-left:10px; padding:6px 10px; border-radius:8px; border:1px solid #e5e7eb; background:transparent; cursor:pointer }
.mock-toggle[aria-pressed="true"] { background:#eef2ff; border-color:#5B7CFA }

.chat-main { 
  padding:18px; 
  height:520px; 
  overflow:auto; 
  background:linear-gradient(180deg,#f8fbff,#ffffff); 
}
.messages { 
  list-style:none; 
  padding:0; 
  margin:0; 
  display:flex; 
  flex-direction:column; 
  gap:12px 
}
.msg { 
  display:flex 
}
.msg.user { 
  justify-content:flex-end 
}
.msg.role { 
  justify-content:flex-start 
}
.msg .content { display:flex; flex-direction:column; align-items:flex-start }
.msg.user .content { align-items:flex-end }
.bubble { 
  max-width:66%; 
  padding:12px 14px; 
  border-radius:12px; 
  background:linear-gradient(90deg,#eef2ff,#e6f0ff); 
  cursor:pointer; 
  display:flex; 
  gap:8px; 
  align-items:center 
}
.msg.user .bubble { 
  background:linear-gradient(90deg,#5b7cfa,#6f8cff); 
  color:#fff }
.icon-play { 
  width:22px; 
  height:22px 
}
.label { 
  font-size:13px; 
  color:inherit }
.text { 
  font-size:14px 
}
.meta { 
  font-size:12px; 
  color:rgba(0,0,0,0.45); 
  margin-top:6px 
}

.transcript {
  margin:6px 8px 0 8px;
  font-size:13px;
  color:#374151;
  background:#f8fafc;
  padding:8px 10px;
  border-radius:10px;
  max-width:70%;
}

.role-actions { margin-top:6px; margin-left:8px }
.play-tts { padding:6px 10px; border-radius:8px; border:1px solid #e5e7eb; background:transparent; cursor:pointer }
.play-tts[disabled] { opacity:0.6; cursor:not-allowed }

.chat-controls { 
  display:flex; 
  gap:12px; 
  align-items:center; 
  padding:14px; 
  border-top:1px solid #eef2ff 
}
.recorder { 
  flex:1 
}
.rec-btn { 
  display:inline-flex; 
  gap:10px; 
  align-items:center; 
  padding:12px 18px; 
  border-radius:999px; 
  background:#fff; 
  border:1px solid #e5e7eb; 
  cursor:pointer 
}
.rec-btn.recording { 
  background:linear-gradient(90deg,#ff6b6b,#ff8a8a); 
  color:#fff; 
  box-shadow:0 6px 18px rgba(255,105,105,0.12) 
}
.mic { 
  width:20px; 
  height:20px 
}
.rec-label { 
  font-weight:600 
}
.small-actions { 
  display:flex; 
  gap:8px 
}
.btn { 
  padding:8px 12px; 
  border-radius:8px; 
  border:1px solid #e5e7eb; 
  background:transparent; 
  cursor:pointer 
}

@media (max-width: 700px) {
  .chat-root { margin:8px }
  .chat-main { height:60vh }
}
</style>
