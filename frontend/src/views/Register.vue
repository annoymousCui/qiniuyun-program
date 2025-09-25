<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const email = ref('')
const password = ref('')
const nickname = ref('')
const loading = ref(false)
const error = ref(null)

const usernameValid = computed(() => username.value.trim().length >= 3 && username.value.trim().length <= 20)
const passwordValid = computed(() => password.value.length >= 6 && password.value.length <= 20)
const emailValid = computed(() => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value))

function validate() {
  if (!usernameValid.value) {
    error.value = '用户名需为 3-20 个字符'
    return false
  }
  if (!emailValid.value) {
    error.value = '请输入有效的邮箱地址'
    return false
  }
  if (!passwordValid.value) {
    error.value = '密码需为 6-20 个字符'
    return false
  }
  return true
}

async function submit() {
  error.value = null
  if (!validate()) return

  loading.value = true
  try {
    const res = await fetch('api/user/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username: username.value.trim(),
        email: email.value.trim(),
        password: password.value,
        nickname: nickname.value.trim() || undefined
      })
    })

    // Attempt to parse JSON response
    const body = await res.json().catch(() => null)
    if (!res.ok) {
      // If server returns non-2xx, prefer message in body
      throw new Error((body && body.message) || '注册失败')
    }

    // The backend returns { code: 200, message: '注册成功', data: { user, token } }
    if (!body || body.code !== 200) {
      throw new Error((body && body.message) || '注册失败')
    }

    // Save token (如果后端返回 token)
    if (body.data && body.data.token) {
      try { localStorage.setItem('token', body.data.token) } catch (e) { /* ignore */ }
    }

    // 注册成功后跳转到首页
    router.push('/')
  } catch (e) {
    error.value = e.message || '注册失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <h2>注册</h2>

    <div class="form-row">
      <input v-model="username" placeholder="用户名（3-20字符）" />
    </div>

    <div class="form-row">
      <input v-model="email" type="email" placeholder="邮箱" />
    </div>

    <div class="form-row">
      <input v-model="password" type="password" placeholder="密码（6-20字符）" />
    </div>

    <div class="form-row">
      <input v-model="nickname" placeholder="昵称（可选）" />
    </div>

    <div class="form-row">
      <button class="primary" @click="submit" :disabled="loading">注册</button>
    </div>

    <div v-if="error" class="error">{{ error }}</div>

    <p>已有账号？ <router-link to="/login">去登录</router-link></p>
  </div>
</template>

<style scoped>
.auth-page { 
  max-width:420px; 
  margin:40px auto; 
  padding:20px;
}
.form-row { 
  margin-bottom:12px;
}
input { 
  width:100%; 
  height:40px; 
  padding:8px 10px; 
  border-radius:8px; 
  border:1px solid #e5e7eb;
}
.error { 
  color:#dc2626;
  margin-top:8px;
}
</style>
