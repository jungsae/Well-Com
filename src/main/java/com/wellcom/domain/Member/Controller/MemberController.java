package com.wellcom.domain.Member.Controller;

import com.wellcom.domain.Member.Dto.*;
import com.wellcom.domain.Member.Member;
import com.wellcom.domain.Member.Service.MemberService;
import com.wellcom.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    @CrossOrigin(originPatterns = "*")
    public String signUp(@RequestBody MemberSignUpDto memberSignUpDto) throws Exception {
        log.info(memberSignUpDto.toString());
        memberService.signUp(memberSignUpDto);
        return "회원가입 성공";
    }

    @DeleteMapping("admin/member/delete/{id}")
    public ResponseEntity<CommonResponse> memberDelete(@PathVariable Long id){
        Member member = memberService.delete(id);
        return new ResponseEntity<>(
                new CommonResponse(HttpStatus.OK,
                        "회원이 성공적으로 삭제되었습니다.",member.getId()),HttpStatus.OK);
    }



    @GetMapping("admin/member/detail/{id}")
    public ResponseEntity<?> memberDetail(@PathVariable Long id) {
        try {
            MemberDetailResDto memberDetail = memberService.findMemberDetail(id);
            return ResponseEntity.ok(memberDetail);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("admin/member/list")
    public ResponseEntity<MemberListResponse> memberList() {
        MemberListResponse memberListResponse = memberService.findAll();
        return ResponseEntity.ok(memberListResponse);
    }


    @PostMapping("member/{id}/update")
    public ResponseEntity<?> memberUpdate(@PathVariable Long id, @RequestBody MemberUpdateReqDto memberUpdateReqDto) {
        try {
            memberService.update(id, memberUpdateReqDto);
            return ResponseEntity.ok().body(Map.of("message", "성공적으로 수정되었습니다.", "memberId", id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "멤버가 없습니다.", "memberId", id));
        }
    }


    // 회원 차단
    @PutMapping("admin/member/{id}/block")
    public ResponseEntity<?> blockMember(@PathVariable Long id) {
        try {
            memberService.blockMember(id);
            return ResponseEntity.ok().body("회원이 성공적으로 차단되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 회원 차단 해제
    @PutMapping("admin/member/{id}/unblock")
    public ResponseEntity<?> unblockMember(@PathVariable Long id) {
        try {
            memberService.unblockMember(id);
            return ResponseEntity.ok().body("회원 차단이 성공적으로 해제되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


}

