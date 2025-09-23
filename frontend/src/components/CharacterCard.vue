<script setup>
import { useRouter } from 'vue-router'
const props = defineProps({
  role: { type: Object, required: true } // {id,name,desc,cover?}
})
const router = useRouter()
function toChat() { router.push(`/chat/${props.role.id}`) }
function toDetail() { router.push(`/role/${props.role.id}`) }
</script>

<template>
  <article class="card">
    <div class="cover" :style="{ backgroundImage: `url(${role.cover || fallback})` }" />
    <div class="body">
      <h3 class="title">{{ role.name }}</h3>
      <p class="desc">{{ role.desc }}</p>
      <div class="actions">
        <button class="primary" @click="toChat">开始聊天</button>
        <button class="ghost" @click="toDetail">详情</button>
      </div>
    </div>
  </article>
</template>

<script>
const fallback = 'https://picsum.photos/400/220'
export default { }
</script>

<style scoped>
.card { border:1px solid #e5e7eb; border-radius:14px; overflow:hidden; background:#fff; display:flex; flex-direction:column; }
.cover { height:140px; background-size:cover; background-position:center; }
.body { padding:12px; display:flex; flex-direction:column; gap:8px; }
.title { margin:0; font-size:18px; }
.desc { margin:0; color:#6b7280; min-height:40px; }
.actions { display:flex; gap:8px; margin-top:4px; }
.primary, .ghost { padding:8px 12px; border-radius:10px; border:1px solid transparent; cursor:pointer; }
.primary { background:#5B7CFA; color:#fff; }
.ghost { background:transparent; border-color:#e5e7eb; }
.card:hover { box-shadow: 0 6px 24px rgba(0,0,0,.06); transform: translateY(-2px); transition: .2s; }
</style>
