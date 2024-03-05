package com.wellcom.domain.Desk.Service;

import com.wellcom.domain.Desk.Desk;
import com.wellcom.domain.Desk.Dto.DeskCreateReqDto;
import com.wellcom.domain.Desk.Dto.DeskResDto;
import com.wellcom.domain.Desk.Dto.DeskUpdateReqDto;
import com.wellcom.domain.Desk.Repository.DeskRepository;
import com.wellcom.domain.Desk.Status;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "findAllDesks", key = "#root.methodName", cacheManager = "cacheManager")
    public List<DeskResDto> findAll() {
        List<Desk> desks = deskRepository.findAllWithReservations();
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