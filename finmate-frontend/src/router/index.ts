import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'

const TOKEN_KEY = 'finmate_token'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/login',     name: 'login',     component: LoginView,                                              meta: { public: true } },
    { path: '/',          name: 'home',      component: HomeView },
    { path: '/budget',    name: 'budget',    component: () => import('@/views/BudgetView.vue') },
    { path: '/goals',     name: 'goals',     component: () => import('@/views/GoalsView.vue') },
    { path: '/learn',     name: 'learn',     component: () => import('@/views/LearnView.vue') },
    { path: '/assistant', name: 'assistant', component: () => import('@/views/AssistantView.vue') },
    { path: '/profile',   name: 'profile',   component: () => import('@/views/ProfileView.vue') },
  ],
})

router.beforeEach((to) => {
  const isAuthenticated = Boolean(localStorage.getItem(TOKEN_KEY))
  if (!to.meta.public && !isAuthenticated) {
    return { name: 'login' }
  }
  if (to.name === 'login' && isAuthenticated) {
    return { name: 'home' }
  }
})

export default router
