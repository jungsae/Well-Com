import { createRouter, createWebHistory } from 'vue-router';

const routes = [
    {
        path: '/',
        name: 'MainLanding',
        component: () => import('@/views/MainLanding.vue'),
    },
    {
        path: '/tableHome',
        name: 'TableHome',
        component: () => import('@/views/TableHome.vue'),
    },
    {
        path: '/sharingHome',
        name: 'SharingHome',
        component: () => import('@/views/SharingHome.vue'),
    },
    {
        path: '/adminHome',
        name: 'AdminHome',
        component: () => import('@/views/AdminHome.vue'),
    },
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