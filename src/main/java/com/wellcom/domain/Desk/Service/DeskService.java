package com.wellcom.domain.Desk.Service;

import com.wellcom.domain.Desk.Desk;
import com.wellcom.domain.Desk.Dto.DeskCreateReqDto;
import com.wellcom.domain.Desk.Dto.DeskResDto;
import com.wellcom.domain.Desk.Dto.DeskUpdateReqDto;
import com.wellcom.domain.Desk.Repository.DeskRepository;
import com.wellcom.domain.Desk.Status;
import com.wellcom.domain.Member.Dto.MemberUpdateReqDto;
import com.wellcom.domain.Member.Member;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeskService {
    private final DeskRepository deskRepository;

    public DeskService(DeskRepository deskRepository) {
        this.deskRepository = deskRepository;
    }

    public Desk createDesk(DeskCreateReqDto deskCreateReqDto) {
        Desk desk = Desk.builder()
                .deskNum(deskCreateReqDto.getDeskNum())
                .hasTV(deskCreateReqDto.getHasTV())
                .seats(deskCreateReqDto.getSeats())
                .isUsable(deskCreateReqDto.getIsUsable())
                .build();
        return deskRepository.save(desk);
    }

    private List<DeskResDto> mapDesksToDeskResDtoList(List<Desk> desks) {
        return desks.stream()
                .map(desk -> DeskResDto.toDeskResDto(desk))
                .collect(Collectors.toList());
    }

    public List<DeskResDto> findAll() {
        List<Desk> desks = deskRepository.findAll()
                .stream()
                //현재 테이블의 상태가 N인것만 조회하도록함요
                .filter(desk -> "N".equals(desk.getDelYn()))
                .collect(Collectors.toList());
        return mapDesksToDeskResDtoList(desks);
    }

    public List<DeskResDto> findAllByUsableAndHasTV(Status isUsable, Status hasTV) {
        List<Desk> desks = deskRepository.findByIsUsableAndHasTV(isUsable, hasTV);
        return mapDesksToDeskResDtoList(desks);
    }

    public List<DeskResDto> findAllByUsable(Status isUsable) {
        List<Desk> desks = deskRepository.findByIsUsable(isUsable);
        return mapDesksToDeskResDtoList(desks);
    }

    public List<DeskResDto> findAllByHasTV(Status hasTV) {
        List<Desk> desks = deskRepository.findByHasTV(hasTV);
        return mapDesksToDeskResDtoList(desks);
    }

    public void updateDeskStatus(int deskNum, String status) {
        Desk desk = deskRepository.findByDeskNum(deskNum).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 테이블 번호입니다."));
        desk.updateIsUsable(status);
    }


    public void deleteDesk(int deskNum) {
        Desk desk = deskRepository.findByDeskNum(deskNum)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 테이블 번호입니다."));
        deskRepository.delete(desk);
    }
    public Desk updateDesk(Long id, DeskUpdateReqDto deskUpdateReqDto) {
        Desk desk = deskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 Desk가 존재하지 않습니다. id=" + id));

        desk.updateIsUsable(deskUpdateReqDto.getIsUsable());

        desk.setDeskNum(deskUpdateReqDto.getDeskNum());
        desk.setSeats(deskUpdateReqDto.getSeats());
        desk.setHasTV(Status.valueOf(deskUpdateReqDto.getHasTV()));

        return deskRepository.save(desk);
    }
}