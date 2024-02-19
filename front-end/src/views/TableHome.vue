<template>
  <v-main class="wrap">
    <v-container class="v-container">
      <v-row justify="center">
        <v-col cols="12" sm="6" v-for="table in tableList" :key="table.id">
          <v-card>
            <v-card-title>테이블 번호: {{ table.deskNum }}</v-card-title>
            <v-card-subtitle>좌석수: {{ table.seats }}</v-card-subtitle>
            <v-img
              class="mytable"
              :height="300"
              :width="1200"
              aspect-ratio="16/9"
            ></v-img>
            <v-card-item>{{
              table.hasTV == "Y" ? "TV 있음" : "TV 없음"
            }}</v-card-item>
            <v-card-actions>
              <v-btn
                v-if="table.isUsable == 'Y'"
                :disabled="table.isUsable == 'N'"
                @click="showReservationModal(table.id)"
                color="primary"
                >예약하기</v-btn
              >
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
    <v-dialog
      v-model="isReservationModalVisible"
      max-width="450px"
      max-height="700px"
    >
      <v-card>
        <v-card-title>예약 날짜와 시간 선택</v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-date-picker
                  v-model="selectedDate"
                  @input="showTimePicker = true"
                ></v-date-picker>
              </v-col>
              <v-col cols="12" v-if="showTimePicker">
                <!-- 시간을 직접 입력할 수 있는 텍스트 필드 추가 -->
                <v-text-field
                  label="시간 직접 입력 (예: 15:00)"
                  v-model="manualTimeInput"
                  @input="updateTimePicker"
                ></v-text-field>
                <!-- 시간 선택기 -->
                <v-time-picker
                  v-model="selectedTime"
                  @change="handleTimeChange"
                ></v-time-picker>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-btn color="primary" @click="reserveTable">예약 확인</v-btn>
          <v-btn text @click="closeReservationModal">취소</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 예약 모달 -->
    <!-- <v-dialog
      v-model="isReservationModalVisible"
      max-width="450px"
      max-height="700px"
    >
      <v-card>
        <v-card-title>예약 날짜와 시간 선택</v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-date-picker
                  show-adjacent-months
                  v-model="selectedDate"
                  @input="showTimePicker = true"
                ></v-date-picker>
              </v-col>
              <v-col cols="12" v-if="showTimePicker">
                <v-time-picker
                  v-model="selectedTime"
                  @change="handleTimeChange"
                ></v-time-picker>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-btn color="primary" @click="reserveTable">예약 확인</v-btn>
          <v-btn text @click="closeReservationModal">취소</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog> -->
  </v-main>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      tableList: [],
      isReservationModalVisible: false,
      selectedDate: null,
      selectedTime: null,
      showTimePicker: false,
      manualTimeInput: "",
      currentTableId: null,
    };
  },
  async created() {
    const response = await axios.get(
      `${process.env.VUE_APP_API_BASE_URL}/desks`
    );
    this.tableList = response.data;
    console.lo;
  },
  methods: {
    handleDateChange(value) {
      this.selectedDate = value; // 선택된 날짜 업데이트
      this.showTimePicker = true; // 시간 선택기 활성화
    },
    updateTimePicker() {
      // `manualTimeInput`에서 입력된 시간을 `selectedTime`에 업데이트
      // 입력 형식이 올바른지 확인 후 시간 업데이트
      if (this.manualTimeInput.match(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/)) {
        this.selectedTime = this.manualTimeInput;
      }
    },
    handleTimeChange(value) {
      this.selectedTime = value;
      // `v-time-picker`에서 시간을 선택하면 `manualTimeInput`도 업데이트
      this.manualTimeInput = this.selectedTime;
    },
    showReservationModal(tableId) {
      this.currentTableId = tableId;
      this.isReservationModalVisible = true;
      this.showTimePicker = false; // 시간 선택기를 초기화합니다.
    },
    closeReservationModal() {
      this.isReservationModalVisible = false;
      this.showTimePicker = false;
      this.selectedDate = null;
      this.selectedTime = null;
    },
    async reserveTable() {
      const reservationData = {
        deskNum: this.currentTableId,
        cntPeople: 6, // 예시: 사용자가 선택한 사람 수 또는 고정 값
        startTime: `${this.selectedDate}T${this.selectedTime}:00`, // 날짜와 시간을 ISO 형식으로 조합
        minutes: 5, // 예시: 예약 지속 시간 (분 단위)
      };
      const isoDateTime = `${this.selectedDate}T${this.selectedTime}:00`;
      console.log(isoDateTime); // "2024-02-19T14:20:00"과 같은 출력을 기대합니다.
      try {
        // 서버로 예약 데이터 전송
        const response = await axios.post(
          `${process.env.VUE_APP_API_BASE_URL}/reservation/create`,
          reservationData
        );
        console.log(response.data); // 응답 로깅
        // 예약 성공 후 처리, 예를 들어 사용자에게 알림 표시
        this.closeReservationModal(); // 모달 닫기
      } catch (error) {
        console.error(error);
        this.closeReservationModal(); // 모달 닫기
        // 에러 처리, 예를 들어 사용자에게 에러 메시지 표시
      } // 실제 예약 처리를 여기에 구현합니다. 예: API 호출
    },
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

.mytable {
  max-width: 1000px;
  max-height: 1000px;
  background-image: url("../assets/table.jpg");
}
</style>
