package com.wellcom.domain.Member.Dto;

import lombok.Data;

import java.util.List;

@Data
public class MemberListResDto {
    private Long id;
    private String nickName;
    private String email;

    public MemberListResDto(Long id, String nickname, String email) {
        this.id = id;
        this.nickName = nickname;
        this.email = email;
    }
}