package com.wellcom.reservation.service;

import com.wellcom.domain.Reservation.Dto.ReservationCreateReqDto;
import com.wellcom.domain.Reservation.Service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class ReservationServiceConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @BeforeEach
    public void setup() {
        // 테스트 사용자 인증 정보 설정
        Authentication auth = new UsernamePasswordAuthenticationToken("user@test.com", null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @Transactional
    public void testConcurrentReservationCreation() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        ReservationCreateReqDto dto = new ReservationCreateReqDto();
        // dto 설정 (예약 정보 설정)

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    reservationService.saveReservation(dto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(1, TimeUnit.MINUTES);

        // 테스트 검증 로직
    }
}
