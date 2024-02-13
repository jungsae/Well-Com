package com.wellcom.domain.Reservation.Dto;

import com.wellcom.domain.Member.Member;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationCreateReqDto
{
    private Member member;
    private int deskNum;
    private int cntPeople;
    private LocalDateTime startTime;
    private int minutes;
}
