import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/Layout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue')
      },
      {
        path: 'shelters',
        name: 'Shelters',
        component: () => import('../views/Shelters.vue')
      },
      {
        path: 'reservations',
        name: 'Reservations',
        component: () => import('../views/Reservations.vue')
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('../views/Orders.vue')
      },
      {
        path: 'alarms',
        name: 'Alarms',
        component: () => import('../views/Alarms.vue')
      },
      {
        path: 'inspections',
        name: 'Inspections',
        component: () => import('../views/Inspections.vue')
      },
      {
        path: 'complaints',
        name: 'Complaints',
        component: () => import('../views/Complaints.vue')
      },
      {
        path: 'maintenance',
        name: 'Maintenance',
        component: () => import('../views/Maintenance.vue')
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/Users.vue')
      },
      {
        path: 'fee-rules',
        name: 'FeeRules',
        component: () => import('../views/FeeRules.vue')
      },
      {
        path: 'night-safety',
        name: 'NightSafety',
        component: () => import('../views/NightSafety.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const user = localStorage.getItem('user')
  if (to.path === '/login') {
    next()
  } else {
    if (user) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
