
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
                    <th>선택</th>
                    <th>닉네임</th>
                    <th>이메일</th>
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
                <v-btn :disabled="selectedMembers.length !== 1" color="primary" @click="editMember">정보수정</v-btn>
                <v-btn :disabled="selectedMembers.length !== 1" color="error" @click="deleteMember">멤버삭제</v-btn>
                <p v-if="selectedMembers.length !== 1">삭제 할 회원을 한명만 선택해주세요. </p>
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
              <p>실행 할 기능을 아래 항목 중에서 선택해주세요.</p>
            </v-card-text>
            <v-card-actions>
              <v-btn color="primary" @click="openTableList">테이블보기</v-btn>
              <v-btn color="primary" @click="openAddTableModal">테이블 생성</v-btn>

              <v-btn color="primary" @click="checkReservationStatus">예약상태확인</v-btn>
              <v-btn color="primary" @click="closeTableModal">닫기</v-btn>
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


    <!-- Add a new card for 나눔관리 -->
    <v-col cols="12" md="4" lg="3">
      <v-card @click="openModal({ title: '나눔관리', description: '공유방 관리 및 상세 조회' })" class="service-card" elevation="12" hover>
        <v-card-title class="headline text-center">나눔관리</v-card-title>
        <v-card-text class="body-1 d-flex align-center justify-center">공유방 관리 및 상세 조회</v-card-text>
      </v-card>
    </v-col>

    <v-dialog v-model="sharingRoomModalOpen" max-width="800px">
          <v-card>
            <v-card-title>Sharing Room Management</v-card-title>
            <v-card-text>
              <v-list>
                <v-list-item v-for="(room, index) in sharingRooms" :key="index">
                  <v-list-item-action>
                    
                    <v-checkbox v-model="selectedSharingRooms" :value="room.id"></v-checkbox>
                  </v-list-item-action>
                  <v-list-item-content>
                    <v-divider :key="'divider-' + room.id" v-if="index < sharingRooms.length - 1"></v-divider>
                    <v-list-item-title>제목 {{ room.title }}</v-list-item-title>
                    <v-divider :key="'divider-' + room.id" v-if="index < sharingRooms.length - 1"></v-divider>
                    <v-list-item-title>내용 {{ room.contents }}</v-list-item-title>
                    <v-divider :key="'divider-' + room.id" v-if="index < sharingRooms.length - 1"></v-divider>
                    
                    
                    
                    
                    
                    
                    
                    <!-- Optionally display more room details here -->
                  </v-list-item-content>
                </v-list-item>
                <v-divider :key="'divider-' + room.id" v-if="index < sharingRooms.length - 1"></v-divider>
              </v-list>
            </v-card-text>
            <v-card-actions>
              <v-btn color="primary" @click="showSharingRoomDetails">상세조회</v-btn>
              <v-btn color="primary" @click="sharingRoomModalOpen = false">Close</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>

        <v-dialog v-model="detailsModalOpen" max-width="600px">
  <v-card>
    <v-card-title class="headline">Sharing Room Details</v-card-title>
    <v-card-text>
      <div>
        <p>제목: {{ sharingRoomDetails.title }}</p>
        <p>내용: {{ sharingRoomDetails.contents }}</p>
        <p>현재 참여 인원: {{ sharingRoomDetails.curPeople }}</p>
        <p>최대 인원: {{ sharingRoomDetails.cntPeople }}</p>
        <p>아이템 이름: {{ sharingRoomDetails.itemName }}</p>
        <p>아이템 상태: {{ sharingRoomDetails.itemStatus }}</p>
        <!-- Add more details as needed -->
      </div>
    </v-card-text>
    <v-card-actions>
      <v-btn color="primary" @click="detailsModalOpen = false">Close</v-btn>
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
          { title: '멤버관리', description: '회원목록 및 상세조회' },
          { title: '테이블관리', description: '테이블설정 및 상세조회' },
          
              ],
      memberModalOpen: false,
      tableModalOpen: false,
      editModalOpen: false, // Added for the edit member modal
      selectedService: {},
      memberList:[],
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
    reservationModalOpen: false,

    sharingRooms: [], // To store sharing rooms data
      sharingRoomModalOpen: false, // To control the sharing room modal visibility
   // 예약 목록 모달의 표시 상태를 관리
   
      selectedSharingRooms: [], // IDs of selected sharing rooms, assuming multi-select is possible
      sharingRoomDetails: {}, // The details of the selected sharing room
      detailsModalOpen: false,
  };
  },
      
  methods: {


      closeTableModal() {
    this.tableModalOpen = false;
  },
  openModal(service) {
  this.selectedService = service;
  if (service.title === '멤버관리') {
    this.fetchMemberList();
    this.memberModalOpen = true;
  } else if (service.title === '테이블관리') {
    this.tableModalOpen = true;
  } else if (service.title === '나눔관리') { // Adjusted to match the service title
    this.fetchSharingRooms();
    this.sharingRoomModalOpen = true;
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
  //   const token = localStorage.getItem('Authorization'); // 인증 토큰 가져오기
  // const headers = token ? { Authorization: `Bearer ${token}` } : {};

  // axios.get(`${process.env.VUE_APP_API_BASE_URL}/user/room/${id}`, { headers })
  //   .then(response => {

  //   fetchSharingRoomDetails(id) {
  // const token = localStorage.getItem('Authorization'); // 인증 토큰 가져오기
  // const headers = token ? { Authorization: `Bearer ${token}` } : {};

  // axios.get(`${process.env.VUE_APP_API_BASE_URL}/user/room/${id}`, { headers })
  //   .then(response => {
  //     this.sharingRoomDetails = response.data.result; 
  //       this.detailsModalOpen = true; // 상세 정보 모달 열기
  //     })
  //     .catch(error => {
  //       console.error('Error fetching sharing room details:', error);
  //     });
  // },
    fetchMemberList() {
      const token = localStorage.getItem('Authorization');
      const headers = token ? { Authorization: `Bearer ${token}` } : {};
      axios.get(`${process.env.VUE_APP_API_BASE_URL}/admin/member/list`, { headers })
      .then(response => {
        this.memberList = response.data.result.members})
        .catch(error => {
          console.error('Error fetching sharing room details:', error);
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
  const token = localStorage.getItem('Authorization');
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
  const apiUrl = `${process.env.VUE_APP_API_BASE_URL}/member/${this.editedMember.id}/update`;

  try {
    await axios.post(apiUrl, {
      nickName: this.editedMember.nickName,
      email: this.editedMember.email
    }, { headers }); // 헤더를 요청과 함께 전송

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
    // 에러 처리 로직 추가 (예: 사용자에게 에러 메시지 표시)
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

  // 토큰 값을 가져오고, 헤더에 추가
  const token = localStorage.getItem('Authorization');
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
  
  axios.delete(apiUrl, { headers }) // headers를 요청과 함께 전송
    .then(() => {
      // 멤버 리스트에서 삭제된 멤버 제거
      this.memberList = this.memberList.filter(member => member.id !== memberId);
      this.closeModal(); // 모달 닫기
      this.selectedMembers = []; // 선택된 멤버 초기화
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

  if (confirm("Are you sure you want to delete the selected table(s)?")) {
    this.selectedTables.forEach(table => {
      this.deleteTable(table.deskNum);
    });
  }
},
deleteTable(deskNum) {
  if (!confirm("Are you sure you want to delete this table?")) {
    return;
  }
  
  // 토큰 값을 가져오고, 헤더에 추가
  const token = localStorage.getItem('Authorization');
  const headers = { Authorization: `Bearer ${token}` };
  
  const apiUrl = `${process.env.VUE_APP_API_BASE_URL}/desk/${deskNum}/delete`;
  
  axios.delete(apiUrl, { headers }) // headers를 요청과 함께 전송
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
  const token = localStorage.getItem('Authorization');
  const headers = token ? { Authorization: `Bearer ${token}` } : {};

  axios.get(`${process.env.VUE_APP_API_BASE_URL}/admin/rooms`, { headers })
    .then(response => {
      // Assuming the response directly contains an array of SharingRoomResDto objects
      this.sharingRooms = response.data;
      this.sharingRoomModalOpen = true;
    })
    .catch(error => console.error('Error fetching sharing rooms:', error));
},


    fetchSharingRoomDetails(id) {
      const token = localStorage.getItem('Authorization');
      const headers = token ? { Authorization: `Bearer ${token}` } : {};
      
      axios.get(`${process.env.VUE_APP_API_BASE_URL}/user/room/${id}`, { headers })
        .then(response => {
          // Logic to display sharing room details
          // This could involve setting data properties and/or opening another modal
          console.log(response.data.result);
          this.sharingRoomDetails = response.data.result;
          this.detailsModalOpen = true; // Example to log the details, replace with actual implementation
        })
        .catch(error => console.error('Error fetching sharing room details:', error));
    },
    showSharingRoomDetails() {
      // Ensure that exactly one sharing room is selected
      if (this.selectedSharingRooms.length !== 1) {
        alert("Please select one sharing room to view details.");
        return;
      }

      // Retrieve the ID of the selected sharing room
      const id = this.selectedSharingRooms[0];
      // Fetch the details of the selected sharing room
      this.fetchSharingRoomDetails(id);
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
