<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { fetchRoles } from '@/api'
import CharacterCard from '@/components/CharacterCard.vue'
import { useSessionStore } from '@/stores/session'

const router = useRouter()
const session = useSessionStore()
const user = session.user
const menuOpen = ref(false)

function toggleMenu() { menuOpen.value = !menuOpen.value }
async function doLogout() { await session.logout(); menuOpen.value = false; router.push('/') }
const loading = ref(true)
const keyword = ref('')
const roles = ref([])
const activeTag = ref('全部')

const tags = ['全部', '奇幻冒险', '人生哲学', '温柔治愈', '推理探案']

onMounted(async () => {
  roles.value = await fetchRoles() // [{id,name,desc,cover?}]
  loading.value = false
})

function onSearch() {
  const kw = keyword.value.trim().toLowerCase()
  if (!kw) return
  // 简单“本地过滤”示例：实际可跳转到 /search?kw=xxx
  roles.value = roles.value.filter(r =>
    r.name.toLowerCase().includes(kw) || r.desc.toLowerCase().includes(kw)
  )
}

const filtered = computed(() => {
  if (activeTag.value === '全部') return roles.value
  // 这里演示按 tag 过滤；真实数据可让后端返回 tags
  return roles.value.filter(r => r.tag === activeTag.value)
})

function gotoSettings() {
  router.push('/settings')
}
</script>

<template>
  <div class="home">
    <!-- 顶部导航 -->
    <header class="nav">
      <div class="brand">AI 角色聊天</div>
      <div class="nav-actions">
        <button class="ghost" @click="gotoSettings" aria-label="设置">设置</button>

        <template v-if="user">
          <div class="user-wrap">
            <button class="avatar-btn" @click="toggleMenu" :aria-expanded="menuOpen">
              <img v-if="user.avatar" :src="user.avatar" alt="头像" class="avatar" />
              <span v-else class="avatar-initial">{{ (user.nickname || user.username || 'U').charAt(0).toUpperCase() }}</span>
            </button>
            <div v-if="menuOpen" class="user-menu" role="menu">
              <router-link to="/profile" class="menu-item">个人中心</router-link>
              <button class="menu-item" @click="doLogout">登出</button>
            </div>
          </div>
        </template>

        <template v-else>
          <!-- 登录/注册，次级操作 -->
          <router-link class="link" to="/login">登录</router-link>
          <router-link class="outline" to="/register">注册</router-link>
        </template>

        <router-link class="primary" to="/chat/harry">快速体验</router-link>
      </div>
    </header>

    <!-- 搜索框 -->
    <section class="search">
      <input
        v-model="keyword"
        type="text"
        placeholder="输入角色名或关键词，开始探索奇幻世界…"
        @keyup.enter="onSearch"
      />
      <button class="search-btn" @click="onSearch">🔍</button>
    </section>

    <!-- 分类 Chips -->
    <section class="chips">
      <button
        v-for="t in tags"
        :key="t"
        :class="['chip', { active: t === activeTag }]"
        @click="activeTag = t"
      >
        {{ t }}
      </button>
    </section>

    <!-- Hero 横幅 -->
    <section class="hero">
      <div class="hero-text">
        <h2>魔法觉醒</h2>
        <p>嗨！今天的“魔法难题”是什么？我们一起想个办法吧。</p>
        <router-link class="primary" to="/chat/harry">立即体验</router-link>
      </div>
      <div class="hero-cover" aria-hidden="true" />
    </section>

    <!-- 列表 -->
    <section class="list">
      <template v-if="loading">
        <div class="skeleton" v-for="i in 3" :key="i" />
      </template>
      <template v-else>
        <CharacterCard
          v-for="r in filtered"
          :key="r.id"
          :role="r"
        />
        <div v-if="!filtered.length" class="empty">
          没有找到匹配的角色，试试其他关键词？
        </div>
      </template>
    </section>
  </div>
</template>

<style scoped>
.home { 
  max-width: 1120px; 
  margin: 0 auto; 
  padding: 16px 20px 40px; 
}
.nav { 
  display:flex; 
  justify-content:space-between; 
  align-items:center; 
  padding:8px 0 16px; 
}
.brand { 
  font-weight:700; 
  font-size:20px; 
}
.nav-actions { 
  display:flex; 
  gap:8px; 
}
.primary, .ghost, .search-btn {
  border-radius:10px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 40px;
  padding: 0 14px;
  border:1px solid transparent;
  cursor:pointer;
}
.primary { 
  background:#5B7CFA; 
  color:#fff; 
}
.primary, a.primary {
  text-decoration: none;
}
.ghost { 
  background:transparent; 
  border-color:#e5e7eb; 
}

.user-wrap { position: relative; }
.avatar-btn { 
  width:40px; height:40px; border-radius:999px; padding:0; border:1px solid transparent; display:inline-flex; align-items:center; justify-content:center; cursor:pointer; background:transparent;
}
.avatar { width:36px; height:36px; border-radius:999px; object-fit:cover; }
.avatar-initial { width:36px; height:36px; border-radius:999px; display:inline-flex; align-items:center; justify-content:center; background:#f3f4f6; color:#374151; font-weight:600 }
.user-menu { position:absolute; right:0; top:48px; background:#fff; border:1px solid #e5e7eb; border-radius:8px; box-shadow:0 6px 18px rgba(0,0,0,0.08); min-width:140px; z-index:40; }
.menu-item { display:block; padding:8px 12px; color:#374151; text-align:left; border:none; background:transparent; width:100%; }
.menu-item:hover { background:#f8fafc }
.search { 
  display:flex; 
  gap:8px; 
  margin:12px 0 8px; 
}
.search input {
  flex:1; 
  height:40px; 
  border:1px solid #e5e7eb; 
  border-radius:12px; 
  padding:0 12px;
  outline:none;
}

.chips { 
  display:flex; 
  flex-wrap:wrap; 
  gap:8px; 
  margin:8px 0 16px; 
}
.chip {
  padding:6px 12px; 
  border-radius:999px; 
  background:#f3f4f6; 
  border:1px solid #e5e7eb; 
  cursor:pointer;
}
.chip.active { 
  background:#eef2ff; 
  border-color:#5B7CFA; 
  color:#374151; 
}
.hero {
  margin: 8px 0 20px; 
  border-radius:16px; 
  min-height:160px;
  background: linear-gradient(90deg, #eef2ff, #e0e7ff);
  display:grid; 
  grid-template-columns: 1fr 280px; 
  overflow:hidden;
}
.hero-text { 
  padding:20px; 
  display:flex; 
  flex-direction:column; 
  gap:10px; 
}
.hero-text h2 { 
  margin:0; 
}
.hero-cover { 
  background: url('https://picsum.photos/560/240?blur=2') center/cover no-repeat; 
}
.list { 
  display:grid; 
  grid-template-columns: repeat(3, 1fr); 
  gap:16px; }
.skeleton { 
  height:180px; 
  border-radius:14px; 
  background:linear-gradient(90deg,#f3f4f6,#e5e7eb,#f3f4f6); 
  animation: shimmer 1.2s infinite; 
}
@keyframes shimmer { 0%{background-position:-200px 0}100%{background-position:200px 0} }
.empty { 
  grid-column:1/-1; 
  text-align:center; 
  padding:24px; 
  color:#6b7280; }
@media (max-width: 900px) { 
  .list { 
    grid-template-columns: 1fr; 
  } 
  .hero { 
    grid-template-columns: 1fr; 
  } 
}
</style>
