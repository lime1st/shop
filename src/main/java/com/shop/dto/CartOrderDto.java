package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartOrderDto {
    //  장바구니 페이지에서 주문할 상품 데이터를 전달하는 DTO

    private Long cartItemId;

    //  장바구니에서 여러 개의 상품을 주문하므로 List 로 가지고 있도록 한다
    private List<CartOrderDto> cartOrderDtoList;
}
