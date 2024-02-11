<template>
  <v-dialog transition="slide-y-transition" v-model="dialog" max-width="450px">
      <v-card class="blue-lighten-1 pa-12" rounded>
       <!-- <v-card class="mx-auto px-6 py-8" max-width="344"> -->
        <v-card-title>
          <span class="headline">로그인</span>
        </v-card-title>
        <v-form v-model="form" @submit.prevent="onSubmit">
          <v-text-field
              v-model="email"
              :readonly="loading"
              :rules="[required]"
              class="mb-2"
              clearable
              label="Email"
              placeholder="encore@wellbeing.com"
          ></v-text-field>
          <v-text-field
              v-model="password"
              :readonly="loading"
              :rules="[required]"
              clearable
              type="password"
              label="Password"
              placeholder="Enter your password"
          ></v-text-field>
          <v-btn @click="signInWithGoogle" dark id="google-connect" class="social-button">
            <v-icon left>mdi mdi-google</v-icon>
            <span class="text">구글 계정으로 로그인</span>
          </v-btn>
          <br/>
          <v-btn
              :disabled="!form"
              :loading="loading"
              block
              color="blue-accent-2"
              size="large"
              type="submit"
              variant="elevated"
          >Sign In</v-btn>
        </v-form>
        <v-spacer></v-spacer>
        <small>아직 회원이 아니신가요? <a href="#"><b>회원가입</b></a></small>
      </v-card>
  </v-dialog>
</template>

<script>
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

export default {
  props: {
    value: {
      type: Boolean,
      required: true,
    },
  },
  data() {
    return {
      form: false,
      email: "",
      password: "",
      loading: false,
    }
  },
  watch: {
    dialog: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('input', value);
      },
    },
  },
  methods: {
    required (v) {
      return !!v || '칸이 비어있습니다.'
    },
    async onSubmit () {
      if (!this.form) return;

      this.loading = true;
      await this.doLogin();
      this.loading = false;
    },
    async doLogin(){
      try{
          const loginData = {email: this.email, password: this.password }
          const response =  await axios.post(`${process.env.VUE_APP_API_BASE_URL}/login`, loginData);
          const access_token = response.data.Authorization;
          const refresh_token = response.data.AuthorizationRefresh;
          if (access_token && refresh_token) {
              const access_decoded = jwtDecode(access_token);
              localStorage.setItem("role", access_decoded.role);
              localStorage.setItem("Authorization", access_token);
              localStorage.setItem("AuthorizationRefresh", refresh_token);
              window.location.href = "/test-modal";
          }else{
              console.log("200 OK but not token");
              alert("Login Failed")
          }
      }catch(error){
          const error_message = error.response.data.error_message;
          if(error_message){
              console.log(error_message);
              alert(error_message);
          }else{
              console.log(error);
              alert("Login Failed")
          }
      }
    },
    async signInWithGoogle() {
      try {
        await axios.get('http://localhost:8080/oauth2/authorization/google');

        // window.location.href = response.data.redirectUrl;
      } catch (error) {
        console.error('OAuth 플로우 시작 중 오류 발생:', error);
      }
    }
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap');

  a {
    color: black;
    text-decoration-line: none;
  }
  a:hover{
    text-decoration-line: underline;
  }

  .social-button:hover {
    box-shadow: 0 8px 10px 0 rgba(0,0,0,0.2),0 6px 20px 0 rgba(0,0,0,0.19);
  }

  .text {
    margin-left: 8px;
    display: inline-flex;
    align-items: center;
    justify-content: flex-start;
    font-family: 'Roboto', sans-serif;
  }

  #google-connect {
    height: 40px; /* 버튼의 높이를 조절합니다. */
    color: rgb(220, 74, 61); /* 텍스트 색상 */
    text-transform: uppercase;
    width: 100%;
    border-radius: 3px;
    margin: 10px auto;
    display: flex; /* 텍스트를 가운데 정렬하기 위해 Flexbox를 사용합니다. */
    align-items: center; /* 수직 가운데 정렬을 설정합니다. */
    font-family: 'Roboto', sans-serif; /* Roboto 폰트를 적용합니다. */
  }

  #google-connect {
    background-color: white; /* 기본 배경색 */
    border: 1px solid rgb(220, 74, 61);
  }
  
  #google-connect:hover {
    background-color: rgb(220, 74, 61);
    color: #FFF;
  }
</style>