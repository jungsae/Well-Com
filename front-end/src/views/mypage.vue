<template>
  <v-app>
    <!-- Main Content -->
    <v-row>
      <!-- Side Bar -->
      <v-col cols="12" md="3">
        <v-navigation-drawer
          app
          dark
          class="grey darken-4"
          :mobile-break-point="1"
        >
          <!-- 사이드 바의 내용 -->
          <v-list dense class="grey darken-4 white--text">
            <v-list-item @click="$router.push('/')">
              <v-list-item-icon>
                <v-icon class="white--text">mdi-home</v-icon>
              </v-list-item-icon>
              <v-list-item-content>
                <v-list-item-title class="sidebar-title">Home</v-list-item-title>
              </v-list-item-content>
            </v-list-item>

            <v-list-item-group>
              <v-list-item @click="toggleCategory">
                <v-list-item-icon>
                  <v-icon class="white--text">mdi-format-list-bulleted</v-icon>
                </v-list-item-icon>
                <v-list-item-content>
                  <v-list-item-title class="sidebar-title">Category</v-list-item-title>
                </v-list-item-content>
                <v-icon v-if="showCategory" class="white--text">mdi-chevron-up</v-icon>
                <v-icon v-else class="white--text">mdi-chevron-down</v-icon>
              </v-list-item>

              <template v-if="showCategory">
                <!-- 테이블 예약 아이콘 -->
                <v-list-item @click="$router.push('/tableHome')">
                  <v-list-item-icon>
                    <v-icon class="white--text">mdi-desk</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title class="sidebar-subtitle">테이블 예약</v-list-item-title>
                  </v-list-item-content>
                </v-list-item>

                <!-- 물건 나눔 아이콘 -->
                <v-list-item @click="$router.push('/sharingHome')">
                  <v-list-item-icon>
                    <v-icon class="white--text">mdi-hand-heart</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title class="sidebar-subtitle">물건 나눔</v-list-item-title>
                  </v-list-item-content>
                </v-list-item>

                <!-- 나의 정보 보기 -->
                <v-list-item @click="$router.push('/myInfo')">
                  <v-list-item-icon>
                    <v-icon class="white--text">mdi-account</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title class="sidebar-subtitle">나의 정보 관리</v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
              </template>
            </v-list-item-group>
          </v-list>
        </v-navigation-drawer>
      </v-col>




  <!-- 가로로 긴 네모칸 -->
  <v-col cols="12" md="9" class="mt-5" >
        <v-card class="mx-auto" max-width="80%" outlined>
          <v-card-title>
            <v-row align="center" justify="start" no-gutters>
              <v-col cols="auto">
                
                <v-avatar size="80" color="grey lighten-4">
                  <v-icon size="48">mdi-account-circle</v-icon>
                </v-avatar>
              </v-col>
              <v-col>
                <div class="user-info">{{ userInfo?.nickName }}</div>
                <div class="caption grey--text user-info">{{ userInfo?.email }}</div>
              </v-col>
            </v-row>
          </v-card-title>
        </v-card>

        <!-- 추가된 네모칸 -->
        <v-card class="mx-auto mt-5" max-width="80%" outlined>
          <v-card-title class="justify-center">테이블정보</v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="12" md="6" class="text-center">
                <div class="font-weight-bold">예약 횟수</div>
                <div>
                  <v-icon color="primary">mdi-desk</v-icon>
                  {{ userInfo?.reservationCount }}번
                </div>
              </v-col>
              <v-col cols="12" md="6" class="text-center">
                <div class="font-weight-bold">총 예약 시간</div>
                <div>
                  <v-icon color="primary">mdi-clock-outline</v-icon>
                  {{ userInfo?.totalReservationTime }}분
                </div>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>

        <v-card class="mx-auto mt-5" max-width="80%">
          <v-card-title class="justify-center">나눔 정보</v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="12" md="4" class="text-center">
                <div class="font-weight-bold">나눔 횟수</div>
                <div>{{ userInfo?.sharingRoomCount }}</div>
              </v-col>
              <v-col cols="12" md="4" class="text-center">
                <div class="font-weight-bold">이긴 횟수</div>
                <div>{{ userInfo?.wins }}</div>
              </v-col>
              <v-col cols="12" md="4" class="text-center">
                <div class="font-weight-bold">승률</div>
                <div>{{ userInfo?.winrate }}</div>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>

        <v-card class="mx-auto mt-3" max-width="80%">
          <v-card-title class="text-h5">나눔방 목록</v-card-title>
          <v-card-text>
            <v-list dense>
              <template v-for="(room, index) in sharingRooms" :key="'room-' + room.id">
                <v-divider v-if="index !== 0" class="mx-auto" :style="{ maxWidth: '95%' }"></v-divider>
                <v-list-item two-line>
                  <v-list-item-avatar size="100">
                  <v-img :src="room.itemImagePath || 'src/assets/noImage.png'" contain></v-img>
                  </v-list-item-avatar>

                  <v-list-item-content>
                    <v-list-item-title>{{ room.title }}</v-list-item-title>
                    <v-list-item-subtitle>{{ room.contents }}</v-list-item-subtitle>
                    <v-list-item-subtitle>참여 인원: {{ room.cntPeople }}명</v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
              </template>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-app>
</template>

<script>
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

export default {
  name: 'MyPage',
  data() {
    return {
      showCategory: false,
      userInfo: null, // Added to store user information
      sharingRooms: [],
    };
  },
  created() {
    this.fetchUserInfo();
    this.fetchSharingRooms();
  },

  methods: {
  toggleCategory() {
    this.showCategory = !this.showCategory;
  },
  async fetchUserInfo() {
    const token = localStorage.getItem('Authorization');
    if (!token) {
      console.error('JWT token is not available');
      alert('로그인이 필요합니다.');
      this.$router.push('/login');
      return;
    }

    try {
      const backendUrl = process.env.VUE_APP_API_BASE_URL;
      const decoded = jwtDecode(token);
      const userEmail = decoded.email;

      if (!userEmail) {
        throw new Error("Email cannot be extracted from token.");
      }

      const response = await axios.get(`${backendUrl}/member/detailByEmail?email=${encodeURIComponent(userEmail)}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      this.userInfo = response.data.result; // 또는 `response.data`로 직접 접근할 수 있습니다.
    } catch (error) {
      console.error('Error fetching user info:', error);
      alert("회원 정보를 불러오는데 실패했습니다.");
    }
  },
  async fetchSharingRooms() {
      const backendUrl = process.env.VUE_APP_API_BASE_URL;
      try {
        const response = await axios.get(`${backendUrl}/rooms`);
        this.sharingRooms = response.data; // Assuming the response data is the list of sharing rooms
      } catch (error) {
        console.error('Error fetching sharing rooms:', error);
      }
    },
  },
};
</script>

<style scoped>
.grey--text {
  color: #757575 !important;
}

.grey.darken-4, .white--text {
  background-color: #424242 !important;
  color: white !important;
}

.white {
  background-color: white !important;
}

.sidebar-title, .sidebar-subtitle {
  font-size: 20px;
}
.user-info {
  margin-left: 20px; /* 예시로 16px을 사용하였으나 필요에 따라 조절 */
}

.v-divider {
  max-width: 80%;
  margin-left: auto;
  margin-right: auto;
}
.v-list-item-avatar {
  width: 80px; /* Adjust the width of the avatar */
  height: 80px; /* Adjust the height of the avatar */
}

.v-img {
  object-fit: cover; /* Adjust to cover or contain based on your needs */
}


</style>