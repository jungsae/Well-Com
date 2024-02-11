package com.wellcom.domain.Desk.Dto;

import com.wellcom.domain.Desk.Status;
import lombok.Data;
@Data
public class DeskCreateReqDto {
    private int deskNum;
    private int seats;
    private Status hasTV;
    private Status isUsable;
}
