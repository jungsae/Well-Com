package com.wellcom.domain.Desk;

import com.wellcom.domain.Reservation.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@OptimisticLocking(type = OptimisticLockType.DIRTY)
public class Desk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private int deskNum;
    @Column(nullable = false)
    private int seats;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status hasTV;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status isUsable;
    @Version //Optimistic lock
    private Long version;
    @OneToMany(mappedBy = "desk")
    List<Reservation> reservations = new ArrayList<>();
    @CreationTimestamp
    private LocalDateTime createdTime;
    @UpdateTimestamp
    private LocalDateTime updatedTime;

    @Builder.Default
    private String delYn = "N";
    public void updateIsUsable(String status) {
        this.isUsable = Status.valueOf(status);
    }
    public void setDelYn(String delYn) {
        this.delYn = delYn;
    }
    public void setDeskNum(int deskNum) {
        this.deskNum = deskNum;
    }
    public void setSeats(int seats) {
        this.seats = seats;
    }
    public void setHasTV(Status hasTV) {
        this.hasTV = hasTV;
    }
}