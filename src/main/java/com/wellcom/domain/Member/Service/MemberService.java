package com.wellcom.domain.Member.Service;

import com.wellcom.domain.Member.Dto.*;
import com.wellcom.domain.Member.Member;
import com.wellcom.domain.Member.Repository.MemberRepository;
import com.wellcom.domain.Member.Role;
import com.wellcom.domain.Reservation.Repository.ReservationRepository;
import com.wellcom.domain.SharingRoom.Repository.SharingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public MemberDetailResDto findMemberDetail(Long id) {
        Member member = findById(id); // 회원 정보 조회

        // 회원과 관련된 추가 정보 조회
        int sharingRoomCount = sharingRoomRepository.countByMemberId(id);
        int reservationCount = reservationRepository.countReservationsByMemberId(id);
        Integer totalReservationTimeWrapper = reservationRepository.sumReservationTimeByMemberId(id);
        int totalReservationTime = totalReservationTimeWrapper != null ? totalReservationTimeWrapper : 0;

        // 모든 사용자에게 반환될 정보를 포함하는 MemberDetailResDto 생성
        return MemberDetailResDto.builder()
                .id(member.getId())
                .nickName(member.getNickname())
                .email(member.getEmail())
                .createdTime(member.getCreatedTime())
                .sharingRoomCount(sharingRoomCount)
                .reservationCount(reservationCount)
                .totalReservationTime(totalReservationTime)
                .build();
    }


    public void update(Long id, MemberUpdateReqDto memberUpdateReqDto) {
        Member member = findById(id); // findById 메서드 활용
        member.updateMember(memberUpdateReqDto.getNickName(), passwordEncoder.encode(memberUpdateReqDto.getPassword()));
        memberRepository.save(member);
    }

    public void delete(Long id) {
        Member member = findById(id); // findById 메서드 활용
        member.deleteMember();
    }

    public MemberListResponse findAll() {
        List<MemberListResDto> memberListResDtos = memberRepository.findAll().stream()
                .map(member -> new MemberListResDto(member.getId(), member.getNickname(), member.getEmail()))
                .collect(Collectors.toList());

        long totalMembers = memberRepository.count();
        return new MemberListResponse(memberListResDtos, totalMembers);
    }

    public void blockMember(Long id) {
        Member member = findById(id); // findById 메서드 활용
        member.setBlocked(true);
        memberRepository.save(member);
    }

    public void unblockMember(Long id) {
        Member member = findById(id); // findById 메서드 활용
        member.setBlocked(false);
        memberRepository.save(member);
    }
}

