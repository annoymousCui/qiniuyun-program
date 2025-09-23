import { ref } from 'vue'

export function useRecorder() {
  const status = ref('idle') // idle|recording
  const mediaRecorder = ref(null)
  const chunks = []
  const hasPermission = ref(false)

  async function askPermission() {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
      stream.getTracks().forEach(t => t.stop())
      hasPermission.value = true
    } catch {
      hasPermission.value = false
    }
    return hasPermission.value
  }

  async function start() {
    if (!hasPermission.value) await askPermission()
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    mediaRecorder.value = new MediaRecorder(stream, { mimeType: 'audio/webm' })
    chunks.length = 0
    mediaRecorder.value.ondataavailable = e => e.data.size && chunks.push(e.data)
    mediaRecorder.value.start()
    status.value = 'recording'
  }

  async function stop() {
    return new Promise(resolve => {
      mediaRecorder.value.onstop = () => {
        const blob = new Blob(chunks, { type: 'audio/webm' })
        status.value = 'idle'
        resolve(blob)
      }
      mediaRecorder.value.stop()
      mediaRecorder.value.stream.getTracks().forEach(t => t.stop())
    })
  }

  return { status, hasPermission, askPermission, start, stop }
}
