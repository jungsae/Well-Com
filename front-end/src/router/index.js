import { createRouter, createWebHistory } from 'vue-router';

const routes = [
    // {
    //     path: '/',
    //     name: 'home',
    //     component: ()=>import('@/components/HelloWorld.vue'),
    // },
    {
        path: '/test-modal',
        name: 'ModalTest',
        component: () => import('@/components/ParentComponent.vue'),
    },
    {
        path: '/oauth2/redirect',
        component: () => import('@/components/GoogleRedirect.vue'),
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;