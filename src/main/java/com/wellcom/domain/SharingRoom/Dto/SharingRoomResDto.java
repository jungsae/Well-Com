package com.wellcom.domain.SharingRoom.Dto;

import com.wellcom.domain.Item.ItemStatus;
import com.wellcom.domain.SharingRoom.SharingRoom;
import lombok.Data;

@Data
public class SharingRoomResDto {
    private Long id;
    private String memberEmail;
    private String title;
    private String contents;
    private int curPeople; //현재 인원 수
    private int cntPeople; //제한 인원 수
    private String itemName;
    private ItemStatus itemStatus;
    private String itemImagePath;

    public static SharingRoomResDto toDto(SharingRoom sharingRoom) {
        SharingRoomResDto dto = new SharingRoomResDto();
        dto.setId(sharingRoom.getId());
        dto.setMemberEmail(sharingRoom.getMember().getEmail());
        dto.setTitle(sharingRoom.getTitle());
        dto.setContents(sharingRoom.getContents());
        dto.setCurPeople(sharingRoom.getCurPeople());
        dto.setCntPeople(sharingRoom.getCntPeople());
        dto.setItemName(sharingRoom.getItem().getName());
        dto.setItemStatus(sharingRoom.getItem().getItemStatus());
        dto.setItemImagePath(sharingRoom.getItem().getImagePath());
        return dto;
    }
}