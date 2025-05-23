import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { 
      title: 'AI智能体应用平台 - 智能服务选择中心',
      description: 'AI智能体应用平台提供多种智能服务，包括AI恋爱大师和AI超级智能体，满足您不同场景下的智能需求。',
      keywords: 'AI智能体,人工智能,智能服务,AI应用,AI平台'
    }
  },
  {
    path: '/love-app',
    name: 'LoveApp',
    component: () => import('../views/LoveApp.vue'),
    meta: { 
      title: 'AI恋爱大师 - 智能情感咨询',
      description: 'AI恋爱大师为您提供专业的情感咨询服务，帮助您解决恋爱问题，提升情感关系质量。',
      keywords: 'AI恋爱大师,情感咨询,恋爱问题,关系建议,情感分析'
    }
  },
  {
    path: '/love-manus',
    name: 'LoveManus',
    component: () => import('../views/LoveManus.vue'),
    meta: { 
      title: 'AI超级智能体 - 您的智能助手',
      description: 'AI超级智能体是一款强大的智能助手，能够解答您的各种问题，帮助您完成复杂任务。',
      keywords: 'AI超级智能体,智能助手,问题解答,知识检索,工具调用'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.afterEach((to) => {
  // 更新页面标题
  document.title = to.meta.title as string || 'AI智能体应用平台'
  
  // 更新meta description
  let descriptionMeta = document.querySelector('meta[name="description"]')
  if (descriptionMeta) {
    descriptionMeta.setAttribute('content', to.meta.description as string || 'AI智能体应用平台，提供AI恋爱大师和AI超级智能体等智能服务')
  }
  
  // 更新meta keywords
  let keywordsMeta = document.querySelector('meta[name="keywords"]')
  if (keywordsMeta) {
    keywordsMeta.setAttribute('content', to.meta.keywords as string || 'AI,智能体,AI恋爱大师,AI超级智能体,人工智能,智能服务')
  }
})

export default router 