package com.wellcom.domain.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
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
}

//import com.encore.happycommunity.member.dto.MemberCreateReqDto;
//        import com.encore.happycommunity.sharingroom.domain.SharingRoom;
//        import com.encore.happycommunity.record.domain.Record;
//        import com.encore.happycommunity.reservation.domain.Reservation;
//        import lombok.AllArgsConstructor;
//        import lombok.Builder;
//        import lombok.Getter;
//        import lombok.NoArgsConstructor;
//
//        import javax.persistence.*;
//        import java.util.ArrayList;
//        import java.util.List;

//@Entity
//@Getter
//@NoArgsConstructor
//public class Member {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String name;
//
//    @Column(unique = true, nullable = false)
//    private String email;
//
//    @Column(nullable = false)
//    private String password;
//
//    @Column(nullable = false)
//    private String phoneNumber = "010-0000-0000";
//
//    @Column(nullable = false)
//    private int generation = 3;
//
//    private String profileImage;
//
//    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
//    private Role role = Role.USER;
//
//    private String socialAuthCode;
//
//    @Column(nullable = false)
//    private String delYn = "N";
//
//    @OneToMany(mappedBy ="member")
//    List<Record> histories = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member")
//    List<SharingRoom> sharingRooms = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member")
//    List<Reservation> reservations = new ArrayList<>();
//
//    @Builder
//    public Member(MemberCreateReqDto dto){
//        this.name = dto.getName();
//        this.email = dto.getEmail();
//        this.password = (dto.getPassword());
//    }
//}