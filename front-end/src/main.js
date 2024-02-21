import { createApp } from "vue";
import App from "./App.vue";
import router from "@/router/index.js";
import vuetify from "./plugins/vuetify";
import token from "./plugins/tokenPlugin.js";
import { loadFonts } from "./plugins/webfontloader";
import axios from "axios";

loadFonts();

const app = createApp(App);

axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.clear();
      history.back();
    }
    return Promise.reject(error);
  }
);

app.use(router);
app.use(vuetify);
app.use(token);
app.mount("#app");
