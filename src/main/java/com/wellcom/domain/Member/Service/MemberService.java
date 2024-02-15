package com.wellcom.domain.Member.Service;

import com.wellcom.domain.Member.Dto.*;
import com.wellcom.domain.Member.Member;
import com.wellcom.domain.Member.Repository.MemberRepository;
import com.wellcom.domain.Member.Role;
import com.wellcom.domain.Reservation.Repository.ReservationRepository;
import com.wellcom.domain.SharingRoom.Repository.SharingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor

public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SharingRoomRepository sharingRoomRepository;
    private final ReservationRepository reservationRepository;


    public void signUp(MemberSignUpDto memberSignUpDto) throws Exception {

        if (memberRepository.findByEmail(memberSignUpDto.getEmail()).isPresent()) {
            throw new Exception("이미 존재하는 이메일입니다.");
        }

        Member member = Member.builder()
                .email(memberSignUpDto.getEmail())
                .password(memberSignUpDto.getPassword())
                .nickname(memberSignUpDto.getNickname())
                .role(Role.USER)
                .build();

        member.passwordEncode(passwordEncoder);
        memberRepository.save(member);
    }
    public Member findById(Long id) throws EntityNotFoundException {
        Member member = memberRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("검색하신 ID의 Member가 없습니다."));
        return member;
    }

    public MemberDetailResDto findMemberDetail(Long id) throws EntityNotFoundException {
        Member member = this.findById(id);
        String role = member.getRole() == null || member.getRole().equals(Role.USER) ? "일반유저" : "관리자";

        int sharingRoomCount = sharingRoomRepository.countByMemberId(id);
        int reservationCount = reservationRepository.countReservationsByMemberId(id);
        Integer totalReservationTime = reservationRepository.sumReservationTimeByMemberId(id) != null ? reservationRepository.sumReservationTimeByMemberId(id) : 0;

        MemberDetailResDto memberDetailResDto = new MemberDetailResDto();
        memberDetailResDto.setId(member.getId());
        memberDetailResDto.setNickName(member.getNickname());
        memberDetailResDto.setEmail(member.getEmail());
        memberDetailResDto.setRole(role);
        memberDetailResDto.setCreatedTime(member.getCreatedTime());
        memberDetailResDto.setSharingRoomCount(sharingRoomCount);
        memberDetailResDto.setReservationCount(reservationCount);
        memberDetailResDto.setTotalReservationTime(totalReservationTime);

        return memberDetailResDto;
    }

    public void update(Long id, MemberUpdateReqDto memberUpdateReqDto) throws EntityNotFoundException{
        Member member = this.findById(id);
        member.updateMember(memberUpdateReqDto.getNickName(), memberUpdateReqDto.getPassword());
        memberRepository.save(member);
    }

    public Member delete(Long id){
        Member member = memberRepository.findById(id).orElseThrow(()->new EntityNotFoundException("찾을 수 없는 멤버입니다."));
        member.deleteMember();
        return member;
    }




    public MemberListResponse findAll() {
        List<Member> members = memberRepository.findAll();
        List<MemberListResDto> memberListResDtos = members.stream()
                .map(member -> new MemberListResDto(member.getId(), member.getNickname(), member.getEmail()))
                .collect(Collectors.toList());

        long totalMembers = memberRepository.count();

        return new MemberListResponse(memberListResDtos, totalMembers);
    }


    public void blockMember(Long id) throws EntityNotFoundException {
        Member member = this.findById(id);
        member.setBlocked(true);
        memberRepository.save(member);
    }


    public void unblockMember(Long id) throws EntityNotFoundException {
        Member member = this.findById(id);
        member.setBlocked(false);
        memberRepository.save(member);
    }
}
