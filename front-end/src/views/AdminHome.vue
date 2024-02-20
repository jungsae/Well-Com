<template>
  <v-app>
    <v-navigation-drawer app permanent class="deep-purple accent-3">
      <!-- Sidebar content -->
    </v-navigation-drawer>

    <v-main>
      <v-container>
        <h1>환영합니다, 관리자님!</h1>
        <div class="d-flex justify-space-between align-center my-4">
          <h2>회원 목록 조회</h2>
          <span>총 회원 수: {{ totalMembers }}</span>
        </div>

        <v-data-table
          :headers="headers"
          :items="members"
          :items-per-page="10"
          class="elevation-1"
          :footer-props="{
            'items-per-page-options': [10],
            'show-current-page': true,
            'show-first-last-page': true,
            'items-per-page-text': 'pagable',
          }"
        >
        <template v-slot:[`item.actions`]="{ item }">
            <v-btn small color="primary" @click="openDetailsModal(item)">상세 보기</v-btn>
          </template>
        </v-data-table>

        <!-- 회원 상세 정보 모달 -->
        <v-dialog v-model="detailsDialog" persistent max-width="600px">
          <v-card>
            <v-card-title>
              회원 상세 정보
              <v-spacer></v-spacer>
              <v-btn icon @click="closeDetailsModal(false)">
                <v-icon>mdi-close</v-icon>
              </v-btn>
            </v-card-title>

            <v-card-text>
              <div>닉네임: {{ memberDetail.nickName }}</div>
              <div>이메일: {{ memberDetail.email }}</div>
              <div>가입일: {{ memberDetail.createdTime }}</div>
              <div>권한: {{ memberDetail.role }}</div>
              <div>나눔방 생성 횟수: {{ memberDetail.sharingRoomCount }}</div>
              <div>테이블 예약 횟수: {{ memberDetail.reservationCount }}</div>
              <div>테이블 총 이용시간: {{ memberDetail.totalReservationTime }}</div>

            </v-card-text>

            <v-card-actions>
              <v-btn color="blue darken-1" text @click="confirmEdit(memberDetail)">회원 정보 수정</v-btn>
              <v-btn color="red darken-1" text @click="confirmDelete(memberDetail.id)">회원 삭제</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>

        <!-- 확인 모달 -->
        <v-dialog v-model="confirmDialog" persistent max-width="400px">
          <v-card>
            <v-card-title class="text-h5">{{ confirmDialogData.title }}</v-card-title>
            <v-card-text>{{ confirmDialogData.text }}</v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="grey darken-1" text @click="closeConfirmDialog(false)">아니오</v-btn>
              <v-btn color="blue darken-1" text @click="closeConfirmDialog(true)">네</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import axios from 'axios';

export default {
  name: 'AdminHome',
  data() {
    return {
      members: [],
      totalMembers: 0,
      memberDetail: {},
      detailsDialog: false,
      confirmDialog: false,
      confirmDialogData: { title: '', text: '', onConfirm: null },
      headers: [
        { text: '회원 ID', value: 'id' },
        { text: '닉네임', value: 'nickName' },
        { text: '이메일', value: 'email' },
        { text: '액션', value: 'actions', sortable: false },
      ],
    };
  },
  methods: {
    async fetchMembers() {
      try {
        const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/member/list`);
        this.members = response.data.result.members;
        this.totalMembers = response.data.result.totalMembers;
      } catch (error) {
        console.error('회원 목록 조회 실패:', error);
      }
    },
    async openDetailsModal(item) {
      try {
        const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/member/${item.id}/detail`);
        this.memberDetail = response.data.result;
        this.detailsDialog = true;
      } catch (error) {
        console.error('회원 상세 정보 조회 실패:', error);
      }
    },
    closeDetailsModal(refetch = false) {
      this.detailsDialog = false;
      if (refetch) this.fetchMembers();
    },
    confirmEdit(member) {
      this.confirmDialogData = {
        title: '회원 정보 수정',
        text: '회원의 정보를 수정하시겠습니까?',
        onConfirm: () => this.editMember(member)
      };
      this.confirmDialog = true;
    },
    // async editMember(member) {
    //   // 회원 정보 수정 로직
    //   this.closeConfirmDialog();
    // },
    confirmDelete(id) {
      this.confirmDialogData = {
        title: '회원 삭제',
        text: '정말 해당 회원을 삭제하시겠습니까?',
        onConfirm: () => this.deleteMember(id)
      };
      this.confirmDialog = true;
    },
    async deleteMember(id) {
      try {
        await axios.delete(`${process.env.VUE_APP_API_BASE_URL}/admin/member/${id}/delete`);
        this.fetchMembers(); // 목록 새로고침
        this.closeDetailsModal(); // 상세 정보 모달 닫기
      } catch (error) {
        console.error('회원 삭제 실패:', error);
      }
      this.closeConfirmDialog();
    },
    closeConfirmDialog(confirm) {
      this.confirmDialog = false;
      if (confirm && this.confirmDialogData.onConfirm) {
        this.confirmDialogData.onConfirm();
      }
      this.confirmDialogData.onConfirm = null; // 중복 호출 방지
    },
  },
  created() {
    this.fetchMembers();
  },
};
</script>
