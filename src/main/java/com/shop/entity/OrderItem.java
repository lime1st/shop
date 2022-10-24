package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class OrderItem extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;                  //  하나의 상품은 여러 주문 상품으로 들어갈 수 있다

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;                //  한 번의 주문에 여러 개의 상품을 주문할 수 있다

    private int orderPrice;

    private int count;

//    BaseEntity 를 상속 받아 필요 없게 되었다.
//    private LocalDateTime regTime;
//
//    private LocalDateTime updateTime;

    //  주문할 상품과 주문 수량을 통해 OrderItem 객체를 만드는 메소드
    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);        //  주문 상품
        orderItem.setCount(count);      //  주문 수량
        orderItem.setOrderPrice(item.getPrice());   //  현재 시간 기준으로 상품 가격을 주문 가격으로 세팅

        item.removeStock(count);        //  주문 수량만큼 상품의 재고 수량 감소
        return orderItem;
    }

    //  주문 가격과 주문 수량을 곱해서 해당 상품을 주문한 총 가격을 계산
    public int getTotalPrice(){
        return orderPrice * count;
    }
    
    //  주문 취소-재고에 취소 갯수만큼 추가
    public void cancel(){
        this.getItem().addStock(count);
    }

}
