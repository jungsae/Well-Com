<template>
  <v-main class="wrap">
    <v-container class="v-container">
      <v-row justify="end" class="mb-4">
        <v-col cols="auto">
          <v-btn color="primary" @click="checkAuth">나눔 글쓰기</v-btn>
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col cols="12">
          <v-card v-for="room in sharingRooms" :key="room.id" class="mb-4">
            <v-card-title
              class="text-center custom-title"
              style="margin-top: 20px"
              >{{ room.title }}</v-card-title
            >
            <v-card-subtitle class="text-center custom-subtitle">
              작성자: {{ room.memberEmail }}
            </v-card-subtitle>
            <v-card-subtitle class="text-center custom-subtitle">
              상품 이름: {{ room.itemName }}
            </v-card-subtitle>
            <v-card-subtitle class="text-center custom-subtitle">
              내용: {{ room.contents }}
            </v-card-subtitle>
            <div style="text-align: center; margin-top: 20px">
              <img
                v-if="room.itemImagePath"
                :src="s3BucketUrl + room.itemImagePath"
                :style="{
                  maxWidth: '300px',
                  filter:
                    room.itemStatus === 'DONE' ? 'brightness(70%)' : 'none',
                }"
                alt="상품 이미지"
              />
            </div>
            <v-card-subtitle class="text-center custom-subtitle">
              나눔 상태:
              <span
                :style="{
                  color:
                    room.itemStatus === 'SHARING' ? 'deeppink' : 'darkpink',
                }"
                >{{
                  room.itemStatus === "SHARING" ? "나눔중" : "나눔 완료"
                }}</span
              >
            </v-card-subtitle>
            <div
              v-if="room.itemStatus !== 'DONE'"
              style="margin-top: 20px; text-align: center"
            >
              <v-card-subtitle class="text-center custom-subtitle">
                현재인원 / {{ room.cntPeople }}</v-card-subtitle
              >
            </div>
            <div style="margin-top: 20px; text-align: center">
              <v-btn
                v-if="
                  getTokenEmail() === room.memberEmail &&
                  room.itemStatus !== 'DONE'
                "
                color="primary"
                @click="goToUpdateSharingRoom(room.id)"
                style="margin-right: 10px"
                >수정하기</v-btn
              >
              <v-btn
                v-if="
                  getTokenEmail() === room.memberEmail &&
                  room.itemStatus !== 'DONE'
                "
                color="primary"
                @click="deleteSharingRoom(room.id)"
                >삭제하기</v-btn
              >
              <v-btn
                v-else-if="room.itemStatus !== 'DONE'"
                color="primary"
                @click="goToNaNumRoom(room.id)"
                >선착순 나눔받기</v-btn
              >
            </div>
            <div style="margin-bottom: 20px"></div>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-main>
</template>

<script>
import axios from "axios";
import { jwtDecode } from "jwt-decode";

export default {
  data() {
    return {
      sharingRooms: [],
      s3BucketUrl: "https://mywellcombucket.s3.ap-northeast-2.amazonaws.com/",
    };
  },
  methods: {
    async loadRooms() {
      try {
        const response = await axios.get(
          `${process.env.VUE_APP_API_BASE_URL}/rooms`
        );
        this.sharingRooms = response.data;
        console.log(this.sharingRooms);
      } catch (error) {
        console.error("Error fetching sharing rooms:", error);
      }
    },
    checkAuth() {
      if (this.isAuthenticated()) {
        this.$router.push("/make/sharingRoom");
      } else {
        alert("로그인이 필요한 서비스입니다.");
      }
    },
    async goToNaNumRoom(roomId) {
      if (this.isAuthenticated()) {
        this.$router.push(`/user/nanumGame/${roomId}`);
      } else {
        alert("로그인이 필요한 서비스입니다.");
      }
    },
    goToUpdateSharingRoom(roomId) {
      this.$router.push(`/user/room/${roomId}`);
    },
    async deleteSharingRoom(roomId) {
      if (confirm("정말로 삭제하시겠습니까?")) {
        try {
          await axios.delete(
            `${process.env.VUE_APP_API_BASE_URL}/user/room/${roomId}/delete`,
            this.$token("members/reissue")
          );
          alert("나눔글 삭제 성공!");
          window.location.reload();
        } catch (error) {
          alert("나눔글 삭제 실패!");
          console.log(error);
        }
      }
    },
    isAuthenticated() {
      return localStorage.getItem("Authorization") !== null;
    },
    getTokenEmail() {
      const token = localStorage.getItem("Authorization");
      if (token) {
        const decodedToken = jwtDecode(token);
        const userEmail = decodedToken.email;
        return userEmail;
      }
      return null;
    },
  },
  created() {
    this.loadRooms();
  },
};
</script>

<style scoped>
.custom-title {
  font-size: 35px;
}

.custom-subtitle {
  font-size: 18px;
}
</style>
