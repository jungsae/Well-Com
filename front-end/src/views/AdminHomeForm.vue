
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
              <v-btn color="primary" @click="openAddTableModal">Add New Table</v-btn>

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


        <v-dialog v-model="tableListModalOpen" max-width="800px">
<v-card>
  <v-card-title>Table List</v-card-title>
  <v-card-text>
    <v-simple-table>
      <thead>
        <tr>
          <th>Select</th>
          <th>#</th>
          <th>Desk Number</th>
          <th>Seats</th>
          <th>Has TV</th>
          <th>Is Usable</th>
        </tr>
      </thead>
      <tbody>
          <tr v-for="(table, index) in tables" :key="index" :class="{ 'grey-background': table.isUsable === 'N' }">
  <td><v-checkbox v-model="selectedTables" :value="table"></v-checkbox></td> <!-- 체크박스 추가 -->
          <td>{{ index + 1 }}</td>
          <td>{{ table.deskNum }}</td>
          <td>{{ table.seats }}</td>
          <td>{{ table.hasTV }}</td>
          <td>{{ table.isUsable }}</td>
        </tr>
      </tbody>
    </v-simple-table>
  </v-card-text>
  <v-card-actions>
      <v-btn color="primary" @click="openEditTableModal">Edit</v-btn>
    <v-btn color="primary" @click="closeTableListModal">Close</v-btn>
    <v-btn color="error" @click="confirmDelete">Delete</v-btn>
    <v-btn color="error" @click="confirmDeleteSharingRoom">Delete</v-btn>

  </v-card-actions>
</v-card>
</v-dialog>

<!-- table modify -->
<v-dialog v-model="editTableModalOpen" max-width="500px">
<v-card>
  <v-card-title>Edit Table</v-card-title>
  <v-card-text>
    <v-container>
      <v-row>
        <v-col cols="6">Desk Number:</v-col>
        <v-col cols="6"><v-text-field v-model="editingTable.deskNum" readonly></v-text-field></v-col>
        <v-col cols="6">Seats:</v-col>
        <v-col cols="6"><v-text-field v-model="editingTable.seats"></v-text-field></v-col>
        <v-col cols="6">Has TV:</v-col>
        <v-col cols="6">
          <v-select :items="['Y', 'N']" v-model="editingTable.hasTV"></v-select>
        </v-col>
        <v-col cols="6">Is Usable:</v-col>
        <v-col cols="6">
          <v-select :items="['Y', 'N']" v-model="editingTable.isUsable"></v-select>
        </v-col>
      </v-row>
    </v-container>
  </v-card-text>
  <v-card-actions>
    <v-btn color="primary" @click="updateTable">Save</v-btn>
    <v-btn color="grey" @click="closeEditTableModal">Cancel</v-btn>
  </v-card-actions>
</v-card>
</v-dialog>

<!-- Add New Table Modal -->
<v-dialog v-model="addTableModalOpen" max-width="500px">
<v-card>
  <v-card-title>Add New Table</v-card-title>
  <v-card-text>
    <v-form ref="form">
      <v-text-field label="Desk Number" v-model="newTable.deskNum" type="number"></v-text-field>
      <v-text-field label="Seats" v-model="newTable.seats" type="number"></v-text-field>
      <v-select :items="['Y', 'N']" label="Has TV" v-model="newTable.hasTV"></v-select>
      <v-select :items="['Y', 'N']" label="Is Usable" v-model="newTable.isUsable"></v-select>
    </v-form>
  </v-card-text>
  <v-card-actions>
    <v-btn color="primary" @click="createTable">Save</v-btn>
    <v-btn color="grey" @click="closeAddTableModal">Cancel</v-btn>
  </v-card-actions>
</v-card>
</v-dialog>

