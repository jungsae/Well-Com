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
        <v-col cols="12" sm="3" v-for="table in tableList" :key="table.deskNum">
          <div
            class="flip-card"
            @mouseover="table.flipped = true"
            @mouseleave="table.flipped = false"
          >
            <div
              class="flip-card-inner"
              :class="{ 'is-flipped': table.flipped }"
            >
              <!-- 카드 전면 -->
              <div class="flip-card-front">
                <v-card class="card-image">
                  <v-card-title>테이블 번호: {{ table.deskNum }}</v-card-title>
                  <v-card-subtitle>좌석수: {{ table.seats }}</v-card-subtitle>
                  <div class="image-container">
                    <img
                      :src="table.tableImg"
                      alt="이미지 준비중"
                      style="height: 100%; width: 100%"
                    />
                    <div v-if="table.isUsable === 'N'" class="overlay">
                      사용중
                    </div>
                  </div>
                </v-card>
              </div>
              <!-- 카드 뒷면 -->
              <div class="flip-card-back">
                <v-card class="card-image">
                  <v-card-title>오늘의 예약 정보</v-card-title>
                  <v-card-text>
                    <v-row>
                      <v-col
                        cols="12"
                        v-for="reservation in filterReservationsForToday(
                          table.reservations
                        )"
                        :key="reservation.reservationId"
                      >
                        {{ reservation.startTime.split("T")[1] }} ~
                        {{ reservation.endTime.split("T")[1] }}
                      </v-col>
                    </v-row>
                  </v-card-text>
                </v-card>
              </div>
            </div>
          </div>
          <v-card-actions class="d-flex justify-center">
            <v-btn
              :disabled="table.isUsable == 'N'"
              color="green"
              rounded="xs"
              size="x-large"
              @click="openImmediateUseDialog(table.deskNum, table.seats)"
            >
              바로사용하기
            </v-btn>
            <v-btn
              color="primary"
              rounded="xs"
              size="x-large"
              @click="showReservationModal(table.deskNum, table.seats)"
            >
              예약하기
            </v-btn>
          </v-card-actions>
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
                  <div v-if="showTimePicker">
                    <input
                      required
                      type="time"
                      min="09:00"
                      max="21:50"
                      v-model="manualTimeInput"
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
    showReservationModal(tableId, seats) {
      this.isReservationModalVisible = true;
      this.currentTableId = tableId;
      this.maxSeat = seats;
    },
    closeReservationModal() {
      this.isReservationModalVisible = false;
      this.showTimePicker = false;
      this.selectedSeats = null;
      this.selectedDate = null;
      this.manualTimeInput = null;
      this.selectedDuration = null;
    },
    openImmediateUseDialog(deskNum, seats) {
      this.isImmediateUseDialogVisible = true;
      this.currentTableId = deskNum;
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
        flipped: false,
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
          throw new Error("올바른 값을 입력해주세요!");
        } else if (localStorage.getItem("Authorization") == null) {
          console.log(localStorage.getItem("Authorization"));
          throw new Error("로그인 해주세요");
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
        alert(error.message);
      }
    },

    async reserveTable() {
      try {
        if (
          this.currentTableId == null ||
          this.selectedSeats == null ||
          this.selectedDate == null ||
          this.selectedDuration == null
        ) {
          throw new Error("올바른 값을 입력해주세요!");
        } else {
          const reservationData = {
            deskNum: this.currentTableId,
            cntPeople: this.selectedSeats,
            startTime: `${
              new Date(
                this.selectedDate.getTime() -
                  this.selectedDate.getTimezoneOffset() * 60000
              )
                .toISOString()
                .split("T")[0]
            }T${this.manualTimeInput}:00`,
            minutes: this.selectedDuration,
          };
          const response = await axios.post(
            `${process.env.VUE_APP_API_BASE_URL}/reservation/create`,
            reservationData,
            this.$token("members/reissue")
          );
          alert(`예약성공 : 예약번호 ${response.data.result}`);
          this.closeReservationModal();
          location.reload();
        }
      } catch (error) {
        console.log(error);
        alert(`예약실패: ${error.message}`);
      }
    },

    filterReservationsForToday(reservations) {
      return reservations.filter((reservation) => {
        const reservationDate = reservation.startTime.split("T")[0];
        return reservationDate === this.today;
      });
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

.flip-card {
  perspective: 1000px;
  width: 100%; /* 부모 컨테이너의 크기에 맞춤 */
}
.flip-card-inner {
  position: relative;
  width: 100%;
  height: 550px; /* 여기서는 예시로 550px을 사용 */
  transition: transform 0.8s;
  transform-style: preserve-3d;
}
.flip-card-front,
.flip-card-back {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
.flip-card-front {
  z-index: 2;
}
.flip-card-back {
  transform: rotateY(180deg);
  z-index: 1;
}
.flip-card:hover .flip-card-inner,
.flip-card-inner.is-flipped {
  transform: rotateY(180deg);
}

@media (max-width: 709px) {
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
    height: 90%;
    margin-bottom: 5px;
    width: 100%;
  }
}

@media (max-width: 600px) {
  .under-600 .v-btn {
    width: 100%;
    margin-bottom: 8px;
  }
  .v-card-title {
    font-size: 25px;
  }
}
</style>
