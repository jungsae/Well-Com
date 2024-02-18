<template>
  <v-main>
    <v-container>
      <div class="background-actions"></div>
      <transition name="custom-fade" mode="out-in">
        <div v-if="showButtons" class="button-container" key="button-container">
          <v-row>
            <v-col cols="5">
              <div class="button-wrap">
                <v-btn @click="goToPage('/tableHome')" class="custom-btn"
                >테이블 예약</v-btn
                >
              </div>
            </v-col>
            <v-col cols="2"></v-col>
            <v-col cols="5">
              <div class="button-wrap">
                <v-btn @click="goToPage('/sharingHome')" class="custom-btn"
                >물건 나눔</v-btn
                >
              </div>
            </v-col>
          </v-row>
        </div>
      </transition>
    </v-container>
  </v-main>
</template>

<script>
export default {
  data() {
    return {
      showButtons: false,
    };
  },
  mounted() {
    setTimeout(() => {
      this.showButtons = true;
    }, 700);
  },
  methods: {
    goToPage(path) {
      this.$router.push(path);
    },
  },
};
</script>

<style scoped>
.background-actions {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url("@/assets/LandingMain.gif");
  background-size: cover;
  background-position: center;
  animation: slide 10s infinite alternate;
}

@keyframes slide {
  0% {
    transform: scale(1);
  }
  100% {
    transform: scale(1.1);
  }
}

.button-container {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.button-wrap {
  transition: 2.5s;
  opacity: 1;
}

.custom-fade-enter-active .button-wrap {
  opacity: 0;
  transform: scale(1);
}

.custom-fade-enter .button-wrap {
  opacity: 0;
  transform: scale(0.5);
}

.custom-btn {
  margin: 100px 50px;
  width: 1500px;
  height: 500px;
  border-radius: 20px;
  border: 10px solid white;
  background-color: rgba(155, 255, 255, 0.3);
  color: white;

  display: flex;
  justify-content: center;
  align-items: center;

  font-size: 70px;
}
</style>
