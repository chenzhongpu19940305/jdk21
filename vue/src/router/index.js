import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/bilibili',
    name: 'Bilibili',
    component: () => import('../views/Bilibili.vue')
  },
  {
    path: '/juejin',
    name: 'Juejin',
    component: () => import('../views/Juejin.vue')
  },
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: () => import('../views/ArticleDetail.vue')
  },
  {
    path: '/web-content',
    name: 'WebContentReader',
    component: () => import('../views/WebContentReader.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

