<template>
  <v-main class="wrap">
    <v-container class="v-container">
      <v-row justify="center">
        <v-col cols="12" sm="6" v-for="n in 4" :key="n">
          <v-card>
            <v-img
              :height="450"
              :width="2000"
              aspect-ratio="16/9"
              src="../assets/table.png"
              alt="이미지 준비중"
            ></v-img>
            <v-card-title>{{ tables[n - 1].description }}</v-card-title>
            <v-card-actions>
              <v-btn :to="tables[n - 1].link" color="primary">예약하기</v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-main>
</template>
<script>
export default {
  data() {
    return {
      tables: [
        {
          description: "테이블 1 설명",
          link: "/reservation/1",
        },
        {
          description: "테이블 2 설명",
          link: "/reservation/2",
        },
        {
          description: "테이블 3 설명",
          link: "/reservation/2",
        },
        {
          description: "테이블 4 설명",
          link: "/reservation/2",
        },
      ],
    };
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

.v-container {
  margin: auto;
}
</style>
