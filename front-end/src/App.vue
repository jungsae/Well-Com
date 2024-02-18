<template>
  <v-app>
    <Header v-if="isMain"></Header>
    <router-view />
  </v-app>
</template>

<script>
import Header from "./components/MainHeader.vue";

export default {
  name: "App",
  components: {
    Header,
  },

  data: () => ({
    showHeader: true,
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
