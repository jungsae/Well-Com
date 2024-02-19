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
      this.isMain = newVal !== "/";
    },
  },
};
</script>

<style></style>
