import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { title: '主页' }
  },
  {
    path: '/love-app',
    name: 'LoveApp',
    component: () => import('../views/LoveApp.vue'),
    meta: { title: 'AI恋爱大师' }
  },
  {
    path: '/love-manus',
    name: 'LoveManus',
    component: () => import('../views/LoveManus.vue'),
    meta: { title: 'AI超级智能体' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.afterEach((to) => {
  document.title = to.meta.title as string || 'AI智能体'
})

export default router 