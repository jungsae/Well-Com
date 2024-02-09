import {createRouter, createWebHistory} from 'vue-router';

const routes = [
    {
        path: '/',
        name: 'home',
        component: ()=>import('@/components/HomeComponent.vue'),
    },
    {
        path: '/login',
        name: 'Login',
        component: ()=>import('@/components/LoginComponent.vue'),
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;