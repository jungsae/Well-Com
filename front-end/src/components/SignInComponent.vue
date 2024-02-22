<template>
  <v-dialog transition="slide-y-transition" v-model="dialog" max-width="450px">
    <v-card class="blue-lighten-1 pa-12 rounded-xl">
      <!-- <v-card class="mx-auto px-6 py-8" max-width="344"> -->
      <v-card-title>
        <span class="headline">로그인</span>

      </v-card-title>
      <v-form v-model="form" @submit.prevent="onSubmit">
        <v-text-field v-model="email" :readonly="loading" :rules="user_email_rule" class="mb-2" clearable label="이메일"
          placeholder="encore@wellbeing.com"></v-text-field>
        <v-text-field v-model="password" :readonly="loading" :rules="user_pw_rule" clearable :type="'password'"
          class="password-input" label="비밀번호" placeholder="Enter your password"></v-text-field>
        <v-btn v-on:click.prevent="signInWithGoogle" dark id="google-connect" class="social-button">
          <v-icon left>mdi mdi-google</v-icon>
          <span class="text">구글 계정으로 로그인</span>
        </v-btn>
        <br />
        <v-btn :disabled="!form" :loading="loading" block color="#8197db" size="large" type="submit"
          variant="elevated" style="margin-bottom: 5px;">로그인</v-btn>
      </v-form>
      <div class="text-right">
        <small>비밀번호를 잊어버리셨나요? <a href="#"><b>비밀번호찾기</b></a></small>
      </div>
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
      email: "",
      user_email_rule: [
        v => !!v || '입력이 비어있습니다.',
        v => /.+@.+\..+/.test(v) || '유효한 이메일 주소를 입력해주세요.',
      ],
      password: "",
      user_pw_rule: [
        v => !!v || '입력이 비어있습니다.',
        v => !(v && v.length >= 30) || '비밀번호는 30자 이상 입력할 수 없습니다.',
      ],
      form: false,
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
    async onSubmit() {
      if (!this.form) return;

      this.loading = true;
      await this.doLogin();
      this.loading = false;
    },
    async doLogin() {
      try {
        const loginData = { email: this.email, password: this.password }
        const response = await axios.post(`${process.env.VUE_APP_API_BASE_URL}/login`, loginData);
        console.log(response);
        const access_token = response.data.Authorization;
        const refresh_token = response.data.AuthorizationRefresh;
        if (access_token && refresh_token) {
          const access_decoded = jwtDecode(access_token);
          localStorage.setItem("role", access_decoded.role);
          localStorage.setItem("expiredTime", access_decoded.exp);
          localStorage.setItem("Authorization", access_token);
          localStorage.setItem("AuthorizationRefresh", refresh_token);
          window.location.reload();
        } else {
          console.log("200 OK but not token");
          alert("Login Failed")
        }
      } catch (error) {
        const error_message = error.response.data.error_message;
        if (error_message) {
          console.log(error_message);
          alert(error_message);
        } else {
          console.log(error);
        }
      }
    },
    signInWithGoogle() {
      window.location.href = `${process.env.VUE_APP_API_BASE_URL}/oauth2/authorize/google?redirect_uri=http://localhost:8081/oauth2/redirect`;
    },
  }
};
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap');

.v-card--shaped {
  border-radius: 24px;
}

.password-input input {
  font-family: sans-serif;
}

a {
  color: black;
  text-decoration-line: none;
}

a:hover {
  text-decoration-line: underline;
}

.social-button:hover {
  box-shadow: 0 8px 10px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
}

.text {
  margin-left: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: flex-start;
}

#google-connect {
  height: 40px;
  /* 버튼의 높이를 조절합니다. */
  color: rgb(220, 74, 61);
  /* 텍스트 색상 */
  text-transform: uppercase;
  width: 100%;
  border-radius: 3px;
  margin: 10px auto;
  display: flex;
  /* 텍스트를 가운데 정렬하기 위해 Flexbox를 사용합니다. */
  align-items: center;
  /* 수직 가운데 정렬을 설정합니다. */
}

#google-connect {
  background-color: white;
  /* 기본 배경색 */
  border: 1px solid rgb(220, 74, 61);
}

#google-connect:hover {
  background-color: rgb(220, 74, 61);
  color: #FFF;
}
</style>