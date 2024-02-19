<template>
  <sign-in-component v-model="signIn" value></sign-in-component>
  <sign-up-component v-model="signUp" value></sign-up-component>

  <v-navigation-drawer v-model="drawer" app right temporary color="blue-grey">
    <v-list>
      <v-list-item link to="/tableHome" @click="drawer = false">
        <v-list-item-title>테이블 예약하기</v-list-item-title>
      </v-list-item>
      <v-list-item link to="sharingHome" @click="drawer = false">
        <v-list-item-title>나눔물건보러가기</v-list-item-title>
      </v-list-item>
    </v-list>
  </v-navigation-drawer>

  <v-app-bar color="blue-grey">
    <div style="margin-left: 25px">
      <router-link to="/" class="text-decoration-none" style="color: white">
        <v-toolbar-title class="text-center flex-grow-1"
          >Well-Com</v-toolbar-title
        >
      </router-link>
    </div>

    <v-spacer></v-spacer>

    <v-btn
      v-if="$route.path !== '/tableHome'"
      text
      class="white--text d-none d-sm-flex"
      @click="goToPage('/tableHome')"
      >테이블 예약하기</v-btn
    >
    <v-btn
      v-if="$route.path !== '/sharingHome'"
      text
      class="white--text d-none d-sm-flex"
      @click="goToPage('/sharingHome')"
      >나눔 물건 보기</v-btn
    >

    <v-spacer></v-spacer>

    <v-btn
      text
      class="login-button custom-login-color"
      @mouseover="loginHover = true"
      @mouseleave="loginHover = false"
      @click.stop="signIn = true"
      :elevation="loginHover ? 10 : 2"
    >
      로그인
    </v-btn>
    <v-btn
      text
      class="signup-button custom-signup-color"
      @mouseover="signupHover = true"
      @mouseleave="signupHover = false"
      @click.stop="signUp = true"
      :elevation="signupHover ? 10 : 2"
    >
      회원가입
    </v-btn>
    <v-app-bar-nav-icon
      @click.stop="drawer = !drawer"
      class="d-flex d-sm-none"
    ></v-app-bar-nav-icon>
  </v-app-bar>
</template>

<script>
import SignInComponent from './SignInComponent.vue';
import SignUpComponent from './SignUpComponent.vue';
export default {
  data: () => ({
    loginHover: false,
    signupHover: false,
    drawer: false,
    signIn: false,
    signUp: false,
  }),
  methods: {
    goToPage(path) {
      this.$router.push(path);
    },
    toggleDrawer() {
      this.drawer = !this.drawer;
    },
  },
  components: {
    SignInComponent,
    SignUpComponent,
  }
};
</script>

<style>
@media (max-width: 1280px) {
  .v-toolbar-title {
    margin-right: auto;
    margin-left: auto;
  }
}

.custom-login-color,
.custom-signup-color {
  background-color: #8197db;
  color: #ffffff;
  margin-right: 10px;
}
</style>
