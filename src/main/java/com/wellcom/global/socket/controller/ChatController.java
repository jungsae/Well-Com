package com.wellcom.global.socket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellcom.domain.SharingRoom.Service.SharingRoomService;
import com.wellcom.global.common.CommonResponse;
import com.wellcom.global.socket.entity.GameDataReqDto;
import com.wellcom.global.socket.entity.RoomNumber;
import com.wellcom.global.socket.entity.UserGameData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SharingRoomService sharingRoomService;
    private final SimpMessageSendingOperations sendingOperations;
    private final SimpMessagingTemplate template;
    private final ObjectMapper objectMapper = new ObjectMapper();
    Map<Long, List<UserGameData>> sessionData = new HashMap<>();
    Map<Long, Integer> countMap = new HashMap<>(); // SharingRoom 연결 인원 수
    int randomNum = 0;

    // 세션별 결과를 저장하는 Map
    private Map<String, String> sessionResults = new HashMap<>();

    // 세션별 결과를 설정하는 메서드
    public void setSessionResult(String sessionId, String result) {
        sessionResults.put(sessionId, result);
    }

    @MessageMapping("connect")
    public void connect(@Payload RoomNumber roomNumber) {
        System.out.println("connect() 호출");
        System.out.println("roomNumber.getRoomNumber() = " + roomNumber.getRoomNumber());
        Long roomId = roomNumber.getRoomNumber();
        int count = countMap.getOrDefault(roomId, 0);
        countMap.put(roomId, ++count);
        System.out.println("현재 인원 수 : " + countMap.get(roomId));

        int limitPeople = sharingRoomService.findByIdForGame(roomId).getCntPeople();
        if(countMap.get(roomId) == limitPeople){
            System.out.println("전원 입장 완료");
            printAllEnter(roomId);
        }
    }
    @MessageMapping("/sendMessage")
    public void messageCheck(@Payload GameDataReqDto req, SimpMessageHeaderAccessor accessor) throws JsonProcessingException {
        String sessionId = accessor.getSessionId();

        // roomId에 해당하는 리스트를 가져오거나 새로 생성합니다.
        Long roomId = req.getRoomId();
        List<UserGameData> roomData = sessionData.getOrDefault(roomId, new ArrayList<>());
        roomData.add(new UserGameData(sessionId, Integer.parseInt(req.getContent())));
        sessionData.put(roomId, roomData);

//        int limitPeople = sharingRoomService.findByIdForGame(roomId).getCntPeople();
//        // 모든 Client가 숫자를 입력하면 처리
//        if (roomData.size() == limitPeople) {
//            processRoomData(req.getRoomId(), roomData);
//        }
        int currentPeople = countMap.get(roomId);
        // 현재 참여 중인 Client가 숫자를 입력하면 처리
        if (roomData.size() == currentPeople) {
            processRoomData(req.getRoomId(), roomData);
        }
    }

    private void processRoomData(Long roomId, List<UserGameData> roomData) throws JsonProcessingException {
        System.out.println("호출했음");
        // 중복을 제외한 가장 큰 숫자를 입력한 세션을 찾습니다.
        // randomNum 보다 큰 값이 들어온 데이터를 제외합니다.
        List<UserGameData> filteredRoomData = roomData.stream()
                .filter(data -> data.getNum() <= randomNum)
                .collect(Collectors.toList());

        // 중복을 제외한 가장 큰 숫자를 입력한 세션을 찾습니다.
        UserGameData maxInputData = Collections.max(filteredRoomData, Comparator.comparingInt(UserGameData::getNum));
        System.out.println("maxInputData = " + maxInputData);

        String destination = "/queue/sharing/" + roomId;
        for (UserGameData data : roomData) {
            String result = data.getSessionId().equals(maxInputData.getSessionId()) ? "축하합니다" : "탈락입니다";
            System.out.println("getSessionId = : " + data.getSessionId() + ", result = " + result);

            // sessionResults Map에 세션별 결과를 설정합니다.
            setSessionResult(data.getSessionId(), result);

             template.convertAndSend(destination, objectMapper.writeValueAsString(sessionResults));
            // template.convertAndSendToUser(data.getSessionId(), "/topic/sharing/"+roomId, result);
        }
        // 처리가 끝난 방의 데이터를 삭제합니다.
        System.out.println("게임 끝. 데이터 정리");
        sessionData.put(roomId, null);
        countMap.put(roomId, null);

        // DB에 반영
        sharingRoomService.completeGame(roomId);
    }

    private void printAllEnter(Long roomId){
        System.out.println("printAllEnter() 호출");
        System.out.println("roomId = " + roomId);

        // 100~1000 숫자 랜덤 생성
        Random rand = new Random();
        randomNum = rand.nextInt(901) + 100;

        String question = "0 ~ " + randomNum + " 사이의 숫자를 입력하세요. \n 다른 사람보다 큰 숫자를 입력할수록 좋아요. \n 하지만 다른 사람과 중복된 숫자를 입력하면 탈락합니다. \n 기회는 한 번이니 신중하게 입력해주세요.";
        String destination = "/topic/sharing/" + roomId;

        try{
            sendingOperations.convertAndSend(destination, question);
//            template.convertAndSend(destination, question);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @MessageMapping("disconnect")
    public void disconnect(@Payload RoomNumber roomNumber) {
        System.out.println("disconnect() 호출");
        Long roomId = roomNumber.getRoomNumber();
        int count = countMap.get(roomId);
        countMap.put(roomId, --count);
        System.out.println("현재 인원 수 : " + countMap.get(roomId));
    }

    @GetMapping("/room/{id}/cntPeople")
    public ResponseEntity<CommonResponse> getCount(@PathVariable Long roomId) {
        int count = countMap.get(roomId);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "successfully get cntPeople", count), HttpStatus.OK);
    }
}