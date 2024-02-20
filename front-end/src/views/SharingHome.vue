<template>
  <v-main class="wrap">
    <v-container class="v-container">
      <v-row justify="end" class="mb-4">
        <v-col cols="auto">
          <v-btn color="primary" @click="goToPage('/make/sharingRoom')"
            >나눔 글쓰기</v-btn
          >
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col cols="12">
          <v-card v-for="room in sharingRooms" :key="room.id" class="mb-4">
            <v-card-title class="text-center">{{ room.title }}</v-card-title>
            <v-card-subtitle class="text-center"
              >작성자: {{ room.memberId }}</v-card-subtitle
            >
            <v-card-subtitle class="text-center"
              >상품 이름: {{ room.itemName }}</v-card-subtitle
            >
            <div style="text-align: center">
              <img
                v-if="room.itemImagePath"
                :src="room.itemImagePath"
                alt="상품 이미지"
                style="max-width: 300px"
              />
            </div>
            <v-card-subtitle class="text-center"
              >나눔 상태: {{ room.itemStatus }}</v-card-subtitle
            >
            <v-card-text class="text-center"
              >내용 : {{ room.contents }}</v-card-text
            >
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-main>
</template>

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
    removeQuotes(value) {
      return value.replace(/"/g, "");
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
