package com.wellcom.domain.Desk.Dto;

import com.wellcom.domain.Desk.Desk;
import com.wellcom.domain.Desk.Status;
import com.wellcom.domain.Member.Member;
import com.wellcom.domain.Reservation.Dto.ReservationResDto;
import com.wellcom.domain.Reservation.Reservation;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class DeskResDto {
    private Long id;
    private int deskNum;
    private int seats;
    private Status hasTV;
    private Status isUsable;
    private List<DeskResReservationDto> reservations;
    @Data
    public static class DeskResReservationDto{
        private String reservationId;
        private String email;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }

    public static DeskResDto toDeskResDto(Desk desk){
        LocalDate today = LocalDate.now();
        List<DeskResReservationDto> reservationResDtos = new ArrayList<>();

        for (Reservation reservation: desk.getReservations()){
            if (reservation.getStatus().toString().equals("WAITING") && reservation.getStartTime().toLocalDate().equals(today)) {
                DeskResDto.DeskResReservationDto dto = new DeskResDto.DeskResReservationDto();
                dto.setReservationId(reservation.getReservationId());
                dto.setEmail(reservation.getMember().getEmail());
                dto.setStartTime(reservation.getStartTime());
                dto.setEndTime(reservation.getEndTime());
                reservationResDtos.add(dto);
            }
        }

        DeskResDtoBuilder builder = DeskResDto.builder();
        builder.id(desk.getId());
        builder.deskNum(desk.getDeskNum());
        builder.isUsable(desk.getIsUsable());
        builder.hasTV(desk.getHasTV());
        builder.seats(desk.getSeats());
        builder.reservations(reservationResDtos);
        return builder.build();
    }
}
