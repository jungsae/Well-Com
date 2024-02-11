package com.wellcom.domain.SharingRoom.Controller;

import com.wellcom.domain.SharingRoom.Dto.SharingRoomCreateReqDto;
import com.wellcom.domain.SharingRoom.Dto.SharingRoomCreateResDto;
import com.wellcom.domain.SharingRoom.Service.SharingRoomService;
import com.wellcom.domain.SharingRoom.SharingRoom;
import com.wellcom.global.common.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SharingRoomController {
    private final SharingRoomService sharingRoomService;
    @Autowired
    public SharingRoomController(SharingRoomService sharingRoomService) {
        this.sharingRoomService = sharingRoomService;
    }

    @PostMapping("/room/create")
    public ResponseEntity<CommonResponse> roomCreate(@RequestBody SharingRoomCreateReqDto sharingRoomCreateReqDto){
        SharingRoom sharingRoom = sharingRoomService.create(sharingRoomCreateReqDto);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.CREATED, "sharingRoom is successfully created", sharingRoom.getId()), HttpStatus.CREATED);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/rooms")
    public List<SharingRoomCreateResDto> orderList(){
        return sharingRoomService.findAll();
    }
}
