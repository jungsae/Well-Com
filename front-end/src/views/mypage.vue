<template>
    <v-container>
      <v-row justify="center">
        <v-col cols="12" sm="8" md="6">
          <v-card>
            <v-card-title class="text-h5">마이페이지</v-card-title>
            <v-card-text>
              <v-form ref="form" v-model="valid" lazy-validation>
                <v-text-field
                  v-model="name"
                  :rules="nameRules"
                  label="이름"
                  required
                ></v-text-field>
                <v-text-field
                  v-model="email"
                  :rules="emailRules"
                  label="이메일"
                  required
                ></v-text-field>
              </v-form>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="primary" @click="submit">변경 사항 저장</v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </template>
  
  <script>
  export default {
    name: 'MyPage',
    data: () => ({
      valid: false,
      name: '',
      nameRules: [
        v => !!v || '이름을 입력해주세요.',
      ],
      email: '',
      emailRules: [
        v => !!v || '이메일 주소를 입력해주세요.',
        v => /.+@.+\..+/.test(v) || '유효한 이메일 주소를 입력해주세요.',
      ],
    }),
    methods: {
      submit() {
        if (this.$refs.form.validate()) {
          // 폼 데이터 제출 로직
          alert('변경 사항이 저장되었습니다.');
        }
      },
    },
  };
  </script>
  