package com.wellcom.domain.Member.Dto;

import lombok.Data;

import java.util.List;
@Data

//총 회원구하는 dto
public class MemberListResponse {
    private List<MemberListResDto> members;
    private long totalMembers;

    public MemberListResponse(List<MemberListResDto> members, long totalMembers) {
        this.members = members;
        this.totalMembers = totalMembers;
    }
}