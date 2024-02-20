import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import vuetify from './plugins/vuetify'
import token from './plugins/tokenPlugin.js'
import { loadFonts } from './plugins/webfontloader'
import 'vuetify/dist/vuetify.min.css';
import 'vuetify/styles'

loadFonts()

createApp(App)
  .use(router)
  .use(vuetify)
  .use(token)
  .mount('#app')
