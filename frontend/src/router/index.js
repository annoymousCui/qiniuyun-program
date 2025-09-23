import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Role from '@/views/Role.vue'
import Chat from '@/views/Chat.vue'
import Settings from '@/views/Settings.vue'

export default createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Home },
    { path: '/role/:id', component: Role, props: true },
    { path: '/chat/:roleId', component: Chat, props: true },
    { path: '/settings', component: Settings }
  ]
})
