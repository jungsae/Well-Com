
import AdminHomeForm from '@/views/me';

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: '/mypage',
      name: 'mypage',
      component: me,
    },
    // 기타 경로 설정
  ],
});