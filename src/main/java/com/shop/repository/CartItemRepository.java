package com.shop.repository;

import com.shop.dto.CartDetailDto;
import com.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long>{
    // 장바구니에 들어갈 상품을 저장하거나 조회

    //  카트 아이디와 상품 아이디를 이용해 상품이 장바구니에 들어있는 지 조회
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    //  장바구니 페이지에 전달할 CartDetailDto 리스트를 쿼리 하나로 조회하는 JPQL 문을 작성한다.
    //  연관 관계 매핑을 지연 로딩으로 설정할 경우 엔티티에 매핑된 다른 엔티티를 조회할 때 추가적으로 쿼리문이 실행된다.
    //  따라서 성능 최적화가 필요할 경우 아래 코드와 같이 DTO 의 생성자를 이용하여 반환 값으로 DTO 객체를 생성할 수 있다.
    @Query("select new com.shop.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) "
            + "from CartItem ci, ItemImg im "
            + "join ci.item i "
            + "where ci.cart.id = :cartId "
            + "and im.item.id = ci.item.id "
            + "and im.repimgYn = 'Y' "
            + "order by ci.regTime desc "
    )
    List<CartDetailDto> findCartDetailDtoList(Long cartId);
}
