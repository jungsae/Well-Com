<template>
  <v-dialog transition="slide-y-transition" v-model="dialog" max-width="450px" class="dialog-container">
    <v-card class="blue-lighten-1 pa-12 rounded-xl">
      <v-card-title class="d-flex justify-between">
        <span class="headline">회원가입</span>
      </v-card-title>
      <v-form ref="form" lazy-validation @submit.prevent="onSubmit">
        <v-row>
          <!-- <v-col cols="12">
            <v-text-field v-model="email" :readonly="loading" :rules="[required]" class="mb-2" clearable label="Email"
            placeholder="encore@wellbeing.com"></v-text-field>
          </v-col> -->
          <v-col cols="12">
            <v-text-field
            v-model="email"
            :rules="user_email_rule"
            color="primary"
            label="이메일"
            placeholder="Enter your password"
            variant="underlined"
          ></v-text-field>

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
          const params = {
            email: this.email,
            password: this.password,
          }
          try{
            await axios.post(`${process.env.VUE_APP_API_BASE_URL}/sign-up`, params);
            alert("회원가입이 정상적으로 이루어졌습니다.")
            window.location.reload();
          } catch(err){
            alert(err);
          } 
        }
      });
    },
  }
};
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
</style>