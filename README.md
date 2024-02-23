#Well-Com ( Well-Being Community )

![001](https://github.com/yujeong-shin/Well-Com/assets/57553339/1f4dbbd0-200c-4a09-b152-ad101362e918)
<br/>
![002](https://github.com/yujeong-shin/Well-Com/assets/57553339/d6858695-d4c9-4d63-a1cf-b0965e8f38b8)
<br/><br/>

## 🎈프로젝트 관리<br/> 
[프로젝트 관리 - Trello](https://trello.com/b/XVOEdX9v/well-com)
<br/><br/>

## 👯‍♂️팀원<br/> 
🤠 정세한 - 테이블 예약 서비스<br/> 
🐼 손정민 - Token, OAuth 사용한 로그인 서비스 및 Chain Filter<br/> 
🐸 신유정 - 실시간 무료 나눔 서비스<br/> 
🐻 조훈 - 관리자 및 MyPage<br/>
<br/><br/>

## 🎈프로젝트 개요
[WellCom프로젝트 개요.pdf](https://github.com/yujeong-shin/Well-Com/files/14381854/WellCom.pdf)
<br/><br/>

## 🎈프로젝트 요구사항 정의서
[WellCom요구사항정의서.pdf](https://github.com/yujeong-shin/Well-Com/files/14381856/WellCom.pdf)
<br/><br/>

## 🎈Entity Relationship Diagram (ERD)
![WellComERD](https://github.com/yujeong-shin/Well-Com/assets/57553339/dd36da92-fc1a-45d2-87d5-bdd5f5e5f611)
<br/><br/>

## 🎈API 설계 <br/>
### Admin
|   End point   	| HTTP Method 	| Description 	| Status 	|
|:-------------:	|:-----------:	|:-----------:	|:------:	|
| /admin/member/{id}/block 	|     PUT    	|    회원 차단   	|  200  	|
| /admin/member/{id}/unblock 	|     PUT    	|    회원 차단 해제   	|  200  	|
| /admin/member/{id}/delete 	|     DELETE    	|    유저 삭제   	|  200  	|
| /admin/member/blocked 	|     GET    	|    차단 회원 조회  	|  200  	|
| /member/list 	|     GET    	|    유저 리스트 출력   	|  200  	|
| /admin/desk/create 	|     POST    	|    유저 리스트 출력   	|  201  	|
| /desk/{deskNum}/delete 	|     DELETE    	|    테이블 삭제   	|  201  	|
| admin/desk/{deskNum}/update 	|     PATCH    	|    테이블 정보수정   	|  201  	|
| admin/desk/create 	|     PATCH    	|    테이블 추가   	|  201  	|
 <br/>
### Member
|   End point   	| HTTP Method 	| Description 	| Status 	|
|:-------------:	|:-----------:	|:-----------:	|:------:	|
|  /member/detailByEmail?email={email} 	|     GET    	|   이메일로 회원 찾기  	|  201  	|
| /member/{id}/reservations 	|     GET    	|    테이블 예약 내역 조회   	|  200  	|
| /member/{id}/detail 	|     GET    	|    맴버 상세 정보   	|  200  	|
| /member/{id}/update 	|     POST    	|    회원 정보 수정   	|  200  	|
 <br/>
### Desk
|          End point         	| HTTP Method 	| Description 	| Status 	|
|:--------------------------:	|:-----------:	|:-----------:	|:------:	|
|          /desks          	|     GET     	| 테이블 전체 조회 	|  200  	|
 <br/>
### Room
|                    End point                   	| HTTP Method 	|     Description     	| Status 	|
|:----------------------------------------------:	|:-----------:	|:-------------------:	|:------:	|
|        /user/room/create        	|  POST  	| 나눔 방 생성 	|  200  	|
|        /rooms        	|  GET  	| 나눔 방 전체조회 	|  200  	|
|        /user/room/{id}        	|  GET  	| 나눔 방 상세조회 	|  200  	|
|        /user/room/{id}/update        	|  PATCH  	| 나눔 방 수정 	|  200  	|
|        /user/room/{id}/delete        	|  DELETE  	| 나눔 방 삭제 	|  200  	|
 <br/>
### Reservation
|          End point         	| HTTP Method 	| Description 	| Status 	|
|:--------------------------:	|:-----------:	|:-----------:	|:------:	|
|          /reservation/now          	|     POST     	| 즉시 사용 	|  201  	|
|          /reservation/create          	|     POST     	| 예약 생성 	|  201  	|
|          /reservation/{reservationId}/cancel          	|     PATCH     	| 예약 취소 	|  200  	|
|          /reservations          	|     GET     	| 예약 전체 조회 	|  200  	|
<br/><br/>

## 🎈중점 기술
###✅ OAuth <br/>
Google과 KaKao 등 외부 소셜 계정을 기반으로 간단히 회원가입 및 로그인<br/> 
###✅ Scheduler <br/>
예약된 시간에 등록된 작업을 스케줄러가 자동으로 상태처리 및 관리<br/> 
###✅ S3(Simple Storage Service) <br/>
언제 어디서나 데이터를 저장할 수 있도록 해주는 AWS의 저장소. <br/>
사용자가 업로드 하는 상품 이미지를 로컬 저장소가 아닌 s3에 저장하면서 관리 및 접근 용이<br/> 
###✅ Web Socket <br/>
하나의 HTTP 접속으로 양방향 메시지를 자유롭게 주고받을 수 있는 프로토콜.<br/> 
무료 나눔이 진행될 때 실시간으로 각 클라이언트의 입력 값을 받고, 결과 메시지 리턴<br/> 
###✅ SMTP <br/>
인터넷에서 이메일을 보내기 위해 이용되는 프로토콜<br/> 
###✅ Vuetify <br/>
vue 에서 사용할 수 있는 용이한 UI 라이브러리. 다양한 화면 크기에 맞게 반응 
<br/><br/>

## 🎈프로젝트 시연

