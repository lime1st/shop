package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "cart_item")
public class CartItem extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  //  하나의 장바구니는 여러 개의 상품을 담을 수 있으므로 다대일
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)  //  하나의 상품은 여러 장바구니의 장바구니 상품으로 담길 수 있으므로 다대일
    @JoinColumn(name = "item_id")
    private Item item;

    private int count;                 //  같은 상품을 장바구니에 몇 개 담을지 저장

    //  장바구니에 담을 상품 엔티티를 생성하는 메소드와 장바구느에 담을 수량을 증가시켜 주는 메소드
    //  null 값을 제어하는 요소를 추가해 주는 것이 좋다.
    public static CartItem createCartItem(Cart cart, Item item, int count){
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);

        return cartItem;
    }

    //  장바구니에 기존에 담겨 있는 상품인데, 추가로 담을 때 더해줌
    public void addCount(int count){
        this.count += count;
    }

    //  장바구니에서 상품의 수량으 ㄹ변경할 경우 실시간으로 해당 회원의 장바구니 상품의 수량도 변경하기 위해
    //  현재 장바구니에 담겨 있는 수량을 변경하는 메소드
    public void updateCount(int count){
        this.count = count;
    }
    
}
