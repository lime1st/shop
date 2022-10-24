package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.exception.OutOfStockException;
import lombok.*;

import javax.persistence.*;
//import java.time.LocalDateTime;

@Entity
@Table(name = "item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //  nullable = false ====> not null
    @Column(nullable = false, length = 50)
    private String itemNm;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(nullable = false)
    private int stockNumber;

    @Lob
    @Column(nullable = false)
    private String itemDetail;

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;

//    BaseEntity 상속
//    private LocalDateTime regTime;
//    private LocalDateTime updateTime;

    //  엔티티 클래스에 비즈니스 로직을 추가한다면 조금 더 객체지향적으로 코딩할 수 있고 재활용할 수 있다.
    //  또한 데이터 변경 포인트를 한군데에서 관리할 수 있다.
    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    //  상품의 재고 관리 - 상품 재고 감소
    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;     //  재고 수량 구하기
        if(restStock < 0){
            throw new OutOfStockException("상품의 재고가 부족합니다." +
                    "(현재 재고 수량: " + this.stockNumber + ")");    //  재고 부족 예외
        }
        this.stockNumber = restStock;   //  주문 후 남은 재고 수량을 상품의 현재 재고 값으로 할당
    }

    //  상품의 재고 관리 - 상품 재고 증가
    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }
}
