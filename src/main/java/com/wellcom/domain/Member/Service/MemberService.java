package com.wellcom.domain.Member.Service;

import com.wellcom.domain.Member.Dto.*;
import com.wellcom.domain.Member.Member;
import com.wellcom.domain.Member.Repository.MemberRepository;
import com.wellcom.domain.Member.Role;
import com.wellcom.domain.Record.Repository.RecordRepository;
import com.wellcom.domain.Reservation.Repository.ReservationRepository;
import com.wellcom.domain.Reservation.Reservation;
import com.wellcom.domain.SharingRoom.Repository.SharingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
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
    private final RecordRepository recordRepository;


    public void signUp(MemberSignUpDto memberSignUpDto) throws IllegalArgumentException {

        if (memberRepository.findByEmail(memberSignUpDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        Member member = Member.builder()
                .email(memberSignUpDto.getEmail())
                .password(memberSignUpDto.getPassword())
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
        long wins = recordRepository.countByMemberIdAndIsWinner(member.getId(), "Y");
        long totalParticipations = recordRepository.countByMemberId(member.getId());
        double winRate = (totalParticipations > 0) ? ((double) wins/totalParticipations) * 100 : 0 ;

        // 모든 사용자에게 반환될 정보를 포함하는 MemberDetailResDto 생성
        return MemberDetailResDto.builder()
                .id(member.getId())
                .nickName(member.getNickname())
                .email(member.getEmail())
                .createdTime(member.getCreatedTime())
                .sharingRoomCount(sharingRoomCount)
                .reservationCount(reservationCount)
                .totalReservationTime(totalReservationTime)
                .wins(wins)
                .winRate(winRate)
                .build();
    }


    public void update(Long id, MemberUpdateReqDto memberUpdateReqDto) {
        Member member = findById(id); // findById 메서드를 활용하여 회원 정보 조회

        // 비밀번호 업데이트 로직 제거
        // 닉네임과 이메일만 업데이트하는 로직으로 변경
        member.updateMember(
                memberUpdateReqDto.getNickName(),
                memberUpdateReqDto.getEmail()
        );
        memberRepository.save(member); // 변경된 회원 정보 저장
    }


    public void delete(Long id) {
        Member member = findById(id); // findById 메서드 활용
        member.deleteMember();
    }

    public MemberListTotalResDto findAll() {
        List<MemberListResDto> memberListResDtos = memberRepository.findByDelYn("N").stream()
                .map(member -> MemberListResDto.builder()
                        .id(member.getId())
                        .nickName(member.getNickname())
                        .email(member.getEmail())
                        .build())
                .collect(Collectors.toList());

        // 삭제되지 않은 회원만 카운트
        long totalMembers = memberListResDtos.size();

        return MemberListTotalResDto.builder()
                .members(memberListResDtos)
                .totalMembers(totalMembers)
                .build();
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
    public List<MemberListResDto> findBlockedMembers() {
        return memberRepository.findByIsBlockedTrue().stream()
                .map(member -> MemberListResDto.builder()
                        .id(member.getId())
                        .nickName(member.getNickname())
                        .email(member.getEmail())
                        .build())
                .collect(Collectors.toList());
    }
    public List<ReservationDetailDto> getMemberReservations(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + memberId));

        List<Reservation> reservations = reservationRepository.findByMember(member);

        return reservations.stream().map(reservation -> new ReservationDetailDto(
                reservation.getId(),
                reservation.getDesk().getId(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus().name()
        )).collect(Collectors.toList());
    }
}

