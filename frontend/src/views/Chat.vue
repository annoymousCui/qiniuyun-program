<script setup>
import { useRoute } from 'vue-router'
const roleId = useRoute().params.roleId ?? 'unknown'
</script>

<template>
  <div style="padding:16px">
    <h2>Chat · {{ roleId }}</h2>
    <p>聊天界面</p>
    <router-link to="/">返回首页</router-link>
  </div>
</template>

<style scoped>
h2 { margin: 0 0 8px; }
</style>
