<template>
  <v-main class="wrap">
    <v-container class="v-container">
      <v-row justify="end" class="mb-4">
        <v-col cols="auto">
          <v-btn color="primary" @click="handleClickButton">나눔 글쓰기</v-btn>
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
              상품 이름: {{ room.itemName }}
            </v-card-subtitle>
            <div style="text-align: center; margin-top: 20px">
              <img
                v-if="room.itemImagePath"
                :src="room.itemImagePath"
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
                v-if="room.itemStatus !== 'DONE'"
                color="primary"
                @click="goToDetailPage(room.id)"
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

<style scoped>
.custom-title {
  font-size: 35px;
}

.custom-subtitle {
  font-size: 18px;
}
</style>

<script>
import axios from "axios";

export default {
  data() {
    return {
      sharingRooms: [],
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
    handleClickButton() {
      if (this.isAuthenticated()) {
        this.$router.push("/make/sharingRoom");
      } else {
        alert("로그인이 필요한 서비스입니다.");
      }
    },
    async goToDetailPage(roomId) {
      if (this.isAuthenticated()) {
        this.$router.push(`/user/room/${roomId}`);
      } else {
        alert("로그인이 필요한 서비스입니다.");
      }
    },
    isAuthenticated() {
      return localStorage.getItem("Authorization") !== null;
    },
    goToPage(path) {
      this.$router.push(path);
    },
  },
  created() {
    this.loadRooms();
  },
};
</script>

<style scoped>
.wrap {
  background-image: url("../assets/background.jpg");
  background-size: cover;
  background-position: center;
  width: 100%;
  height: 100%;
}
</style>
