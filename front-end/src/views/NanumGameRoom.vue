<template>
  <v-container style="margin-top: 10%">
    <v-text-field v-model="messageToReceived" disabled></v-text-field>
    <v-text-field v-model="messageToSend"></v-text-field>
    <v-btn color="accent" large @click.prevent="sendMessage">메시지 전송</v-btn>
  </v-container>
</template>

<script>
import Stomp from "stompjs";
import SockJS from "sockjs-client";

export default {
  props: ["id"],
  data() {
    return {
      connected: false,
      messageToSend: "",
      messageToReceived: "",
    };
  },
  created() {
    this.connect();
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
      const token = localStorage.getItem("Authorization");
      const headers = token ? { Authorization: `Bearer ${token}` } : {};
      const destination = `/topic/sharing/${this.id}`;
      this.stompClient.connect(
        headers,
        (frame) => {
          console.log("소켓 연결 성공", frame);
          this.connected = true;

          // 메시지 구독
          this.stompClient.subscribe(destination, (message) => {
            console.log("메시지 수신", message);
            try {
              // 서버에서 전송한 JSON 형식의 메시지를 객체로 파싱하여 messageToReceived 변수에 할당
              this.messageToReceived = JSON.parse(message.body);
            } catch (error) {
              console.error(
                "수신한 메시지가 유효한 JSON 형식이 아닙니다:",
                error
              );
              // JSON 형식이 아니라면 그대로 메시지를 처리합니다.
              this.messageToReceived = message.body;
            }
          });
        },
        (error) => {
          console.log("소켓 연결 실패", error);
          this.connected = false;
        }
      );
    },
    disconnect() {
      if (this.stompClient && this.stompClient.connected) {
        this.stompClient.disconnect(); // Stomp 클라이언트 연결 해제
        console.log("소켓 연결이 해제되었습니다.");
        this.connected = false;
      } else {
        console.log("소켓 연결이 이미 해제되었거나 없습니다.");
      }
    },
    sendMessage() {
      if (this.connected && this.messageToSend.trim() !== "") {
        // this.messageToSend를 소켓을 통해 전송하는 로직
        console.log("전송할 메시지:", this.messageToSend);
        // WebSocket 클라이언트로 메시지 전송
        this.stompClient.send(
          "/app/sendMessage",
          {
            destination: "/app/sendMessage",
          },
          JSON.stringify({
            token: localStorage.getItem("Authorization"),
            content: this.messageToSend,
            roomId: `${this.id}`,
          })
        );
        // 메시지를 전송한 후, 텍스트 필드를 비워줍니다.
        this.messageToSend = "";
      } else {
        console.log("연결되지 않았거나 빈 메시지입니다.");
      }
    },
  },
};
</script>
