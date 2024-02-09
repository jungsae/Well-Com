import { createApp } from 'vue'
import App from './App.vue'
import router from '@/router/index.js'
import axios from 'axios';

const app = createApp(App);
axios.interceptors.response.use(response => response, error => {
    if(error.response && error.response.status === 401){
        localStorage.clear();
        window.location.href = "/login";
    }
    return Promise.reject(error);
})
app.config.globalProperties.$axios = axios
app.use(router);
app.mount('#app');