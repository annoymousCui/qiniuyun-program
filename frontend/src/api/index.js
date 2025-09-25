import http from './http'

// ASR（先用假请求，2s 后返回）
export async function asrUpload(blob) {
  await new Promise(r => setTimeout(r, 1200))
  return { text: '（模拟转写）你好，今天过得怎么样？' }
}

// Chat（SSE Mock，用浏览器 EventSource 简化）
export function chatStream({ roleId, sessionId, messages }) {
  // 开发期：用浏览器原生 EventSource 无法 POST，所以先用 fetch + ReadableStream 模拟
  const encoder = new TextEncoder()
  const chunks = [
    '朋友，', '让我们从定义开始。', '你说的“幸福”，', '指的是什么？'
  ]
  const stream = new ReadableStream({
    start(controller) {
      let i = 0
      const timer = setInterval(() => {
        if (i < chunks.length) {
          controller.enqueue(encoder.encode(JSON.stringify({ delta: chunks[i++] }) + '\n'))
        } else {
          clearInterval(timer)
          controller.enqueue(encoder.encode(JSON.stringify({ done: true, final_text: chunks.join('') }) + '\n'))
          controller.close()
        }
      }, 250)
    }
  })
  return new Response(stream).body.getReader() // 返回 reader，逐段读取
}

// TTS（直接给一段本地占位音频或不播）
export async function ttsGet(text, voiceId, rate) {
  return { audioUrl: 'https://interactive-examples.mdn.mozilla.net/media/cc0-audio/t-rex-roar.mp3' }
}

// 角色列表（写死几条）
export async function fetchRoles() {
  return [
    { id: 'harry', name: '哈利·波特', desc: '勇敢、重友情的少年英雄，鼓励用机智与合作解决难题。', tag: '奇幻冒险', cover: 'https://picsum.photos/id/1003/560/300' },
    { id: 'socrates', name: '苏格拉底', desc: '用连环提问澄清概念，引导你自己找到答案。', tag: '人生哲学', cover: 'https://picsum.photos/id/1011/560/300' },
    { id: 'li-bai', name: '李白', desc: '浪漫豪放，以意象抒怀，帮你把心绪变成画面。', tag: '温柔治愈', cover: 'https://picsum.photos/id/1015/560/300' }
  ]
}

