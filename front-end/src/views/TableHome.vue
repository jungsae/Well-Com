<template>
  <v-main class="wrap">
    <v-container class="v-container">
      <v-row align-content="sm">
        <v-btn @click="filterTablesWithTV">TV가 있는 테이블 보기</v-btn>
        <v-spacer></v-spacer>
        <v-btn @click="loadTables">전체 보기</v-btn>
        <v-spacer></v-spacer>
        <v-btn @click="filterTablesWithUsable"
          >바로 사용가능한 테이블 보기</v-btn
        >
      </v-row>
      <v-row justify="center">
        <v-col cols="12" sm="3" v-for="table in tableList" :key="table.id">
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
            <v-card-actions>
              <v-row align-content="center">
                <v-col cols="12" sm="6">
                  <v-btn
                    :disabled="table.isUsable == 'N'"
                    color="green"
                    size="large"
                    rounded="xs"
                    @click="openImmediateUseDialog(table.id, table.seats)"
                  >
                    바로사용하기
                  </v-btn>
                </v-col>
                <v-col cols="12" sm="6">
                  <v-btn
                    color="primary"
                    size="large"
                    rounded="xs"
                    @click="showReservationModal(table.id, table.seats)"
                  >
                    예약하기
                  </v-btn>
                </v-col>
              </v-row>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-container>

    <!-- 예약 -->
    <v-dialog
      v-model="isReservationModalVisible"
      max-width="450px"
      max-height="850px"
      transition="dialog-bottom-transition"
    >
      <v-card>
        <v-card-title>예약 날짜와 시간 선택</v-card-title>
        <v-card-text>
          <v-row justify="center">
            <v-col cols="12">
              <v-select
                v-model="selectedSeats"
                label="인원선택"
                color="blue-grey"
                density="comfortable"
                :items="
                  Array.apply(0, { length: maxSeat + 1 })
                    .map(Number.call, Number)
                    .slice(1)
                "
              ></v-select>
              <v-col cols="12">
                <v-date-picker
                  title="예약 일시"
                  color="blue-grey"
                  v-model="selectedDate"
                  :min="today"
                ></v-date-picker>
              </v-col>
            </v-col>
            <v-col cols="12" v-if="showTimePicker">
              <v-text-field
                label="시간을 입력해주세요 (예: 15:00)"
                :rules="[(v) => !!v || '예약할 시간을 선택해주세요']"
                v-model="manualTimeInput"
                @input="updateTimePicker"
              ></v-text-field>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-btn color="primary" @click="reserveTable">예약 확인</v-btn>
          <v-btn text @click="closeReservationModal">취소</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 바로사용 -->
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
            :rules="[(v) => !!v || '인원을 선택해주세요']"
          ></v-select>
          <v-text-field
            v-model="immediateUseMinutes"
            label="사용할 시간 (분)"
            type="number"
            :rules="[
              (v) => !!v || '시간을 입력해주세요',
              (v) =>
                (v > 0 && v <= 60) || '유효한 시간을 입력해주세요 (1분 ~ 60분)',
            ]"
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
      today: new Date().toISOString().substring(0, 10),
      tableList: [],
      currentTableId: null,
      maxSeat: null,
      selectedSeats: null,
      manualTimeInput: null,
      immediateUseMinutes: null,
      selectedDate: null,
      selectedTime: null,

      isImmediateUseDialogVisible: false,
      isReservationModalVisible: false,
      showTimePicker: false,
    };
  },
  created() {
    this.loadTables();
  },
  watch: {
    selectedDate(newVal) {
      this.showTimePicker = newVal !== null;
    },
  },
  methods: {
    handleDateChange(value) {
      this.selectedDate = value;
      this.showTimePicker = true;
    },
    handleTimeChange(value) {
      this.selectedTime = value;
      this.manualTimeInput = this.selectedTime;
    },
    showReservationModal(tableId, seats) {
      this.currentTableId = tableId;
      this.maxSeat = seats;
      this.isReservationModalVisible = true;
      // this.showTimePicker = false;
    },
    closeReservationModal() {
      this.isReservationModalVisible = false;
      this.showTimePicker = false;
      this.selectedDate = null;
      this.selectedSeats = null;
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
    async loadTables() {
      const response = await axios.get(
        `${process.env.VUE_APP_API_BASE_URL}/desks`
      );
      this.tableList = response.data;
      this.tableList = this.tableList.map((table) => ({
        ...table,
        tableImg:
          table.hasTV === "Y"
            ? require("../assets/TvTable.jpg")
            : require("../assets/table.jpg"),
      }));
    },
    async filterTablesWithTV() {
      const response = await axios.get(
        `${process.env.VUE_APP_API_BASE_URL}/desks?hasTV=Y`
      );
      this.tableList = response.data;
      this.tableList = this.tableList.map((table) => ({
        ...table,
        tableImg:
          table.hasTV === "Y"
            ? require("../assets/TvTable.jpg")
            : require("../assets/table.jpg"),
      }));
    },
    async filterTablesWithUsable() {
      const response = await axios.get(
        `${process.env.VUE_APP_API_BASE_URL}/desks?isUsable=Y`
      );
      this.tableList = response.data;
      this.tableList = this.tableList.map((table) => ({
        ...table,
        tableImg:
          table.hasTV === "Y"
            ? require("../assets/TvTable.jpg")
            : require("../assets/table.jpg"),
      }));
    },
    async immediateUseTable() {
      const reservationData = {
        deskNum: this.currentTableId,
        cntPeople: this.selectedSeats,
        minutes: this.immediateUseMinutes,
      };
      try {
        if (
          reservationData.minutes < 1 ||
          reservationData.minutes === null ||
          reservationData.cntPeople < 1 ||
          reservationData.cntPeople === null
        ) {
          alert("올바른 값을 입력해주세요");
        } else if (localStorage.getItem("Authorization") == null) {
          console.log(localStorage.getItem("Authorization"));
          alert("로그인 해주세요");
          location.reload();
        } else {
          await axios.post(
            `${process.env.VUE_APP_API_BASE_URL}/reservation/now`,
            reservationData,
            this.$token("members/reissue")
          );
          this.closeImmediateUseDialog();
          alert("바로 사용 예약 성공!");
          location.reload();
        }
      } catch (error) {
        if (error.response.status == 404) {
          alert("로그인 해주세요");
        } else {
          alert(error);
        }
      }
      // finally {
      //   this.closeImmediateUseDialog();
      //   location.reload();
      // }
    },

    async reserveTable() {
      const reservationData = {
        deskNum: this.currentTableId,
        cntPeople: this.selectedSeats, // 예시: 사용자가 선택한 사람 수 또는 고정 값
        startTime: `${this.selectedDate}T${this.selectedTime}:00`, // 날짜와 시간을 ISO 형식으로 조합
        minutes: 5, // 예시: 예약 지속 시간 (분 단위)
      };
      console.log();
      const isoDateTime = `${this.selectedDate}T${this.selectedTime}:00`;
      console.log(isoDateTime); // "2024-02-19T14:20:00"과 같은 출력을 기대합니다.
      try {
        const response = await axios.post(
          `${process.env.VUE_APP_API_BASE_URL}/reservation/create`,
          reservationData
        );
        console.log(response.data);
        this.closeReservationModal();
      } catch (error) {
        console.log(error.message);
        alert("예약실패");
        this.closeReservationModal();
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
  margin-top: 10px;
}

.image-container {
  text-align: center;
  position: relative;
  height: 72%;
  width: 100%;
  margin-bottom: 22px;
}

.card-image {
  border-radius: 20px;
  display: block;
  object-fit: cover;
  height: 550px;
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.6);
  color: black;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 35px;
}

@media (max-width: 675px) {
  .v-card-title {
    font-size: 16px; /* 폰트 크기를 줄여서 텍스트가 잘리지 않도록 조정 */
  }
}
@media (max-width: 600px) {
  .v-card-actions,
  .card-image {
    flex-direction: column;
  }
  .v-btn,
  .card-image {
    height: auto;
    margin-bottom: 5px;
    width: 100%;
  }
}
</style>
