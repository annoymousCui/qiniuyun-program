<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref(null)

const usernameValid = computed(() => username.value.trim().length > 0)
const passwordValid = computed(() => password.value.length > 0)

function validate() {
  if (!usernameValid.value) {
    error.value = '请输入用户名'
    return false
  }
  if (!passwordValid.value) {
    error.value = '请输入密码'
    return false
  }
  return true
}

async function submit() {
  error.value = null
  if (!validate()) return

  loading.value = true
  try {
    // application/x-www-form-urlencoded
    const params = new URLSearchParams()
    params.append('username', username.value.trim())
    params.append('password', password.value)

    const res = await fetch('/api/user/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params.toString()
    })

    const body = await res.json().catch(() => null)
    if (!res.ok) {
      throw new Error((body && body.message) || '登录失败')
    }

    // 服务端返回 { code:200, message:'登录成功', data: { user, token } }
    if (!body || body.code !== 200) {
      throw new Error((body && body.message) || '登录失败')
    }

    // 保存 token（若后端返回 token）
    if (body.data && body.data.token) {
      try { localStorage.setItem('token', body.data.token) } catch (e) { /* ignore */ }
    }

    // 跳转到 redirect 或首页
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    error.value = e.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <h2>登录</h2>

    <div class="form-row">
      <input v-model="username" placeholder="用户名" />
    </div>

    <div class="form-row">
      <input v-model="password" type="password" placeholder="密码" />
    </div>

    <div class="form-row">
      <button class="primary" @click="submit" :disabled="loading">登录</button>
    </div>

    <div v-if="error" class="error">{{ error }}</div>

    <p>没有账号？ <router-link to="/register">去注册</router-link></p>
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
