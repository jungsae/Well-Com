package com.wellcom.global.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessageSendingOperations sendingOperations;
    private final SimpMessagingTemplate template;
    Map<Long, List<UserGameData>> sessionData = new HashMap<>();
    Map<Long, Integer> countMap = new HashMap<>(); // SharingRoom 연결 인원 수

    @MessageMapping("connect")
    public void connect(@Payload RoomNumber roomNumber) {
        System.out.println("connect() 호출");
        System.out.println("roomNumber.getRoomNumber() = " + roomNumber.getRoomNumber());
        Long roomId = roomNumber.getRoomNumber();
        int count = countMap.getOrDefault(roomId, 0);
        countMap.put(roomId, ++count);
        System.out.println("현재 인원 수 : " + countMap.get(roomId));

        if(countMap.get(roomId) == 3){
            System.out.println("전원 입장 완료");
            printAllEnter(roomId);
        }
    }
    @MessageMapping("/sendMessage")
    public void messageCheck(@Payload GameDataReqDto req, SimpMessageHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();

        // roomId에 해당하는 리스트를 가져오거나 새로 생성합니다.
        List<UserGameData> roomData = sessionData.getOrDefault(req.getRoomId(), new ArrayList<>());
        roomData.add(new UserGameData(sessionId, Integer.parseInt(req.getContent())));
        sessionData.put(req.getRoomId(), roomData);

        // 모든 Client가 숫자를 입력하면 처리
        if (roomData.size() == 3) {
            processRoomData(req.getRoomId(), roomData);
        }
    }

    private void processRoomData(Long roomId, List<UserGameData> roomData) {
        System.out.println("호출했음");
        // 중복을 제외한 가장 큰 숫자를 입력한 세션을 찾습니다.
        UserGameData maxInputData = Collections.max(roomData, Comparator.comparingInt(UserGameData::getNum));
        System.out.println("maxInputData = " + maxInputData);

        String destination = "/topic/sharing/" + roomId;
        for (UserGameData data : roomData) {
            String result = data.getSessionId().equals(maxInputData.getSessionId()) ? "축하합니다" : "탈락입니다";
            System.out.println("getSessionId = : " + data.getSessionId() + ", result = " + result);
            template.convertAndSend(destination, result);
//            template.convertAndSendToUser(data.getSessionId(), "/topic/sharing/"+roomId, result);
        }
        // 처리가 끝난 방의 데이터를 삭제합니다.
        System.out.println("게임 끝. 데이터 정리");
        sessionData.put(roomId, null);
        countMap.put(roomId, null);
//        sessionData.remove(roomId);
//        countMap.remove(roomId);
    }

    private void printAllEnter(Long roomId){
        System.out.println("printAllEnter() 호출");
        System.out.println("roomId = " + roomId);

        // 100~1000 숫자 랜덤 생성
        Random rand = new Random();
        int randomNum = rand.nextInt(901) + 100;

        String question = "0 ~" + randomNum + " 사이의 숫자를 입력하세요. <br/> 다른 사람보다 큰 숫자를 입력할수록 좋아요. <br/> 하지만 다른 사람과 중복된 숫자를 입력하면 탈락합니다. <br/> 기회는 한 번이니 신중하게 입력해주세요.";

        System.out.println("question = " + question);
        String destination = "/topic/sharing/" + roomId;

        try{
            sendingOperations.convertAndSend(destination, question);
//            template.convertAndSend(destination, question);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

//    @MessageMapping("disconnect")
//    public void disconnect(@Payload RoomNumber roomNumber) {
//        System.out.println("disconnect() 호출");
//        Long roomId = roomNumber.getRoomNumber();
//        int count = countMap.get(roomId);
//        countMap.put(roomId, --count);
//        System.out.println("현재 인원 수 : " + countMap.get(roomId));
//    }
}