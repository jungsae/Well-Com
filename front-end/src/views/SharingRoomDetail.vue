<template>
  <h2>Hi</h2>
</template>

<!--
<template>
  <div>
    <h1>Sharing Room Detail</h1>
    <p v-if="loading">Loading...</p>
    <div v-else>
      <p>Room ID: {{ room.id }}</p>
      <p>제목: {{ room.title }}</p>
      <p>내용: {{ room.contents }}</p>
      <p>인원 수: {{ room.cntPeople }}</p>
      <p>상품 이름: {{ room.itemName }}</p>
      <img
        v-if="room.itemImagePath"
        :src="room.itemImagePath"
        alt="상품 이미지"
        style="max-width: 300px"
      />
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  props: {
    id,
  },
  data() {
    return {
      room: null,
      loading: false,
    };
  },
  created() {
    this.loadRoomDetail();
  },
  methods: {
    async loadRoomDetail() {
      try {
        this.loading = true;
        const response = await axios.get(
          `${process.env.VUE_APP_API_BASE_URL}/user/room/${this.id}`,
          this.$token("members/reissue")
        );
        this.room = response.data.result;
        this.loading = false;
      } catch (error) {
        console.error("Error loading room detail:", error);
        // 에러 처리 로직 추가
      }
    },
  },
};
</script> -->
