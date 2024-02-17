package com.wellcom.domain.Member.Dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MemberListTotalResDto {
    private List<MemberListResDto> members;
    private long totalMembers;
}
