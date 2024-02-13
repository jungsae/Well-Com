package com.wellcom.domain.Desk.Service;

import com.wellcom.domain.Desk.Desk;
import com.wellcom.domain.Desk.Dto.DeskCreateReqDto;
import com.wellcom.domain.Desk.Dto.DeskResDto;
import com.wellcom.domain.Desk.Repository.DeskRepository;
import com.wellcom.domain.Desk.Status;
import org.springframework.stereotype.Service;

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
        List<Desk> desks = deskRepository.findAll();
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
}
