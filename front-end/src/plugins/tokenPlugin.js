import { jwtDecode } from "jwt-decode"
import axios from "axios"

const methods = {
    async setToken(url) {
        // HEADER에 토큰 설정
        axios.defaults.headers.common['Authorization'] = `Bearer ` + localStorage.getItem('Authorization')
        // 만료시간이 지났을 경우, RefreshToken을 이용하여 AccessToken 재발급
        const expiredTime = localStorage.getItem('expiredTime');
        const now = new Date();
        const currentTime = now.getTime();
        const diffTime = (expiredTime*1000) - currentTime;
        // 토큰 만료시간 - 현재시간이 10초 이상일 경우 RefreshToken을 헤더에 넣어서 아무값 넣어서 요청
        if (diffTime < 10000){
            console.log("Token 재발급 요청");
            axios.defaults.headers.common['AuthorizationRefresh'] = `Bearer ` + localStorage.getItem('AuthorizationRefresh')
            await axios.get(process.env.VUE_APP_API_BASE_URL + url).then(
                (res) => {
                localStorage.setItem('Authorization', res.data.Authorization)
                const decoded = jwtDecode(res.data.Authorization);
                localStorage.setItem("role", decoded.role);
                localStorage.setItem('expiredTime', decoded.exp)
                axios.defaults.headers.common['Authorization'] =  localStorage.getItem('Authorization')
                },
                (err) => {
                    localStorage.clear();
                    console.log(err);
                    window.location.href = "/";
                }
            ) 
        }
    }  
}

export default {
    install(app) {
        app.config.globalProperties.$token = methods.setToken;
    }
}