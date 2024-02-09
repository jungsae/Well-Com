package com.wellcom.domain.SharingRoom.Dto;

import lombok.Data;

@Data
public class SharingRoomCreateReqDto {
    private String title;
    private String contents;
    private int cntPeople;
    // Item으로 조립할 값들
    private String itemName;
    private String itemImagePath;
    // Status는 default=SHARING
}
