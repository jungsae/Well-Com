package com.wellcom.domain.Member.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값이 있는 필드는 제외
public class MemberDetailResDto {
    private Long id;
    private String nickName;
    private String email;
    private String password;
    private String role;
    private LocalDateTime createdTime;
    private int sharingRoomCount;
    private int reservationCount;
    private int totalReservationTime;
}