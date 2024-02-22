<template>
    <div>
    </div>
</template>

<script>
import router from "@/router";
import axios from 'axios'
import { jwtDecode } from 'jwt-decode';

export default {
    created() {
        console.log(this.$route.query)
        const access_token = this.$route.query.accessToken
        const refresh_token = this.$route.query.refreshToken
        if (access_token && refresh_token) {
            localStorage.setItem('Authorization', access_token);
            localStorage.setItem('AuthorizationRefresh', refresh_token);
            const access_decoded = jwtDecode(access_token);
            localStorage.setItem("role", access_decoded.role);
            localStorage.setItem("expiredTime", access_decoded.exp);
            window.alert('로그인이 성공적으로 이루어졌습니다.');
            history.back();
        } else {
            window.alert('로그인에 실패하였습니다.')
            router.push({ path: '/test-modal' })
        }
    },
    mounted() {
        // URL에서 인증 코드 추출
        const urlParams = new URLSearchParams(window.location.search);
        const authCode = urlParams.get('code');

        if (authCode) {
            // Axios를 사용하여 백엔드에 인증 코드 전달
            axios.post('http://localhost:8080/auth/google', { code: authCode })
                .then(response => {
                    // 백엔드에서 반환한 응답 처리  
                    console.log(response.data);
                })
                .catch(error => {
                    // 오류 처리
                    console.error('Error authenticating with Google:', error);
                });
        }
    }
}
</script>

<style></style>