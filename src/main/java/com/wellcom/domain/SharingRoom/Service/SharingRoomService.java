package com.wellcom.domain.SharingRoom.Service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.wellcom.domain.Item.Item;
import com.wellcom.domain.Item.ItemStatus;
import com.wellcom.domain.Member.Member;
import com.wellcom.domain.Member.Repository.MemberRepository;
import com.wellcom.domain.SharingRoom.Dto.SharingRoomReqDto;
import com.wellcom.domain.SharingRoom.Dto.SharingRoomResDto;
import com.wellcom.domain.SharingRoom.Repository.SharingRoomRepository;
import com.wellcom.domain.SharingRoom.SharingRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SharingRoomService {
    private final SharingRoomRepository sharingRoomRepository;
    private final MemberRepository memberRepository;
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public SharingRoom create(SharingRoomReqDto sharingRoomReqDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("not found email"));

        String fileUrl = null;
        MultipartFile file = sharingRoomReqDto.getItemImage();
        if (file != null && !file.isEmpty()) {
            fileUrl = saveFile(file);
        }

        Item item = Item.builder()
                .name(sharingRoomReqDto.getItemName())
                .imagePath(fileUrl)
                .itemStatus(ItemStatus.SHARING)
                .build();

        // SharingRoom 객체가 생성될 때 Item 객체도 함께 생성 : Cascading PERSIST
        SharingRoom sharingRoom = SharingRoom.builder()
                .member(member)
                .title(sharingRoomReqDto.getTitle())
                .contents(sharingRoomReqDto.getContents())
                .cntPeople(sharingRoomReqDto.getCntPeople())
                .item(item).build();

        return sharingRoomRepository.save(sharingRoom);
    }

    public String saveFile(MultipartFile file) {
        String fileUrl;
        if(file.isEmpty()){
            fileUrl = null;
        }
        else{
            try {
                fileUrl = UUID.randomUUID() + "_" + file.getOriginalFilename();
                ObjectMetadata metadata= new ObjectMetadata();
                metadata.setContentType(file.getContentType());
                metadata.setContentLength(file.getSize());
                amazonS3Client.putObject(bucket, fileUrl, file.getInputStream(), metadata);
//            fileUrl = amazonS3Client.getUrl(bucket, fileUrl).toString();
            } catch (IOException e) {
                throw new IllegalArgumentException("image is not available");
            }
        }
        return fileUrl;
    }

    public List<SharingRoomResDto> findAll() {
        List<SharingRoom> sharingRooms = sharingRoomRepository.findAll();
        return sharingRooms.stream().map(o -> SharingRoomResDto.toDto(o)).collect(Collectors.toList());
    }

    public List<SharingRoomResDto> findByDelYn(){
        List<SharingRoom> sharingRooms = sharingRoomRepository.findByDelYn("N");
        return sharingRooms.stream().map(o -> SharingRoomResDto.toDto(o)).collect(Collectors.toList());
    }

    public SharingRoomResDto findById(Long id){
        // 로그인 한 사용자만 Sharing Room 상세 조회 가능
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("not found email"));
        SharingRoom sharingRoom = sharingRoomRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("sharingRoom not found"));
        return SharingRoomResDto.toDto(sharingRoom);
    }

    public SharingRoomResDto findByIdForGame(Long id){
        // 어차피 프론트에서 로그인한 사용자만 들어오니까 인증 확인 없이 SharingRoom 반환
        SharingRoom sharingRoom = sharingRoomRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("sharingRoom not found"));
        return SharingRoomResDto.toDto(sharingRoom);
    }


    public SharingRoomResDto update(Long id, SharingRoomReqDto sharingRoomReqDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        SharingRoom sharingRoom = sharingRoomRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("sharingRoom not found"));

        // sharingRoom의 member의 email과 token에서 가져온 email과 비교
        // 자신이 생성한 SharingRoom이 아니라면 접근 에러
        if(!sharingRoom.getMember().getEmail().equals(email)){
            throw new AccessDeniedException("Access denied");
        }

        String fileUrl = null;
        // 입력(SharingRoomReqDto)에서 들어온 itemImage가 null
        if(sharingRoomReqDto.getItemImage() == null){
            // DB에 image값이 있다면 image 값 유지
            if(sharingRoom.getItem().getImagePath() != null){
                fileUrl = sharingRoom.getItem().getImagePath();
            }
            // DB에 image값이 없다면 null 값 유지
            else {
                fileUrl = null;
            }
        }
        // dto에서 들어온 itemImage가 not null => 기존 이미지 삭제 후 재업로드
        else{
            // 기존 s3에 업로드 된 이미지 삭제
            if(sharingRoom.getItem().getImagePath() != null){
                try{
                    amazonS3Client.deleteObject(bucket, sharingRoom.getItem().getImagePath());
                } catch (SdkClientException e){
                    log.error("Not able to delete from S3: " + e.getMessage(), e);
                }
            }
            // 새로운 이미지 s3에 재업로드
            MultipartFile file = sharingRoomReqDto.getItemImage();
            if (file != null && !file.isEmpty()) {
                fileUrl = saveFile(file);
            }
        }

        sharingRoom.getItem().updateItem(sharingRoomReqDto.getItemName(), fileUrl);

        sharingRoom.updateSharingRoom(
                sharingRoomReqDto.getTitle(),
                sharingRoomReqDto.getContents(),
                sharingRoomReqDto.getCntPeople()
        );

        return SharingRoomResDto.toDto(sharingRoom);
    }

    public void delete(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        SharingRoom sharingRoom = sharingRoomRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("sharingRoom not found"));

        // 자신이 생성한 SharingRoom이 아니라면 접근 에러
        if(!sharingRoom.getMember().getEmail().equals(email)){
            throw new AccessDeniedException("Access denied");
        }

        // 이미 나눔이 끝난 SharingRoom에 대해서는 삭제 불가능
        if(sharingRoom.getItem().getItemStatus() == ItemStatus.DONE){
            throw new IllegalArgumentException("already canceled sharing room");
        }

        // s3에 업로드 된 경우에만 이미지 삭제
        if(sharingRoom.getItem().getImagePath() != null){
            try{
                amazonS3Client.deleteObject(bucket, sharingRoom.getItem().getImagePath());
            } catch (SdkClientException e){
                log.error("Not able to delete from S3: " + e.getMessage(), e);
            }
        }

        //ItemStatue=DONE 설정
        sharingRoom.getItem().doneItem();
        //SharingRoom delYn="Y" 설정
        sharingRoom.deleteSharingRoom();
    }
    public void completeGame(Long id) {
        SharingRoom sharingRoom = sharingRoomRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("sharingRoom not found"));

        //ItemStatue=DONE 설정
        sharingRoom.getItem().doneItem();
    }
}