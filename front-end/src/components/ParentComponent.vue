<template>
  <div style="margin-top: 10%;">
    <v-btn color="accent" large @click.stop="signin = true">Sign in</v-btn>
    <v-btn color="accent" large @click.stop="signup = true">Sign up</v-btn>
    <v-btn color="accent" large @click.prevent="reissue">ReIssue 테스트</v-btn>
    <button @click="connect">웹 소켓 연결</button>
    <div v-if="connected">
      연결됨
    </div>
    <SignInComponent v-model="signin" value></SignInComponent>
   <SignUpComponent v-model="signup" value></SignUpComponent>
  </div>
</template>

<script>
import SignInComponent from '@/components/SignInComponent.vue';
import SignUpComponent from "@/components/SignUpComponent.vue";
import Stomp from 'stompjs'
import SockJS from 'sockjs-client'

export default {
  data() {
    return {
      signin: false,
      signup: false,
      connected: false,
    };
  },
  created() {
    this.connect();
  },
  components: {
    SignInComponent,
    SignUpComponent
  },
  methods: {
    connect() {
      // 웹 소켓 서버 URL
      const serverURL = "http://localhost:8080/portfolio";

      // SockJS로 서버에 연결
      let socket = new SockJS(serverURL);

      // Stomp 클라이언트 생성
      this.stompClient = Stomp.over(socket);

      console.log(`소켓 연결을 시도합니다. 서버 주소: ${serverURL}`);

      // Stomp 클라이언트로 서버에 연결
      const token = localStorage.getItem('Authorization');
      const headers = token ? {Authorization: `Bearer ${token}`} : {};
      this.stompClient.connect(headers, frame => {
        console.log('소켓 연결 성공', frame);
        this.connected = true;

        // 메시지 구독 예시
        this.stompClient.subscribe('/topic/greeting', message => {
        console.log('메시지 수신', message);
        // 여기서 받은 메시지를 처리할 수 있습니다.
        });
      }, error => {
        console.log('소켓 연결 실패', error);
        this.connected = false;
      });
    },
  }
};
</script>