package com.wellcom.domain.Reservation;

import com.wellcom.domain.Desk.Desk;
import com.wellcom.domain.Member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reservationId;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch= FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "desk_id")
    @ManyToOne(fetch=FetchType.LAZY)
    private Desk desk;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private int reservationTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private int cntPeople;

    @CreationTimestamp
    private LocalDateTime createdTime;

    @UpdateTimestamp
    private LocalDateTime updatedTime;

    public void setStatus(String status){
        this.status = Status.valueOf(status);
    }

}
