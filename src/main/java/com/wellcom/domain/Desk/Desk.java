package com.wellcom.domain.Desk;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Desk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int seats;

    @Column(nullable = false)
    private String hasTV;

    @Column(nullable = false)
    private String isUsable;
}