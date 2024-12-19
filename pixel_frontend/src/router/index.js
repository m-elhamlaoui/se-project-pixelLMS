import { createRouter, createWebHistory } from 'vue-router'; // Import createWebHistory
import HomeView from '../views/GeneralViews/HomeView.vue';
import ProfileView from '@/views/GeneralViews/ProfileView.vue';
import CalendarView from '@/views/GeneralViews/CalendarView.vue';

import CourseView from '../views/CourseViews/CourseView.vue';
import TasksView from '@/views/CourseViews/TasksView.vue';

import {getUser, isLogged} from '@/modules/auth';

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/profile/:id',
    name: 'profile',
    component: ProfileView
  },
  {
    path: '/course/:id',
    name: 'course',
    component: CourseView
  },
  {
    path: '/calendar',
    name: 'calendar',
    component: CalendarView
  },
  {
    path: '/tasks/:id',
    name: 'tasks',
    component: TasksView
  },
  {
    path: '/discussion/:id',
    name: 'discussions',
    component: () => import('../views/CourseViews/DiscussionsView.vue')
  },
  {
    path: '/auth',
    name: 'auth',
    component: () => import('../views/AuthViews/AuthView.vue') //import('../views/docs.vue')
  },
  {
    path: '/logout',
    name: 'logout',
    component: () => import('../views/AuthViews/LogoutView.vue')
  },
  {
    path: '/admin',
    name: 'admin',
    component: () => import('../views/Admin/AdminView.vue')
  }
];

const router = createRouter({
  history: createWebHistory(), 
  routes
});


router.beforeEach(async (to, from, next) => {

  if (to.name === 'admin') {
    if (!isLogged()) {
      return next('/auth');
    }

    try {
      const user = await getUser();
      if (user.role.toLowerCase() !== 'student') {
        next();
      } else {
        next('/');
      }
    } catch (error) {
      next('/');
    }
  } else {

    if (to.name === 'tasks') {
      const id = to.params.id;
      if (localStorage.getItem("course") !== id) {
        return next('/');
      }
    }

    if (to.name !== 'auth' && !isLogged()) {
      next('/auth');
    } else if (to.name === 'auth' && isLogged()) {
      next('/');
    } else {
      next();
    }
  }
});



export default router;
