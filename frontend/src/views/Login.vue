<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref(null)

async function submit() {
  loading.value = true
  error.value = null
  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ username: username.value, password: password.value })
    })
    if (!res.ok) {
      const body = await res.json().catch(() => ({}))
      throw new Error(body.message || '登录失败')
    }
    // 登录成功后尝试跳转到 redirect 或首页
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <h2>登录</h2>
    <div class="form-row">
      <input v-model="username" placeholder="用户名或邮箱" />
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
  padding:20px 
}
.form-row { 
  margin-bottom:12px 
}
input { 
  width:100%; 
  height:40px; 
  padding:8px 10px; 
  border-radius:8px; 
  border:1px solid #e5e7eb 
}
.error { 
  color:#dc2626 
}
</style>
