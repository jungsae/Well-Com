package com.wellcom.domain.Reservation.Dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReservationResDto {
    private int deskNum;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
