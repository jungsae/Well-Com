<template>
  <v-app>
    <v-main>
      <v-container class="fill-height" fluid>
        <v-row justify="center" align="center">
          <v-col cols="12" md="8">
            <v-card class="mb-5" outlined>
              <v-card-title>
                <v-avatar size="80" color="grey lighten-4">
                  <v-icon size="48">mdi-account-circle</v-icon>
                </v-avatar>
                <div class="ml-3">
                  <div class="user-info">{{ userInfo?.nickName }}</div>
                  <div class="caption grey--text">{{ userInfo?.email }}</div>
                </div>
                <v-spacer></v-spacer>
                <v-btn color="green" @click="showEditModal = true">정보 수정하기</v-btn>
              </v-card-title>
            </v-card>
            <v-card class="mb-5" outlined>
              <v-card-title>테이블정보</v-card-title>
              <v-card-text>
                <v-row>
                  <v-col cols="12" md="6" class="text-center">
                    <div class="font-weight-bold">예약 횟수</div>
                    <div><v-icon color="primary">mdi-desk</v-icon> {{ userInfo?.reservationCount }}번</div>
                  </v-col>
                  <v-col cols="12" md="6" class="text-center">
                    <div class="font-weight-bold">총 예약 시간</div>
                    <div><v-icon color="primary">mdi-clock-outline</v-icon> {{ userInfo?.totalReservationTime }}분</div>
                  </v-col>
                </v-row>
              </v-card-text>
            </v-card>
            <v-card class="mb-5" outlined>
              <v-card-title>나눔 정보</v-card-title>
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
            <v-card outlined>
              <v-card-title>나눔방 목록</v-card-title>
              <v-card-text>
                <v-list dense>
                  <template v-for="(room, index) in sharingRooms" :key="'room-' + room.id">
                    <v-divider v-if="index !== 0" class="mx-auto" :style="{ maxWidth: '95%' }"></v-divider>
                    <v-list-item two-line>
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
            <v-dialog v-model="showEditModal" persistent max-width="500px" transition="dialog-transition">
              <v-card>
                <v-card-title>정보 수정</v-card-title>
                <v-card-text>
                  <v-form>
                    <v-text-field v-model="editUserInfo.nickName" label="닉네임"></v-text-field>
                    <v-text-field v-model="editUserInfo.email" label="이메일"></v-text-field>
                  </v-form>
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="blue darken-1" @click="updateUserInfo">저장</v-btn>
                  <v-btn color="grey" text @click="showEditModal = false">취소</v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
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
      showEditModal: false,
      editUserInfo: {
        nickName: '',
        email: '',
      }
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
      this.userInfo = response.data.result;
      this.editUserInfo.nickName = this.userInfo.nickName;
      this.editUserInfo.email = this.userInfo.email;// 또는 `response.data`로 직접 접근할 수 있습니다.
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
    updateUserInfo() {
      const backendUrl = process.env.VUE_APP_API_BASE_URL;
      axios.post(`${backendUrl}/member/${this.userInfo.id}/update`, this.editUserInfo, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('Authorization')}`
        }
      })
      .then(() => {
        alert('정보가 성공적으로 수정되었습니다.');
        this.showEditModal = false;
        this.fetchUserInfo(); // 유저 정보를 다시 불러옵니다.
      })
      .catch(error => {
        console.error('정보 수정 실패:', error);
        alert('정보 수정에 실패했습니다.');
      });
    },
  },
};
</script>
<style scoped>
/* 스타일 부분은 이전에 제공된 것을 기반으로 추가적인 스타일링이 필요할 수 있습니다. */
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
.sidebar-title, .sidebar-subtitle, .user-info {
  font-size: 16px; /* 필요에 따라 조절 */
}
/* 추가: 카드 내부의 텍스트 정렬을 위한 스타일 */
.user-info {
  margin-left: 20px; /* 예시로 사용; 실제 필요에 따라 조절 */
}
</style>