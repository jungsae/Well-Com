<template>
  <v-main class="wrap">
    <v-container class="v-container">
      <v-row justify="center">
        <v-col cols="12" md="6">
          <v-card>
            <v-card-title class="text-center">나눔 글쓰기</v-card-title>
            <v-card-text>
              <v-form @submit.prevent="roomCreate">
                <v-text-field
                  v-model="title"
                  label="제목"
                  :counter="titleCounter"
                  maxlength="20"
                  :error-messages="titleErrors"
                  :error="titleErrors.length > 0"
                ></v-text-field>
                <v-textarea
                  v-model="contents"
                  label="내용"
                  :counter="contentsCounter"
                  maxlength="300"
                  :error-messages="contentsErrors"
                  :error="contentsErrors.length > 0"
                ></v-textarea>
                <v-text-field
                  v-model.number="cntPeople"
                  label="인원 수"
                  type="number"
                  max="150"
                  :error-messages="cntPeopleErrors"
                  :error="cntPeopleErrors.length > 0"
                ></v-text-field>
                <v-text-field
                  v-model="itemName"
                  label="상품 이름"
                  :counter="itemNameCounter"
                  maxlength="20"
                  :error-messages="itemNameErrors"
                  :error="itemNameErrors.length > 0"
                ></v-text-field>
                <v-file-input
                  v-model="itemImage"
                  label="상품 이미지"
                  @change="fileUpload"
                ></v-file-input>
                <div class="text-right">
                  <v-btn type="submit" color="primary">제출</v-btn>
                  <v-btn @click="goToPage('/sharingHome')" class="custom-btn"
                    >취소</v-btn
                  >
                </div>
              </v-form>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-main>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      title: "",
      contents: "",
      cntPeople: null,
      itemName: "",
      itemImage: null,
    };
  },
  computed: {
    titleCounter() {
      return `${this.title.length} / 20`;
    },
    titleErrors() {
      const errors = [];
      if (this.title.length === 0) {
        errors.push("제목을 입력하세요.");
      } else if (this.title.length > 20) {
        errors.push("제목은 20자 이하여야 합니다.");
      }
      return errors;
    },
    contentsCounter() {
      return `${this.contents.length} / 300`;
    },
    contentsErrors() {
      const errors = [];
      if (this.contents.length === 0) {
        errors.push("내용을 입력하세요.");
      } else if (this.contents.length > 300) {
        errors.push("내용은 300자 이하여야 합니다.");
      }
      return errors;
    },
    cntPeopleErrors() {
      const errors = [];
      if (!this.cntPeople || isNaN(this.cntPeople) || this.cntPeople > 150) {
        errors.push(
          "올바른 인원 수를 입력하세요. 0명 ~ 150명까지 설정 가능합니다."
        );
      }
      return errors;
    },
    itemNameCounter() {
      return `${this.itemName.length} / 20`;
    },
    itemNameErrors() {
      const errors = [];
      if (this.itemName.length === 0) {
        errors.push("상품 이름을 입력하세요.");
      } else if (this.itemName.length > 20) {
        errors.push("상품 이름은 20자 이하여야 합니다.");
      }
      return errors;
    },
  },
  methods: {
    goToPage(path) {
      this.$router.push(path);
    },
    fileUpload(event) {
      this.itemImage = event.target.files[0];
    },
    async roomCreate() {
      try {
        // 필수 입력 필드인 title, contents, cntPeople, itemName이 모두 입력되었는지 확인
        if (
          this.title &&
          this.contents &&
          this.cntPeople !== null &&
          this.itemName
        ) {
          const formData = new FormData();
          formData.append("title", this.title);
          formData.append("contents", this.contents);
          formData.append("cntPeople", this.cntPeople);
          formData.append("itemName", this.itemName);
          formData.append("itemImage", this.itemImage ? this.itemImage : null);
          // // 이미지가 첨부되었을 때만 FormData에 추가
          // if (this.itemImage) {
          //   formData.append("itemImage", this.itemImage);
          // }

          const response = await axios.post(
            `${process.env.VUE_APP_API_BASE_URL}/user/room/create`,
            formData,
            this.$token("members/reissue")
          );
          alert("상품 등록에 성공했습니다.");
          console.log(response.data.result);
          this.$router.push("/sharingHome");
        } else {
          // 필수 입력 필드가 비어있을 경우 알림 표시
          alert("제목, 내용, 인원 수, 상품 이름은 필수 입력 값입니다.");
        }
      } catch (error) {
        console.error("Error submitting form:", error);
        alert("상품 등록에 실패했습니다. 다시 시도해주세요.");
      }
    },
  },
};
</script>