<v-dialog v-model="reservationModalOpen" max-width="800px">
      <v-card>
        <v-card-title>Reservations</v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12" md="6">
                <v-text-field label="Desk Number" v-model="searchDeskNum" type="number"></v-text-field>
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field label="Date" v-model="searchDate" type="date"></v-text-field>
              </v-col>
            </v-row>
            <v-btn color="primary" @click="fetchReservations">Search</v-btn>
          </v-container>
          <v-simple-table>
            <!-- 테이블 헤더와 바디 -->
            <thead>
              <tr>
                <th>#</th>
                <th>Desk Number</th>
                <th>Status</th>
                <th>Start Time</th>
                <th>End Time</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(reservation, index) in reservations" :key="index">
                <td>{{ index + 1 }}</td>
                <td>{{ reservation.deskNum }}</td>
                <td>{{ reservation.status }}</td>
                <td>{{ reservation.startTime }}</td>
                <td>{{ reservation.endTime }}</td>
              </tr>
            </tbody>
          </v-simple-table>
        </v-card-text>
        <v-card-actions>
          <v-btn color="primary" @click="reservationModalOpen = false">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>


    <v-dialog v-model="sharingRoomModalOpen" max-width="800px">
  <v-card>
    <v-card-title>Item Sharing Room Management</v-card-title>
    <v-card-text>
      <v-simple-table>
        <thead>
          <tr>
            <th>Select</th>
            <th class="text-left">Title</th>
            <th class="text-left">Contents</th>
            <th class="text-left">People Count</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(room, index) in sharingRooms" :key="index">
            <td><v-checkbox v-model="selectedSharingRooms" :value="room.id"></v-checkbox></td>
            <td>{{ room.title }}</td>
            <td>{{ room.contents }}</td>
            <td>{{ room.cntPeople }}</td>
          </tr>
        </tbody>
      </v-simple-table>
    </v-card-text>
    <v-card-actions>
      <v-btn color="primary" @click="showSharingRoomDetails">Details</v-btn>
      <v-btn color="primary" @click="sharingRoomModalOpen = false">Close</v-btn>
    </v-card-actions>
  </v-card>
</v-dialog>

<v-dialog v-model="detailsModalOpen" max-width="800px">
  <v-card>
    <v-card-title>Sharing Room Details</v-card-title>
    <v-card-text v-if="sharingRoomDetails">
      <v-simple-table dense>
        <template v-slot:default>
          <thead>
            <tr>
              <th class="text-left">Field</th>
              <th class="text-left">Value</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Title</td>
              <td>{{ sharingRoomDetails.title }}</td>
            </tr>
            <tr>
              <td>Contents</td>
              <td>{{ sharingRoomDetails.contents }}</td>
            </tr>
            <tr>
              <td>People Count</td>
              <td>{{ sharingRoomDetails.cntPeople }}</td>
            </tr>
            <tr>
              <td>Item Name</td>
              <td>{{ sharingRoomDetails.itemName }}</td>
            </tr>
            <tr>
              <td>Item Status</td>
              <td>{{ sharingRoomDetails.itemStatus }}</td>
            </tr>
            <tr>
              <td>Item Image</td>
              <td><img :src="sharingRoomDetails.itemImagePath" alt="Item Image" height="100"></td>
            </tr>
          </tbody>
        </template>
      </v-simple-table>
    </v-card-text>
    <v-card-text v-else>
      <p>No details available.</p>
    </v-card-text>
    <v-card-actions>
      <v-btn color="primary" @click="openUpdateModal">Update</v-btn>
      <v-btn color="error" @click="confirmDelete">Delete</v-btn>
      <v-btn color="primary" @click="closeDetailsModal">Close</v-btn>
    </v-card-actions>
  </v-card>
</v-dialog>

