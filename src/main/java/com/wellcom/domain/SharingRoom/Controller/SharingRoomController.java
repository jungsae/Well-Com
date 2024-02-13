package com.wellcom.domain.SharingRoom.Controller;

import com.wellcom.domain.SharingRoom.Dto.SharingRoomReqDto;
import com.wellcom.domain.SharingRoom.Dto.SharingRoomResDto;
import com.wellcom.domain.SharingRoom.Service.SharingRoomService;
import com.wellcom.domain.SharingRoom.SharingRoom;
import com.wellcom.global.common.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SharingRoomController {
    private final SharingRoomService sharingRoomService;
    @Autowired
    public SharingRoomController(SharingRoomService sharingRoomService) {
        this.sharingRoomService = sharingRoomService;
    }

    @PostMapping("/room/create")
    public ResponseEntity<CommonResponse> roomCreate(SharingRoomReqDto sharingRoomReqDto){
        SharingRoom sharingRoom = sharingRoomService.create(sharingRoomReqDto);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.CREATED, "sharingRoom is successfully created", sharingRoom.getId()), HttpStatus.CREATED);
    }

    @GetMapping("/rooms")
    public List<SharingRoomResDto> roomList(){
        return sharingRoomService.findAll();
    }

    @GetMapping("/room/{id}")
    public ResponseEntity<CommonResponse> roomDetail(@PathVariable Long id){
        SharingRoomResDto sharingRoomResDto = sharingRoomService.findById(id);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "sharingRoom"+ id +" is successfully found", sharingRoomResDto), HttpStatus.OK);
    }

    @PatchMapping("/room/{id}/update")
    public ResponseEntity<CommonResponse> roomUpdate(@PathVariable Long id, SharingRoomReqDto sharingRoomReqDto){
        SharingRoomResDto sharingRoomResDto = sharingRoomService.update(id, sharingRoomReqDto);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "sharingRoom successfully updated", sharingRoomResDto), HttpStatus.OK);
    }

    @DeleteMapping("/room/{id}/delete")
    public ResponseEntity<CommonResponse> roomDelete(@PathVariable Long id){
        sharingRoomService.delete(id);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "sharingRoom"+ id +" is successfully deleted", id), HttpStatus.OK);
    }
}