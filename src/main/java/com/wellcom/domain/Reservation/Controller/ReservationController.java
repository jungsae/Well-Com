package com.wellcom.domain.Reservation.Controller;

import com.wellcom.domain.Reservation.Dto.ReservationCreateReqDto;
import com.wellcom.domain.Reservation.Dto.ReservationResDto;
import com.wellcom.domain.Reservation.Reservation;
import com.wellcom.domain.Reservation.Service.ReservationService;
import com.wellcom.global.common.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReservationController {
    private final ReservationService reservationService;
    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResDto>> getReservations(@RequestParam int deskNum, @RequestParam String date) {
        return new ResponseEntity<>(reservationService.getReservations(deskNum, date),HttpStatus.OK);
    }
    @PostMapping("/reservation/create")
    public ResponseEntity<CommonResponse> saveReservation(@RequestBody ReservationCreateReqDto reservationCreateReqDto){
        Reservation reservation = reservationService.saveReservation(reservationCreateReqDto);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.CREATED, "예약 성공", reservation.getReservationId()),HttpStatus.CREATED);
    }
    @PatchMapping("/reservation/{reservation_id}/cancel")
    public ResponseEntity<CommonResponse> cancelReservation(@PathVariable String reservation_id){
        return new ResponseEntity<>(new CommonResponse(HttpStatus.NO_CONTENT, "예약이 취소되었습니다", reservationService.cancelReservation(reservation_id)), HttpStatus.NO_CONTENT);
    }
}
