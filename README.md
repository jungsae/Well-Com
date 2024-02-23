## APIs
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
### Member
|   End point   	| HTTP Method 	| Description 	| Status 	|
|:-------------:	|:-----------:	|:-----------:	|:------:	|
|  /member/detailByEmail?email={email} 	|     GET    	|   이메일로 회원 찾기  	|  201  	|
| /member/{id}/reservations 	|     GET    	|    테이블 예약 내역 조회   	|  200  	|
| /member/{id}/detail 	|     GET    	|    맴버 상세 정보   	|  200  	|
| /member/{id}/update 	|     POST    	|    회원 정보 수정   	|  200  	|
### Desk
|          End point         	| HTTP Method 	| Description 	| Status 	|
|:--------------------------:	|:-----------:	|:-----------:	|:------:	|
|          /desks          	|     GET     	| 테이블 전체 조회 	|  200  	|
### Room
|                    End point                   	| HTTP Method 	|     Description     	| Status 	|
|:----------------------------------------------:	|:-----------:	|:-------------------:	|:------:	|
|        /user/room/create        	|  POST  	| 나눔 방 생성 	|  200  	|
|        /rooms        	|  GET  	| 나눔 방 전체조회 	|  200  	|
|        /user/room/{id}        	|  GET  	| 나눔 방 상세조회 	|  200  	|
|        /user/room/{id}/update        	|  PATCH  	| 나눔 방 수정 	|  200  	|
|        /user/room/{id}/delete        	|  DELETE  	| 나눔 방 삭제 	|  200  	|
### Reservation
|          End point         	| HTTP Method 	| Description 	| Status 	|
|:--------------------------:	|:-----------:	|:-----------:	|:------:	|
|          /reservation/now          	|     POST     	| 즉시 사용 	|  201  	|
|          /reservation/create          	|     POST     	| 예약 생성 	|  201  	|
|          /reservation/{reservationId}/cancel          	|     PATCH     	| 예약 취소 	|  200  	|
|          /reservations          	|     GET     	| 예약 전체 조회 	|  200  	|
