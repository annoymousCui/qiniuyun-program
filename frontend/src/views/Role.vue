<script setup>
import { useRoute } from 'vue-router'
const id = useRoute().params.id
</script>

<template>
  <div style="padding:16px">
    <h2>角色：{{ id }}</h2>
    <router-link :to="`/chat/${id}`">开始聊天</router-link>
  </div>
</template>
