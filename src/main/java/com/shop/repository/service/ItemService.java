package com.shop.repository.service;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        //  상품 등록
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        //  이미지 등록
        for(int i = 0; i < itemImgFileList.size(); i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if(i == 0)  itemImg.setRepimgYn("Y");
            else        itemImg.setRepimgYn("N");

            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }

        return item.getId();
    }

    //  등록된 상품을 불러오는 메소드
    //  데이터를 읽어오는 트랜잭션을 읽기 전용으로 설정, 이럴 경우 JPA 가 더티체킹(변경감지)을 하지 않아 성능 향상
    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId){

        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);     //  이미지 조회
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for(ItemImg itemImg : itemImgList){     //  조회한 ItemImg 엔티티 -> ItemImgDto 객체로
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId) //  아이디로 상품 엔티티 조회
                .orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);

        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        //  상품 수정
        Item item = itemRepository.findById(itemFormDto.getId())    //  상품 등록 화면에서 전달받은 상품 아이디로 조회
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);   //  상품 엔티티 업데이트

        List<Long> itemImgIds = itemFormDto.getItemImgIds();        //  상품 이미지 아이디 리스트 조회

        //  이미지 등록
        for(int i = 0; i < itemImgFileList.size(); i++){
            //  상품 이미지를 업데이트하기 위해서 updateItemImg() 메소드에 상품 이미지 아이디와, 상품 이미지 파일 정보를 파라미터로 전달
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }

        return  item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){

        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    //  메인 페이지 보여줄 상품 데이터를 조회하는 메소드
    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){

        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }
}
