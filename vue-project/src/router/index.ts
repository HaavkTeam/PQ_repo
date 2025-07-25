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
        {
          path: 'tests',
          component: () => import('../views/student/TestListView.vue'),
        },
        {
          path: 'quiz',
          component: () => import('../views/student/QuizView.vue'),
        },
      ],
    },
    {
      path: '/organizer',
      children: [
        {
          path: 'dashboard',
          component: () => import('../views/organizer/DashboardView.vue'),
        },
        {
          path: 'lecture/:id', // 添加:id参数
          component: () => import('../views/organizer/LectureView.vue'),
        },
      ],
    },
    {
      path: '/teacher',
      children: [
        {
          path: 'dashboard',
          component: () => import('../views/teacher/DashboardView.vue'),
        },
        {
          path: 'lecture/:id',
          component: () => import('../views/teacher/LectureView.vue'),
        },
      ],
    },
  ],
})

export default router
