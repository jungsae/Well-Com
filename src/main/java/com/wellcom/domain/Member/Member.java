package com.wellcom.domain.Member;

import com.wellcom.domain.Record.Record;
import com.wellcom.domain.Reservation.Reservation;
import com.wellcom.domain.SharingRoom.SharingRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;
    private String email;
    private String password;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String socialType;

    private String socialId;

    private String refreshToken; // 리프레시 토큰

    private LocalDateTime createdTime;

    @Builder.Default
    private String delYn = "N";

    private int count;

    @OneToMany(mappedBy = "member")
    List<Record> histories = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    List<SharingRoom> sharingRooms = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    List<Reservation> reservations = new ArrayList<>();

    @Builder.Default
    private boolean isBlocked = false;

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public void deleteMember() {
        this.delYn = "Y";
    }

    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void authorizeUser() {
        this.role = Role.USER;
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }

    public boolean isSocial() {
        return this.socialId != null;
    }

    public void updateMember(String nickname, String email) {
        if (nickname != null && !nickname.isEmpty()) {
            this.nickname = nickname;
        }
        // 비밀번호 업데이트 로직 제거
        if (email != null && !email.isEmpty()) {
            this.email = email;
        }
    }
}
