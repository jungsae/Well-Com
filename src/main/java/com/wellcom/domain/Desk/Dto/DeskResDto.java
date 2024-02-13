package com.wellcom.domain.Desk.Dto;

import com.wellcom.domain.Desk.Desk;
import com.wellcom.domain.Desk.Status;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DeskResDto {
    private Long id;
    private int deskNum;
    private int seats;
    private Status hasTV;
    private Status isUsable;

    public static DeskResDto toDeskResDto(Desk desk){
        DeskResDtoBuilder builder = DeskResDto.builder();
        builder.id(desk.getId());
        builder.deskNum(desk.getDeskNum());
        builder.isUsable(desk.getIsUsable());
        builder.hasTV(desk.getHasTV());
        builder.seats(desk.getSeats());
        return builder.build();
    }
}
