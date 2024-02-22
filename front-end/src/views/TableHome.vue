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
            <v-card-actions class="d-flex justify-center">
              <v-btn
                :disabled="table.isUsable == 'N'"
                color="green"
                rounded="xs"
                size="x-large"
                @click="openImmediateUseDialog(table.id, table.seats)"
              >
                바로사용하기
              </v-btn>
              <v-btn
                color="primary"
                rounded="xs"
                size="x-large"
                @click="showReservationModal(table.id, table.seats)"
              >
                예약하기
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-container>

    <!-- 예약 -->
    <v-dialog
      v-model="isReservationModalVisible"
      persistent
      max-width="450px"
      max-height="850px"
      transition="dialog-bottom-transition"
    >
      <v-card class="under-600">
        <v-card-title class="d-flex justify-center"
          >예약 날짜와 시간 선택</v-card-title
        >
        <v-card-text>
          <v-row justify="center">
            <v-col cols="12" style="max-height: 700px">
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
              <v-date-picker
                title="예약 일시"
                color="blue-grey"
                v-model="selectedDate"
                :min="today"
              ></v-date-picker>
              <v-row>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-if="showTimePicker"
                    label="예약 시작 시간"
                    :rules="[(v) => !!v || '시간을 선택해주세요']"
                    v-model="manualTimeInput"
                    placeholder="예: 15:30"
                  ></v-text-field>
                  <div v-if="showTimePicker">
                    <input
                      type="time"
                      :rules="[(v) => !!v || '시간을 선택해주세요']"
                      v-model="selectedTime"
                      @input="updateTime"
                    />
                  </div>
                </v-col>
                <v-col cols="12" sm="6">
                  <v-select
                    v-if="showTimePicker"
                    label="사용 시간(분)"
                    v-model="selectedDuration"
                    :items="Array.from({ length: 60 }, (_, i) => i + 1)"
                    @input="updateDateTime"
                  >
                    <template v-slot:append-outer>분</template>
                  </v-select>
                </v-col>
              </v-row>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions class="d-flex justify-center under-600">
          <v-btn color="primary" @click="reserveTable">예약 확인</v-btn>
          <v-btn @click="closeReservationModal">취소</v-btn>
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
        <v-card-title class="d-flex justify-center"
          >사용 시간 입력</v-card-title
        >
        <v-card-text>
          <v-select
            v-model="selectedSeats"
            label="인원선택"
            density="comfortable"
            :items="Array.from({ length: maxSeat }, (_, i) => i + 1)"
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
        <v-card-actions class="d-flex justify-center">
          <v-btn
            size="x-large"
            color="blue darken-1"
            text
            @click="immediateUseTable"
            >확인</v-btn
          >
          <v-btn
            size="x-large"
            color="blue darken-1"
            text
            class="mx-1"
            @click="closeImmediateUseDialog"
            >취소</v-btn
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
      today: new Date(
        new Date().getTime() - new Date().getTimezoneOffset() * 60000
      )
        .toISOString()
        .split("T")[0],

      tableList: [],

      currentTableId: null,
      maxSeat: null,
      selectedSeats: null,
      manualTimeInput: null,
      immediateUseMinutes: null,
      selectedDate: null,
      selectedTime: null,
      selectedDuration: 1,

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
    updateDateTime() {
      if (
        this.selectedDate &&
        this.selectedStartTime &&
        this.selectedDuration
      ) {
        const isoDateTime = `${this.selectedDate}T${this.selectedStartTime}:00`;
        console.log(isoDateTime);
        console.log(`예약 시간(분): ${this.selectedDuration}`);
      }
    },
    showReservationModal(tableId, seats) {
      this.isReservationModalVisible = true;
      this.currentTableId = tableId;
      this.maxSeat = seats;
    },
    closeReservationModal() {
      this.isReservationModalVisible = false;
      this.showTimePicker = false;
      this.selectedDate = null;
      this.selectedSeats = null;
      this.selectedTime = null;
      this.manualTimeInput = null;
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
          (reservationData.minutes < 1 && reservationData.minutes > 60) ||
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
    },

    async reserveTable() {
      const reservationData = {
        deskNum: this.currentTableId,
        cntPeople: this.selectedSeats,
        startTime: `${this.selectedDate.toISOString()}`,
        minutes: this.selectedDuration,
      };

      console.log("선택시간: " + this.selectedDuration);
      console.log("선택날짜: " + this.selectedDate.getFullYear());
      console.log(reservationData);
      // const create_datetime = "2023-03-30T14:37:39.165926";
      // const date = new Date(create_datetime);
      // const year = date.getFullYear();
      // const month = String(date.getMonth() + 1).padStart(2, "0");
      // const day = String(date.getDate()).padStart(2, "0");
      // const hours = String(date.getHours()).padStart(2, "0");
      // const minutes = String(date.getMinutes()).padStart(2, "0");

      // const formattedDate = `${year}-${month}-${day} ${hours}:${minutes}`;

      // // console.log(formattedDate) ===>  2023-03-30 14:37

      try {
        // 유효성검사추가
        const response = await axios.post(
          `${process.env.VUE_APP_API_BASE_URL}/reservation/create`,
          reservationData,
          this.$token("members/reissue")
        );
        console.log(response.data);
        alert(`예약성공 : 예약번호 ${response.data.result}`);
        this.closeReservationModal();
        location.reload();
      } catch (error) {
        console.log(error);
        alert("예약실패");
        this.closeReservationModal();
      }
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

@media (max-width: 693px) {
  .v-card-title {
    font-size: 15px;
  }
}
@media (max-width: 920px) {
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

@media (max-width: 600px) {
  .under-600 .v-btn {
    width: 100%; /* 화면이 좁을 때 버튼을 전체 너비로 확장 */
    margin-bottom: 8px; /* 버튼 사이의 간격 */
  }
}
</style>