<v-dialog v-model="updateModalOpen" max-width="500px">
  <v-card>
    <v-card-title>Update Sharing Room</v-card-title>
    <v-card-text>
      <v-text-field label="Title" v-model="sharingRoomDetails.title"></v-text-field>
      <v-text-field label="Contents" v-model="sharingRoomDetails.contents"></v-text-field>
      <!-- Add more fields as needed -->
    </v-card-text>
    <v-card-actions>
      <v-btn color="primary" @click="updateSharingRoom">Save</v-btn>
      <v-btn color="grey" @click="updateModalOpen = false">Cancel</v-btn>
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
      addTableModalOpen: false,
      editedMember: {
        nickName: '',
        email: '',
        id: '',
        
      },
      tableListModalOpen: false,
    tables: [],
    selectedTables: [],
  editTableModalOpen: false,
  editingTable: {}, // 수정 중인 테이블 데이터
  newTable: {
    deskNum: '',
    seats: '',
    hasTV: 'N',
    isUsable: 'N',

    },
    searchDeskNum: null, // 사용자 입력을 위한 책상 번호
    searchDate: '', // 사용자 입력을 위한 날짜
    reservations: [], // 예약 목록을 저장할 배열
    reservationModalOpen: false, // 예약 목록 모달의 표시 상태를 관리

    sharingRoomModalOpen: false, // 아이템 공유방 모달의 표시 상태
    sharingRooms: [], // 아이템 공유방 목록을 저장할 배열

    selectedSharingRooms: [], // Track selected sharing rooms
    detailsModalOpen: false,
    sharingRoomDetails: null,
    };
  },
      
  methods: {



      closeTableModal() {
    this.tableModalOpen = false;
  },

  openModal(service) {
    this.selectedService = service;
    if (service.title === 'Member Management') {
      this.fetchMemberList();
      this.memberModalOpen = true;
    } else if (service.title === 'Table Management') {
      this.tableModalOpen = true;
    } else if (service.title === 'Item Sharing Room Management') {
      this.fetchSharingRooms(); // 아이템 공유방 목록을 불러옵니다.
      this.sharingRoomModalOpen = true; // 아이템 공유방 모달을 엽니다.
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
    openTableList() {
    this.fetchTables();
    this.tableListModalOpen = true;
  },
  closeTableListModal() {
    this.tableListModalOpen = false;
  },
  fetchTables() {
    const apiUrl = `${process.env.VUE_APP_API_BASE_URL}/desks`;
    axios.get(apiUrl)
      .then(response => {
        this.tables = response.data;
      })
      .catch(error => {
        console.error('Error fetching tables:', error);
      });
  },
  openEditTableModal() {
  if (this.selectedTables.length !== 1) {
    alert("Please select only one table to edit.");
    return;
  }
  this.editingTable = Object.assign({}, this.selectedTables[0]);
  this.editTableModalOpen = true;
},
closeEditTableModal() {
  this.editTableModalOpen = false;
},
updateTable() {
  const apiUrl = `${process.env.VUE_APP_API_BASE_URL}/desk/${this.editingTable.id}/update`;
  axios.patch(apiUrl, this.editingTable)
    .then(() => {
      this.fetchTables(); // 테이블 목록 새로 고침
      this.closeEditTableModal();
      this.selectedTables = [];
    })
    .catch(error => {
      console.error('Error updating table:', error);
      alert("Table update failed. Please check console for details.");
    });
  },
  confirmDelete() {
      console.log("Trying to delete: ",this.selectedTables);
  if (this.selectedTables.length === 0) {
    alert("Please select at least one table to delete.");
    return;
  }
  if (confirm("Are you sure you want to delete the selected table(s)?")) {
    this.selectedTables.forEach(table => {
      this.deleteTable(table.deskNum);
    });
  }
},
deleteTable(deskNum) {
  console.log(`Deleting deskNum: ${deskNum}`);
  const apiUrl = `${process.env.VUE_APP_API_BASE_URL}/desk/${deskNum}/delete`;
  axios.delete(apiUrl)
    .then(() => {
      alert(`Desk number ${deskNum} has been successfully deleted.`);
      this.fetchTables(); // 테이블 목록 새로 고침
      this.selectedTables = []; // 선택된 테이블 목록 초기화
    })
    .catch(error => {
      console.error('Error deleting table:', error);
      alert("Failed to delete the table. Please check the console for details.");
    });  
      },
closeAddTableModal() {
  this.addTableModalOpen = false;
},
openAddTableModal() {
  this.addTableModalOpen = true;
},

createTable() {
  const apiUrl = `${process.env.VUE_APP_API_BASE_URL}/admin/desk/create`;
  const token = localStorage.getItem('Authorization');
  const headers = token ? { 
      Authorization: `Bearer ${token}` ,
  } : {};
  axios.post(apiUrl,this.newTable, {headers} )
    .then(() => {
      alert('Table has been successfully added.');
      this.fetchTables(); // 테이블 목록 새로 고침
      this.closeAddTableModal();
    })
    .catch(error => {
      console.error('Error adding new table:', error);
      alert("Failed to add the table. Please check console for details.");
    });
},
fetchReservations() {
      if (!this.searchDeskNum || !this.searchDate) {
        alert("Both desk number and date are required for search.");
        return;
      }
      const apiUrl = `${process.env.VUE_APP_API_BASE_URL}/reservations?deskNum=${this.searchDeskNum}&date=${this.searchDate}`;
      axios.get(apiUrl)
        .then(response => {
          this.reservations = response.data.result; // 수정: 백엔드 응답에 따라 적절한 접근
        })
        .catch(error => {
          console.error('Error fetching reservations:', error);
        });
    },
    checkReservationStatus() {
      this.reservationModalOpen = true; // 예약 상태 확인 모달을 열기
    },
    fetchSharingRooms() {
    axios.get(`${process.env.VUE_APP_API_BASE_URL}/rooms`)
      .then(response => {
        this.sharingRooms = response.data; // 아이템 공유방 목록을 저장
      })
      .catch(error => {
        console.error('Error fetching sharing rooms:', error);
      });
  },

  fetchSharingRoomDetails(id) {
  const token = localStorage.getItem('Authorization'); // 인증 토큰 가져오기
  const headers = token ? { Authorization: `Bearer ${token}` } : {};

  axios.get(`${process.env.VUE_APP_API_BASE_URL}/user/room/${id}`, { headers })
    .then(response => {
      this.sharingRoomDetails = response.data.result; 
        this.detailsModalOpen = true; // 상세 정보 모달 열기
      })
      .catch(error => {
        console.error('Error fetching sharing room details:', error);
      });
  },
  showSharingRoomDetails() {
    if (this.selectedSharingRooms.length !== 1) {
      alert("Please select one sharing room to view details.");
      return;
    }
    const id = this.selectedSharingRooms[0]; // 선택된 방의 ID
    this.fetchSharingRoomDetails(id);
  },
  closeDetailsModal() {
    this.detailsModalOpen = false; // 상세 정보 모달 닫기
  },
  openUpdateModal() {
    this.updateModalOpen = true;
  },
  updateSharingRoom() {
    // API 호출을 통해 서버에 수정 요청
    axios.patch(`/user/room/${this.sharingRoomDetails.id}/update`, this.sharingRoomDetails)
      .then(() => {
        alert("Sharing Room updated successfully.");
        this.updateModalOpen = false;
        this.detailsModalOpen = false;
        // 필요한 경우 목록 새로고침 등의 추가 작업
      })
      .catch(error => {
        console.error("Error updating sharing room:", error);
      });
  },

  confirmDeleteSharingRoom() {
    if (confirm(`Are you sure you want to delete Sharing Room ${this.sharingRoomDetails.id}?`)) {
      this.deleteSharingRoom();
    }
  },
  deleteSharingRoom() {
    // API 호출을 통해 서버에 삭제 요청
    axios.delete(`/user/room/${this.sharingRoomDetails.id}/delete`)
      .then(() => {
        alert(`Sharing Room ${this.sharingRoomDetails.id} deleted successfully.`);
        this.detailsModalOpen = false; // 삭제 후 상세정보 모달 닫기
        // 목록 새로고침 등의 추가 작업
        this.fetchSharingRooms(); // 예시로 목록 새로고침 추가
      })
      .catch(error => {
        console.error("Error deleting sharing room:", error);
        alert("Error deleting sharing room. Please try again.");
      });
  },
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

/* 추가 */
.grey-background {
background-color: #e0e0e0;
}

</style>
