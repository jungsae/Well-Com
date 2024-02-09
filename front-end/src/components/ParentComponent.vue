<template>
  <div>
    <v-btn color="accent" large @click.stop="signIn = true">Sign in</v-btn>
    <v-btn color="accent" large @click.stop="signUp = true">Sign up</v-btn>
    <SignInComponent v-model="signIn"></SignInComponent>
    <SignUpComponent v-model="signUp"></SignUpComponent>
  </div>
</template>

<script>
import SignInComponent from '@/components/SignInComponent.vue';
import SignUpComponent from "@/components/SignUpComponent.vue";

export default {
  data() {
    return {
      signIn: false,
      signUp: false,
    };
  },
  components: {
    SignInComponent,
    SignUpComponent
  },
};
</script>