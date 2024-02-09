package com.wellcom.domain.Member.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSignUpDto {
    private String email;
    private String password;
    private String nickname;
    private int age;
    private String city;
}
