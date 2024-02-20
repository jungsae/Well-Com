
<template>
    <v-app>
      <v-main>
        <v-container fluid>
          <v-row justify="center">
            <v-col cols="12" md="4" lg="3" v-for="(service, index) in services" :key="index">
              <v-card @click="openModal(service)" class="service-card" elevation="12" hover>
                <v-card-title class="headline text-center">{{ service.title }}</v-card-title>
                <v-card-text class="body-1 d-flex align-center justify-center">{{ service.description }}</v-card-text>
              </v-card>
            </v-col>
          </v-row>
          <!-- Member Management Modal -->
          <v-dialog v-model="memberModalOpen" max-width="800">
            <v-card class="modal-card">
              <v-card-title class="headline">
                {{ selectedService.title }}
                <span class="total-members">Total members: {{ memberList.length }}</span>
              </v-card-title>
              <!-- Member Detail Display -->
              <v-card-text v-if="!showMemberDetails" class="body-1">
                <table>
                  <tbody>
                    <tr>
                      <th>Select</th>
                      <th>Nickname</th>
                      <th>Email</th>
                    </tr>
                    <tr v-for="(item, index) in memberList" :key="index" :class="{ 'selected-row': isSelected(item) }">
                      <td>
                        <v-checkbox v-model="selectedMembers" :value="item" class="rounded-checkbox" color="primary"></v-checkbox>
                      </td>
                      <td>{{ item.nickName }}</td>
                      <td>{{ item.email }}</td>
                    </tr>
                  </tbody>
                </table>
              </v-card-text>
              <!-- Detailed Member Information Display -->
              <v-card-text v-if="showMemberDetails" class="body-1">
                <div v-for="(member, index) in selectedMembers" :key="index" class="member-info">
                  <table>
                    <tbody>
                      <tr>
                        <td>Nickname:</td>
                        <td>{{ member.nickName }}</td>
                      </tr>
                      <tr>
                        <td>Email:</td>
                        <td>{{ member.email }}</td>
                      </tr>
                      <tr>
                        <td>Membership Date:</td>
                        <td>{{ member.createdTime }}</td>
                      </tr>
                      <tr>
                        <td>Number of Sharing Rooms:</td>
                        <td>{{ member.sharingRoomCount }}</td>
                      </tr>
                      <tr>
                        <td>Reservation Count:</td>
                        <td>{{ member.reservationCount }}</td>
                      </tr>
                      <tr>
                        <td>Total Reservation Time:</td>
                        <td>{{ member.totalReservationTime }}</td>
                      </tr>
                      <tr>
                        <td>Wins:</td>
                        <td>{{ member.wins }}</td>
                      </tr>
                      <tr>
                        <td>Win Rate:</td>
                        <td>{{ member.winRate }}%</td>
                      </tr>
                    </tbody>
                  </table>
                  <hr class="member-info-separator">
                </div>
                <!-- Member Information Edit and Delete Buttons -->
                <v-card-actions>
                  <v-btn :disabled="selectedMembers.length !== 1" color="primary" @click="editMember">Edit Member Info</v-btn>
                  <v-btn :disabled="selectedMembers.length !== 1" color="error" @click="deleteMember">Delete Member</v-btn>
                  <p v-if="selectedMembers.length !== 1">Please select only one member to edit or delete their information.</p>
                </v-card-actions>
              </v-card-text>
              <v-card-actions>
                <!-- Button to View Details or Go Back -->
                <v-btn v-if="!showMemberDetails" color="primary" @click="showDetails">View Details</v-btn>
                <v-btn v-else color="primary" @click="backToList">Go Back</v-btn>
                <v-btn color="primary" @click="closeModal">Close</v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
  
          <!-- Table Management Modal -->
          <v-dialog v-model="tableModalOpen" max-width="800">
            <v-card class="modal-card">
              <v-card-title class="headline">Table Management</v-card-title>
              <!-- Table Management Content -->
              <v-card-text>
                <p>Table management content goes here.</p>
              </v-card-text>
              <v-card-actions>
                <v-btn color="primary" @click="openTableList">View Tables</v-btn>
                <v-btn color="primary" @click="addNewTable">Add New Table</v-btn>
                <v-btn color="primary" @click="checkReservationStatus">Check Reservation Status</v-btn>
                <v-btn color="primary" @click="closeTableModal">Close</v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
  
          <!-- Member Edit Modal -->
          <v-dialog v-model="editModalOpen" max-width="500px">
            <v-card>
              <v-card-title>Edit Member Information</v-card-title>
              <v-card-text>
                <v-text-field label="Nickname" v-model="editedMember.nickName"></v-text-field>
                <v-text-field label="Email" v-model="editedMember.email"></v-text-field>
              </v-card-text>
              <v-card-actions>
                <v-btn color="primary" @click="updateMember">Save</v-btn>
                <v-btn color="grey" @click="editModalOpen = false">Cancel</v-btn>
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
    name: 'AdminHomeForm',
    data() {
      return {
        services: [
          { title: 'Member Management', description: 'Manage member list and details' },
          { title: 'Table Management', description: 'Set up and manage tables' },
          { title: 'Item Sharing Room Management', description: 'Manage sharing room list and details' },
        ],
        memberModalOpen: false,
        tableModalOpen: false,
        editModalOpen: false, // Added for the edit member modal
        selectedService: {},
        memberList: [],
        selectedMembers: [],
        showMemberDetails: false,
        editedMember: {
          nickName: '',
          email: '',
          id: '',
        },
      };
    },
    methods: {
      openModal(service) {
        this.selectedService = service;
        if (service.title === 'Member Management') {
          this.fetchMemberList();
          this.memberModalOpen = true;
        } else if (service.title === 'Table Management') {
          this.tableModalOpen = true;
        }
      },
      closeModal() {
        this.memberModalOpen = false;
        this.tableModalOpen = false;
        this.editModalOpen = false; // Ensure edit modal is also closed
        this.selectedService = {};
        this.selectedMembers = [];
        this.showMemberDetails = false;
      },
      fetchMemberList() {
  const apiUrl = `${process.env.VUE_APP_API_BASE_URL}/member/list`;
  // Return the Axios promise directly
  return axios.get(apiUrl)
    .then(response => {
      this.memberList = response.data.result.members;
    })
    .catch(error => {
      console.error('Error fetching member list:', error);
    });
},

      isSelected(member) {
        return this.selectedMembers.includes(member);
      },
      showDetails() {
  if (this.selectedMembers.length === 1) {
    const selectedMemberId = this.selectedMembers[0].id;
    // 선택된 회원의 상세 정보만 표시
    this.selectedMembers = this.memberList.filter(member => member.id === selectedMemberId);
    this.showMemberDetails = true;
  }
},
      backToList() {
        this.showMemberDetails = false;
      },
      editMember() {
        if (this.selectedMembers.length !== 1) {
          return;
        }
        const selectedMember = this.selectedMembers[0];
        this.editedMember = {
          nickName: selectedMember.nickName,
          email: selectedMember.email,
          id: selectedMember.id,
        };
        this.editModalOpen = true;
      },
      async updateMember() {
  const apiUrl = `${process.env.VUE_APP_API_BASE_URL}/member/${this.editedMember.id}/update`;

  try {
    await axios.post(apiUrl, {
      nickName: this.editedMember.nickName,
      email: this.editedMember.email
    });
    // 회원 정보를 즉시 업데이트
    const updatedMemberIndex = this.memberList.findIndex(member => member.id === this.editedMember.id);
    if (updatedMemberIndex !== -1) {
      this.memberList[updatedMemberIndex].nickName = this.editedMember.nickName;
      this.memberList[updatedMemberIndex].email = this.editedMember.email;
    }
    // 모달 닫기
    this.editModalOpen = false;
  } catch (error) {
    console.error('Error updating member information:', error);
    alert("회원 정보 업데이트에 실패했습니다. 콘솔을 확인해주세요.");
  }
},

      deleteMember() {
        if (this.selectedMembers.length !== 1) {
          alert("Please select a member."); 
          return;
        }
        if (!confirm("Are you sure you want to delete this member?")) {
          return; 
        }
        const memberId = this.selectedMembers[0].id;
        const apiUrl = `${process.env.VUE_APP_API_BASE_URL}/member/${memberId}/delete`;
        axios.delete(apiUrl)
          .then(() => {
            this.memberList = this.memberList.filter(member => member.id !== memberId);
            this.closeModal(); 
            this.selectedMembers = []; 
          })
          .catch(error => {
            console.error('Error deleting member:', error);
            alert("There was an error deleting the member."); 
          });
      },
      // Additional methods for table management modal...
    },
  };
  </script>
  <style scoped>
  .service-card {
    height: 200px;
    width: 100%;
    margin: 15px;
    border-radius: 15px;
    border: 1px solid #ccc;
    transition: transform 0.3s ease, box-shadow 1s ease;
    background: rgba(255, 255, 255, 0.3); /* 투명 배경 */
    position: relative;
    overflow: hidden;
    cursor: pointer;
  }
  
  .service-card::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 100%;
    height: 100%;
    background-image: radial-gradient(circle, rgba(255, 255, 255, 0.4), transparent);
    transform: translate(-50%, -50%) scale(0);
    transition: transform 0.5s ease;
  }
  
  .service-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
  }
  
  .service-card.clicked::before {
    transform: translate(-50%, -50%) scale(4);
    opacity: 0;
  }
  
  /* 기존 스타일은 유지 */
  .headline {
    font-size: 24px;
    color: orange;
  }
  
  .body-1 {
    font-size: 18px;
    color: black;
  }
  
  .total-members {
    font-size: 16px;
    color: gray;
    float: right;
  }
  
  .table,
  th,
  td {
    border: 1px solid #ccc;
    padding: 8px;
    border-radius: 5px;
  }
  
  th {
    font-weight: bold;
    text-align: left;
  }
  
  td {
    text-align: left;
  }
  
  .rounded-checkbox .v-input--selection-controls__input {
    border-radius: 50%;
  }
  
  .rounded-checkbox .v-input--selection-controls__ripple {
    border-radius: 50%;
  }
  
  .selected-row {
    background-color: #f0f0f0;
  }
  
  .modal-card {
    border-radius: 15px;
  }
  
  .member-info-separator {
    margin-top: 20px;
    margin-bottom: 20px;
  }
  </style>
  