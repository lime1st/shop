package com.shop.repository;

import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    //  Querydsl 을 Spring Data Jpa 와 함께 사용하기 위해서는 사용자 정의 리포지토리를 정의해야 한다
    //  1. 사용자 정의 인터페이스 작성
    //  2. 사용자 정의 인터페이스 구현
    //  3. Spring Data Jpa 리포지토리에서 사용자 정의 인터페이스 상속
    
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
