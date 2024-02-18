package com.wellcom.domain.Desk.Controller;

import com.wellcom.domain.Desk.Desk;
import com.wellcom.domain.Desk.Dto.DeskCreateReqDto;
import com.wellcom.domain.Desk.Dto.DeskResDto;
import com.wellcom.domain.Desk.Service.DeskService;
import com.wellcom.domain.Desk.Status;
import com.wellcom.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DeskController {

    private final DeskService deskService;


    @GetMapping("/desks")
    public ResponseEntity<List<DeskResDto>> getAllDesks(@RequestParam(required = false) Status isUsable, @RequestParam(required = false) Status hasTV) {
        List<DeskResDto> deskResDtos = filterDesks(isUsable, hasTV);
        return new ResponseEntity<>(deskResDtos, HttpStatus.OK);
    }

    private List<DeskResDto> filterDesks(Status isUsable, Status hasTV) {
        if (isUsable != null && hasTV != null) {
            return deskService.findAllByUsableAndHasTV(isUsable, hasTV);
        } else if (isUsable != null) {
            return deskService.findAllByUsable(isUsable);
        } else if (hasTV != null) {
            return deskService.findAllByHasTV(hasTV);
        } else {
            return deskService.findAll();
        }
    }

    @PostMapping("/desk/create")
    public ResponseEntity<CommonResponse> roomCreate(@RequestBody DeskCreateReqDto deskCreateReqDto) {
        Desk desk = deskService.createDesk(deskCreateReqDto);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.CREATED, "Desk Information has uploaded", desk.getId()), HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/desk/{deskNum}/delete")
    public ResponseEntity<CommonResponse> deleteDesk(@PathVariable int deskNum) {
        deskService.deleteDesk(deskNum);
        CommonResponse response = new CommonResponse(HttpStatus.OK, "Desk 번호 " + deskNum + "이(가) 성공적으로 삭제 처리되었습니다.", HttpStatus.OK);
        return ResponseEntity.ok(response);
    }
}


