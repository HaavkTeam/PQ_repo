import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/auth/login',
    },
    {
      path: '/auth',
      children: [
        {
          path: 'login',
          component: () => import('../views/auth/LoginView.vue'),
        },
        {
          path: 'register',
          component: () => import('../views/auth/RegisterView.vue'),
        },
      ],
    },
    {
      path: '/student',
      children: [
        {
          path: 'dashboard',
          component: () => import('../views/student/DashboardView.vue'),
        },
      ],
    },
  ],
})

export default router
