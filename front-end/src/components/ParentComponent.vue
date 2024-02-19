<template>
  <div>
    <v-btn color="accent" large @click.stop="signin = true">Sign in</v-btn>
    <v-btn color="accent" large @click.stop="signup = true">Sign up</v-btn>
    <v-btn color="accent" large @click.prevent="reissue">ReIssue 테스트</v-btn>
    <v-btn color="accent" large @click.prevent="socketConnect">웹 소켓 연결</v-btn>
    <SignInComponent v-model="signin" value></SignInComponent>
   <SignUpComponent v-model="signup" value></SignUpComponent>
  </div>
</template>

<script>
import SignInComponent from '@/components/SignInComponent.vue';
import SignUpComponent from "@/components/SignUpComponent.vue";
import axios from 'axios';

export default {
  data() {
    return {
      signin: false,
      signup: false,
    };
  },
  components: {
    SignInComponent,
    SignUpComponent
  },
  methods: {
    async reissue() {
      await axios.get('http://localhost:8080/members/ping', this.$token('/members/reissue'));
    },
    socketConnect() {
      
    }
  }
};
</script>