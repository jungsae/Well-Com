package com.wellcom.domain.Desk;

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
    @CreationTimestamp
    private LocalDateTime createdTime;
    @UpdateTimestamp
    private LocalDateTime updatedTime;
    public void updateIsUsable(String status) {
        this.isUsable = Status.valueOf(status);
    }
}