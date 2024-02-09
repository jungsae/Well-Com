import {createRouter, createWebHistory} from 'vue-router';

const routes = [
    {
        path: '/',
        name: 'home',
        component: ()=>import('@/components/HelloWorld.vue'),
    },
    {
        path: '/login',
        name: 'Login',
        component: ()=>import('@/components/LoginComponent.vue'),
    },
    {
        path: '/test-modal',
        name: 'ModalTest',
        component: ()=>import('@/components/ParentComponent.vue'),
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;