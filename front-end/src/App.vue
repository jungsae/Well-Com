<template>
  <v-app>
    <Header v-if="isMain"></Header>
    <router-view />
    <!-- <Footer v-if="isMain"></Footer> -->
  </v-app>
</template>

<script>
import Header from "./components/MainHeader.vue";
// import Footer from "./components/MainFooter.vue";

export default {
  name: "App",
  components: {
    Header,
    // Footer,
  },

  data: () => ({
    showHeader: true,
    showFooter: true,
    isMain: false,
  }),
  computed: {
    currentPath() {
      return this.$route.path;
    },
  },
  watch: {
    currentPath(newVal) {
      // 메인 페이지 또는 /admin/home 페이지가 아닐 때만 헤더와 푸터를 보이게 함
      this.isMain = newVal !== "/" && newVal !== "/admin/home" ;
    },
  },
};
</script>

<style>
@font-face {
  font-family: "jua";
  src: url(../public/font/BMJUA_ttf.ttf);
}

* {
  font-family: "jua";
}
</style>
