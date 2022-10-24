package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "cart")
@Getter
@Setter
@ToString
public class Cart extends BaseEntity{

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)   //  회원 엔티티와 1:1 매핑
                                        //  Fetch 전략을 설정하지 않으면 즉시로딩이 기본이다
                                        //  FetchType.EAGER
    @JoinColumn(name = "member_id")     //  매핑할 외래키 지정
    private Member member;

    public static Cart createCart(Member member){
        //  회원 1명당 1개의 장바구니를 갖으므로 처음 장바구니에 상품을 담을 때 해당 회원의 장바구니 생성
        
        Cart cart = new Cart();
        cart.setMember(member);

        return cart;
    }
}
