package com.wellcom.domain.Reservation.Service;

import com.wellcom.domain.Desk.Desk;
import com.wellcom.domain.Desk.Service.DeskService;
import com.wellcom.domain.Reservation.Repository.ReservationRepository;
import com.wellcom.domain.Reservation.Reservation;
import com.wellcom.domain.Reservation.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
@Slf4j
public class ReservationScheduler {
    private final TaskScheduler taskScheduler;
    private final DeskService deskService;
    private final ReservationRepository reservationRepository;
    @Autowired
    public ReservationScheduler(TaskScheduler taskScheduler, DeskService deskService, ReservationRepository reservationRepository) {
        this.taskScheduler = taskScheduler;
        this.deskService = deskService;
        this.reservationRepository = reservationRepository;
    }
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    public void scheduleReservationEnd(Reservation reservation){
        LocalDateTime endTime = reservation.getEndTime();
        int deskNum = reservation.getDesk().getDeskNum();
        String reservationId = reservation.getReservationId();

        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            log.info("작업시작: 예약번호" + reservationId + "완료처리 / 현재 " + deskNum + "번 테이블 사용가능");
            updateReservationStatus(reservationId, "COMPLETE");
            deskService.updateDeskStatus(deskNum, "Y");
            }, Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));
        scheduledTasks.put("end_" + reservation.getReservationId(), future);
    }
    public void scheduleReservationStart(Reservation reservation) {
        LocalDateTime startTime = reservation.getStartTime();
        int deskNum = reservation.getDesk().getDeskNum();
        String reservationId = reservation.getReservationId();

        // 사용요청이 WAITING 상태로 예약 될 경우 시작과 종료 작업이 뒤죽박죽 섞여 들어가서 데이터베이스의 무결성 파괴
        // 예약 시작 작업들을 예약 종료 작업보다 1초 딜레이를 줘서 양쪽 작업들끼리의 그룹 블록 생성
        LocalDateTime delayStartTime = startTime.plusSeconds(1);
        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            log.info("작업시작: 예약번호 " + reservationId + " / " + deskNum + "번 테이블 사용 시작");
            updateReservationStatus(reservationId, "USING");
            deskService.updateDeskStatus(deskNum, "N");
        }, Date.from(delayStartTime.atZone(ZoneId.systemDefault()).toInstant()));
        scheduledTasks.put("start_" + reservation.getReservationId(), future);
    }
    private void updateReservationStatus(String reservationId, String status) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("예약번호 "+ reservationId + "를 찾을 수 없습니다."));
        reservation.setStatus(status);
        reservationRepository.save(reservation);
    }
    public void cancelFutureSchedule(String reservationId) {
//        예약 취소시 예약의 시작과 종료를 ConcurrentHashMap에 넣을때 동일한 예약 id로 넣어서 하나만 삭제되어지는 에러가 발생
//        각 예약에 시작과 종료 작업 태그를 예약번호에 추가하고 HashMap에서 전부 삭제
        String startTaskKey = "start_" + reservationId;
        String endTaskKey = "end_" + reservationId;

        ScheduledFuture<?> startFuture = scheduledTasks.get(startTaskKey);
        if (startFuture != null) {
            startFuture.cancel(false);
            scheduledTasks.remove(startTaskKey);
            log.info("예약 시작 작업 취소됨: " + startTaskKey);
        }

        ScheduledFuture<?> endFuture = scheduledTasks.get(endTaskKey);
        if (endFuture != null) {
            endFuture.cancel(false);
            scheduledTasks.remove(endTaskKey);
            log.info("예약 종료 작업 취소됨: " + endTaskKey);
        }

        log.info("현재 스케줄된 작업 수: " + scheduledTasks.size());
    }
}
