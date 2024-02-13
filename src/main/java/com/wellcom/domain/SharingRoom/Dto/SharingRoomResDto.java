package com.wellcom.domain.SharingRoom.Dto;

import com.wellcom.domain.Item.ItemStatus;
import com.wellcom.domain.SharingRoom.SharingRoom;
import lombok.Data;

@Data
public class SharingRoomResDto {
    private Long id;
    private Long memberId;
    private String title;
    private String contents;
    private int cntPeople;
    private String itemName;
    private ItemStatus itemStatus;
    private String itemImagePath;

    public static SharingRoomResDto toDto(SharingRoom sharingRoom) {
        SharingRoomResDto dto = new SharingRoomResDto();
        dto.setId(sharingRoom.getId());
        dto.setMemberId(sharingRoom.getMember().getId());
        dto.setTitle(sharingRoom.getTitle());
        dto.setContents(sharingRoom.getContents());
        dto.setCntPeople(sharingRoom.getCntPeople());
        dto.setItemName(sharingRoom.getItem().getName());
        dto.setItemStatus(sharingRoom.getItem().getItemStatus());
        dto.setItemImagePath(sharingRoom.getItem().getImagePath());
        return dto;
    }
}