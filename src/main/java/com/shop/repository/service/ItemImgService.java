package com.shop.repository.service;

import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception{
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //  파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            
            //  DB에 저장될 경로
            imgUrl = "/images/items/" + imgName;
        }

        //  상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
    }

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception{

        if(!itemImgFile.isEmpty()){     //  상품 이미지를 수정한 경우 업데이트
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)    //  이미지 아이디로 기존 이미지 조회
                    .orElseThrow(EntityNotFoundException::new);

            //  기존 이미지가 있을 경우 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())){
                fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();

            //  업데이트한 상품 이미지 파일을 업로드
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/items/" + imgName;

            //  변경된 상품 이미지 세팅, savedItemImg 엔티티는 현재 영속 상태이므로 데이터를 변경하는 것만으로 변경 감지
            //  기능이 동작하여 트랜잭션이 끝날 때 update 쿼리가 실행, 여기서 중요한 것은 엔티티가 영속 상태여야 한다는 것
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }
}
