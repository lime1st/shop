package com.shop.entity;

import com.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")                 //  order 가 키워드이기 때문에 orders로 
@Getter
@Setter
public class Order extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;              //  한 명의 회원은 여러 번 주문 가능하므로 주문 엔티티 기준 다대일

    private LocalDateTime orderDate;    //  주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;    //  주문상태

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();
    //  cascade 부모 엔티티의 영속성 상태 변화를 자식 엔티티에 모두 전이한다.
    //  orphanRemoval 고아 객체(부모 엔티티와 연관 관계가 끊어진 자식 엔티티) 제거
    //  고아 객체 제거 기능은 참조하는 곳이 하나일 때만 사용해야 한다.
    //  주문 상품 엔티티와 일대다 매핑, 연관관계의 주인은 OrderItem 이므로
    //  mappedBy 속성을 지정, 속성의 값으로 "order"를 적어준 이유는
    //  OrderItem 에 있는 order 에 의해 관리된다는 의미
    /*
    다대다 매핑은 실무에서는 사용하지 않는다. 관계형 데이터베이스는 정규화된 테이블 2개로 다대다를 표현할 수 없다. 따라서
    녀결 테이블을 생성해서 다대다 관계를 일대다, 다대일 관계로 풀어낸다
    다대다 매핑을 사용하지 않는 이유는 연결 테이블에는 컬럼을 추가할 수 없기 때문이다. 연결 테이블에는 조인 컬럼뿐 아니라
    추가 컬럼이 필요한 경우가 많다. 또한 엔티티를 조회할 때 member 엔티티에서 item 을 조회하면 중간 테이블이 있기 때문에
    어떤 쿼리문이 실행될지 예측하기도 쉽지 않다. 따라서 연결 테이블용 엔티티를 하나 생서한 후 일대다 다대일 관계로 매핑을 하면 된다.
     */

//    BaseEntity 상속
//    private LocalDateTime regTime;
//    private LocalDateTime updateTime;

    //  생성한 주문 상품 객체를 이용하여 주문 객체를 만드는 메소드
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);  //  orderItem 객체를 order 객체의 orderItems에 추가
        orderItem.setOrder(this);   //  Order 엔티티와 OrderItem 엔티티가 양방향 참조 관계 이므로, orderItem 객체에도 order 객체를 세팅
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order = new Order();
        order.setMember(member);
        for(OrderItem orderItem : orderItemList)    //  상품 페이지에서는 1개의 상품을 주문하지만 장바구니에서는 여러 개 주문 할 수 있다
            order.addOrderItem(orderItem);

        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems)
            totalPrice += orderItem.getTotalPrice();

        return totalPrice;
    }

    //  주문 취소 - 취소 상태로 변경
    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;

        for(OrderItem orderItem : orderItems)
            orderItem.cancel();
    }
}
