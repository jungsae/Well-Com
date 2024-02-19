package com.wellcom.domain.Desk.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeskUpdateReqDto {
    private int deskNum;
    private int seats;
    private String hasTV; // Status Enum을 String으로 받아 처리
    private String isUsable; // Status Enum을 String으로 받아 처리
}