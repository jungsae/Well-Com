<template>
  <v-dialog transition="slide-y-transition" v-model="dialog" max-width="450px" class="dialog-container">
    <v-card class="blue-lighten-1 pa-12 rounded-xl">
      <v-card-title class="d-flex justify-between">
        <span class="headline">회원가입</span>
      </v-card-title>
      <v-form ref="form" lazy-validation @submit.prevent="onSubmit">
        <v-row>
          <v-col cols="12">
            <v-text-field
            v-model="email"
            :rules="user_email_rule"
            color="primary"
            label="이메일"
            placeholder="Enter your password"
            variant="underlined"
          ></v-text-field>
          <v-row>
            <v-col cols="12" sm="5">
              <transition name="fade">
                <v-btn :disabled="mail_btn" :loading="loading" color="accent" style="margin-top: 20px" large @click.prevent="sendEmail()">인증번호 발송</v-btn>
              </transition>
            </v-col>
            <v-col cols="12" sm="6">  
              <transition name="x-slide">
                <v-text-field
                v-model="keyValue"
                v-if="viewkeyValue"
                :rules="mail_key_rule"
                style="margin-top: 10px"
                color="primary"
                label="인증번호"
                placeholder="인증번호를 입력해주세요."
                variant="underlined"
              ></v-text-field>
            </transition>
            </v-col>
          </v-row>
          </v-col>
          <v-col cols="12">
            <v-text-field
            v-model="password"
            :rules="user_pw_rule"
            clearable
            color="primary"
            label="비밀번호"
            :type="'password'"
            class="password-input"
            placeholder="Enter your password"
            maxlength="30"
            variant="underlined"
          ></v-text-field>
          <v-text-field
          v-model="passwordCheck"
          :rules="pw_check_rule"
          clearable
          color="primary"
          label="비밀번호 확인"
          :type="'password'"
          class="password-input"
          placeholder="Enter your password"
          maxlength="30"
          variant="underlined"
        ></v-text-field>
          </v-col>
        </v-row>
        <br />
        <v-btn :disabled="form" block color="#8197db" size="large" type="submit"
          variant="elevated" style="margin-bottom: 5px;">회원가입</v-btn>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<script>
import axios from 'axios';

export default {
  props: {
    value: {
      type: Boolean,
      required: true,
    },
  },
  data() {
    return {
      loading: false,
      mail_btn: false,
      form: false,
      email: "",
      user_email_rule: [
        v => !!v || '이메일은 필수 입력사항입니다.',
        v => /.+@.+\..+/.test(v) || '유효한 이메일 주소를 입력해주세요.',
      ],
      password: "",
      passwordCheck: "",
      user_pw_rule: [
        v => !!v || '비밀번호는 필수 입력사항입니다.',
        v => !(v && v.length >= 30) || '비밀번호는 30자 이상 입력할 수 없습니다.',
      ],
      pw_check_rule: [
        v => !!v || '비밀번호는 필수 입력사항입니다.',
        v => !(v && v.length >= 30) || '비밀번호는 30자 이상 입력할 수 없습니다.',
        v => v === this.password || '비밀번호가 일치하지 않습니다.'
      ],
      key: "",
      keyValue: "",
      viewkeyValue: false,
      mail_key_rule: [
        v => v === this.key || "인증번호가 일치하지 않습니다."
      ]
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
      const validate = this.$refs.form.validate();
      const object = Promise.resolve(validate); 
      object.then(async (value) => {
        if(value.valid){
          if(!this.mail_btn){
            alert("이메일 인증을 해주세요.");
            return; 
          }
          const params = {
            email: this.email,
            password: this.password,
          }
          try{
            await axios.post(`${process.env.VUE_APP_API_BASE_URL}/sign-up`, params);
            alert("회원가입이 정상적으로 이루어졌습니다.")
            window.location.reload();
          } catch(err){
            if(err.response.status === 400){
              alert("이미 등록된 이메일입니다.")
            }
          } 
        }
      });
    },
    async sendEmail() {
      if(this.email != ""){
        this.loading = true;
        await axios.post(`${process.env.VUE_APP_API_BASE_URL}/send-email?email=${this.email}`)
          .then((res) => {
            this.key = res.data.result;
            this.mail_btn = true;
            this.viewkeyValue = true;
          });
          this.loading = false;
        } else {
          alert("유효한 이메일을 입력해주세요.");
        }
      }
    },
  }
</script>

<style>

.card-container {
  position: relative;
}

.v-card-title {
  display: flex;
  justify-content: space-between;
}

.v-card--shaped {
  border-radius: 24px;
}

.password-input input {
  font-family: sans-serif;
}

.text {
  margin-left: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: flex-start;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter, .fade-leave-to{
  opacity: 0;
}

.x-slide-enter-active, .x-slide-leave-active {
  transition: transform 0.5s ease;
}
.x-slide-enter, .x-slide-leave-to {
  transform: translateX(100%);
}
</style>