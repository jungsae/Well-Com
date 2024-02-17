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
            log.info("예약된 작업시작: 예약번호" + reservationId + "처리 / " + deskNum + "번 테이블 사용가능");
            deskService.updateDeskStatus(deskNum, "Y");
            updateReservationStatus(reservationId, "COMPLETE");
            }, Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));
        scheduledTasks.put(reservation.getReservationId(), future);
    }
    public void scheduleReservationStart(Reservation reservation) {
        LocalDateTime startTime = reservation.getStartTime();
        int deskNum = reservation.getDesk().getDeskNum();
        String reservationId = reservation.getReservationId();

        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            log.info("예약 시작 작업: 예약번호 " + reservationId + " / " + deskNum + "번 테이블 사용 시작");
            deskService.updateDeskStatus(deskNum, "N");
            updateReservationStatus(reservationId, "USING");
        }, Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
        scheduledTasks.put("start_" + reservation.getReservationId(), future);
    }
    private void updateReservationStatus(String reservationId, String status) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("예약번호 "+ reservationId + "를 찾을 수 없습니다."));
        reservation.setStatus(status);
        reservationRepository.save(reservation);
    }
    public void cancelFutureSchedule(String reservationId) {
        System.out.println("---------------------사이즈: "+scheduledTasks.size()+" -----------------");
        ScheduledFuture<?> future = scheduledTasks.get(reservationId);
        if (future != null){
            future.cancel(false);
            scheduledTasks.remove(reservationId);
            System.out.println("---------------------사이즈: "+scheduledTasks.size()+" -----------------");
        }
    }
}
