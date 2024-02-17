package com.wellcom.domain.SharingRoom.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
public class SharingRoomReqDto {
    private String title;
    private String contents;
    private int cntPeople;
    private String itemName;
    private String itemImagePath;
    private MultipartFile itemImage;
}
