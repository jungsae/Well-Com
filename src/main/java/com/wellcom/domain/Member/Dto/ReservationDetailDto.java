package com.wellcom.domain.Member.Dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReservationDetailDto {
    private Long reservationId;
    private Long deskId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    public ReservationDetailDto(Long reservationId, Long deskId, LocalDateTime startTime, LocalDateTime endTime, String status) {
        this.reservationId = reservationId;
        this.deskId = deskId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }
}
