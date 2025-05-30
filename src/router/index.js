import { createRouter, createWebHistory } from 'vue-router';

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/pages/auth/LoginPage.vue')
      },
      {
        path: '/enterpriseRegister',
        name: 'EnterpriseRegister',
        component: () => import('@/pages/auth/EnterpriseRegister.vue')
      },
      {
        path: '/workerRegister',
        name: 'WorkerRegister',
        component: () => import('@/pages/auth/WorkerRegister.vue')
      },
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    redirect:'/Login',
    children:[
      {
        path: '/Home',
        name: 'Home',
        component: () => import('@/pages/home/Home.vue'),
      },
      {
        path: '/profile',
        name: 'Profile',
        component: ()=> import('@/pages/profile/ProfileDetail.vue')
      },
      {
        path: '/postJob',
        name: 'PostJob',
        component: () => import('@/pages/job/PostJob.vue')
      },

      {
        path: '/chat',
        name: 'Chat',
        component: () => import('@/pages/chat/ChatRoom.vue')
      },
      {
        path: '/jobList',
        name: 'JobList',
        component: () => import('@/pages/job/JobDisplay.vue')
      },
      {
        path: '/statistics',
        name: 'Statistics',
        component: () => import('@/pages/statistics/Statistics.vue')
      },
      {
        path: '/analytics',
        name: 'Analytics',
        component: () => import('@/pages/analytics/Analytics.vue')
      },
      {
        path: '/task-management',
        name: 'TaskManagement',
        component: () => import('@/pages/employer/TaskManagement.vue')
      },  
    
      {
        path: '/workerList',
        name: 'WorkerList',
        component: () => import('@/pages/worker/WorkerList.vue')
      },
      {
        path: '/reviewList',
        name: 'ReviewList',
        component: () => import('@/pages/review/ReviewList.vue')
      },
   
    ]
  }
];
const router = createRouter({
  history: createWebHistory(),
  routes
});
router.beforeEach(async(to,from) => {
  if(!localStorage.token && to.name !== 'Login' && to.name!== 'EnterpriseRegister' && to.name!== 'WorkerRegister'){
    return 'Login'
  }
})
export default router;