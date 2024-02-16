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
    public MemberDetailResDto findMemberDetail(Long id, Authentication authentication) {
        Member member = findById(id);

        int sharingRoomCount = sharingRoomRepository.countByMemberId(id);
        int reservationCount = reservationRepository.countReservationsByMemberId(id);
        int totalReservationTime = reservationRepository.sumReservationTimeByMemberId(id);

        // 관리자 권한 확인
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (isAdmin) {
            // 관리자인 경우 모든 정보 반환
            return new MemberDetailResDto(
                    member.getId(),
                    member.getNickname(),
                    member.getEmail(),
                    member.getPassword(),
                    member.getRole().toString(),
                    member.getCreatedTime(),
                    sharingRoomCount,
                    reservationCount,
                    totalReservationTime
            );
        } else {
            // 일반 사용자인 경우 비밀번호와 역할 정보 제외
            return new MemberDetailResDto(
                    member.getId(),
                    member.getNickname(),
                    member.getEmail(),
                    null, // 비밀번호 null 처리
                    null, // 역할 정보 null 처리
                    member.getCreatedTime(),
                    sharingRoomCount,
                    reservationCount,
                    totalReservationTime
            );
        }
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
