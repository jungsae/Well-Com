
import AdminHomeForm from '@/components/AdminHomeForm';

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: '/admin/home',
      name: 'AdminHomeForm',
      component: AdminHomeForm,
    },
    // 기타 경로 설정
  ],
});

