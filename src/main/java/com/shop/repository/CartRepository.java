package com.shop.repository;

import com.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CartRepository extends JpaRepository<Cart, Long> {

    //  현재 로그인한 회원의 Cart 엔티티 찾기
    Cart findByMemberId(Long memberId);
}
