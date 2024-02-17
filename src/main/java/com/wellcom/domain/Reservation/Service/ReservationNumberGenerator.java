package com.wellcom.domain.Reservation.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReservationNumberGenerator
{
    // 예약 번호 생성 메서드
    public static String generateReservationNumber() {
        // 현재 날짜 및 시간을 가져옴
        LocalDateTime now = LocalDateTime.now();
        // 날짜를 형식에 맞게 포맷팅 (예: yyyyMMddHHmmss)
        String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        // 짧은 값을 랜덤으로 생성
        int shortValue = (int) (Math.random() * 1000);
        // 날짜와 짧은 값을 조합하여 예약 번호 생성
        return formattedDate + String.format("%03d", shortValue);
    }
}
