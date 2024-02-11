<template>
  <div>
    <v-btn color="accent" large @click.stop="dialog = true">Sign in</v-btn>
    <v-btn color="accent" large @click.stop="dialog = true">Sign up</v-btn>
    <SignInComponent v-model="dialog" value></SignInComponent>
<!--    <SignUpComponent v-model="dialog" value></SignUpComponent>-->
  </div>
</template>

<script>
import SignInComponent from '@/components/SignInComponent.vue';
// import SignUpComponent from "@/components/SignUpComponent.vue";

export default {
  data() {
    return {
      dialog: false,
    };
  },
  components: {
    SignInComponent,
    // SignUpComponent
  },
};
</script>