<template>
  <v-main class="wrap">
    <v-container class="v-container">
      <v-row justify="center">
        <v-col cols="12" sm="6" v-for="table in tableList" :key="table.id">
          <v-card class="card-image">
            <v-card-title>테이블 번호: {{ table.deskNum }}</v-card-title>
            <v-card-subtitle>좌석수: {{ table.seats }}</v-card-subtitle>
            <div class="image-container">
              <img
                :src="table.tableImg"
                alt="이미지 준비중"
                style="height: 100%; width: 100%"
              />
              <div v-if="table.isUsable === 'N'" class="overlay">사용중</div>
            </div>
            <v-card-item>{{
              table.hasTV == "Y" ? "TV 있음" : "TV 없음"
            }}</v-card-item>
            <v-card-actions>
              <v-btn
                :disabled="table.isUsable == 'N'"
                @click="openImmediateUseDialog(table.id, table.seats)"
                color="primary"
                >바로사용하기</v-btn
              >
              <v-spacer></v-spacer>
              <v-btn
                @click="showReservationModal(table.id, table.seats)"
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
      max-height="850px"
      transition="dialog-bottom-transition"
    >
      <v-card>
        <v-card-title>예약 날짜와 시간 선택</v-card-title>
        <v-card-text>
          <v-row>
            <v-col cols="12">
              <v-date-picker
                v-model="selectedDate"
                @input="showTimePicker = true"
              ></v-date-picker>
            </v-col>
            <v-select
              v-model="selectedSeats"
              label="인원선택"
              density="comfortable"
              :items="
                Array.apply(0, { length: maxSeat + 1 })
                  .map(Number.call, Number)
                  .slice(1)
              "
            ></v-select>
            <v-col cols="12" v-if="showTimePicker">
              <v-text-field
                label="시간을 입력해주세요 (예: 15:00)"
                v-model="manualTimeInput"
                @input="updateTimePicker"
              ></v-text-field>
              <v-time-picker
                v-model="selectedTime"
                @change="handleTimeChange"
              ></v-time-picker>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-btn color="primary" @click="reserveTable">예약 확인</v-btn>
          <v-btn text @click="closeReservationModal">취소</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog
      v-model="isImmediateUseDialogVisible"
      max-width="290px"
      persistent
      transition="dialog-bottom-transition"
    >
      <v-card>
        <v-card-title>사용 시간 입력</v-card-title>
        <v-card-text>
          <v-select
            v-model="selectedSeats"
            label="인원선택"
            density="comfortable"
            :items="
              Array.apply(0, { length: maxSeat + 1 })
                .map(Number.call, Number)
                .slice(1)
            "
          ></v-select>
          <v-text-field
            v-model="immediateUseMinutes"
            label="사용할 시간 (분)"
            type="number"
            :rules="[(v) => !!v || '시간을 입력해주세요']"
          ></v-text-field>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="closeImmediateUseDialog"
            >취소</v-btn
          >
          <v-btn color="blue darken-1" text @click="immediateUseTable"
            >확인</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-main>
</template>

<script>
import axios from "axios";
export default {
  data() {
    return {
      tableList: [],
      maxSeat: null,
      selectedSeats: null,

      isImmediateUseDialogVisible: false,
      isReservationModalVisible: false,
      selectedDate: null,
      selectedTime: null,
      showTimePicker: false,
      manualTimeInput: "",
      currentTableId: null,
      immediateUseMinutes: null,
    };
  },
  async created() {
    const response = await axios.get(
      `${process.env.VUE_APP_API_BASE_URL}/desks`
    );
    this.tableList = response.data;
    this.tableList = this.tableList.map((table) => ({
      ...table,
      tableImg: require("../assets/table.jpg"),
    }));
  },
  watch: {
    selectedDate(newVal) {
      if (newVal) {
        this.showTimePicker = true;
      }
    },
  },
  methods: {
    handleDateChange(value) {
      this.selectedDate = value;
      this.showTimePicker = true;
    },
    updateTimePicker() {
      if (this.manualTimeInput.match(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/)) {
        this.selectedTime = this.manualTimeInput;
      }
    },
    handleTimeChange(value) {
      this.selectedTime = value;
      this.manualTimeInput = this.selectedTime;
    },
    showReservationModal(tableId) {
      this.currentTableId = tableId;
      this.isReservationModalVisible = true;
      this.showTimePicker = false;
    },
    closeReservationModal() {
      this.isReservationModalVisible = false;
      this.showTimePicker = false;
      this.selectedDate = null;
      this.selectedTime = null;
    },
    openImmediateUseDialog(tableId, seats) {
      this.isImmediateUseDialogVisible = true;
      this.currentTableId = tableId;
      this.maxSeat = seats;
    },
    closeImmediateUseDialog() {
      this.isImmediateUseDialogVisible = false;
      this.currentTableId = null;
      this.selectedSeats = null;
      this.immediateUseMinutes = null;
    },
    async immediateUseTable() {
      const reservationData = {
        deskNum: this.currentTableId,
        cntPeople: this.selectedSeats,
        minutes: this.immediateUseMinutes, // 고정된 값으로 설정, 필요에 따라 조정 가능
      };
      try {
        await axios.post(
          `${process.env.VUE_APP_API_BASE_URL}/reservation/now`,
          reservationData
        );
        alert("바로 사용 예약 성공!");
      } catch (error) {
        if (error.response.status) {
          alert("로그인 해주세요");
        }
        alert("바로 사용 예약 실패");
      } finally {
        this.closeImmediateUseDialog();
      }
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
        console.log(error.message);
        alert("예약실패");
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
.image-container {
  position: relative;
  display: inline-block;
  width: 100%;
  margin-bottom: 16px;
}

.card-image {
  border-radius: 20px;
  display: block;
  object-fit: cover;
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.6);
  color: black; /* 텍스트 색상 */
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 35px; /* 텍스트 크기 */
}
</style>
