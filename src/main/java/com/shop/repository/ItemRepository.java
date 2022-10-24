package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

//  Predicate 는 '이 조건이 맞다'고 판단하는 근거를 함수로 제공하는 함수형 인터페이스
//  QuerydslPredicateExecutor 는 전달한 조건에 맞는 값을 반환해줌
public interface ItemRepository extends JpaRepository<Item, Long>,
        QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {

    //  ItemNm 으로 검색
    List<Item> findByItemNm(String itemNm);

    //  상품명과 상품 상세 설명을 OR 조건을 이용해 조회
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    //  price 보다 작은 값 조회
    List<Item> findByPriceLessThan(Integer price);

    //  price 보다 작은 값 조회 후 Desc
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    //  JPQL 을 이용한 쿼리문 작성: Java Persistence(Programmable) Query Language
    //  JPQL(@Query 사용) 의 단점: JPQL 문법으로 문자열을 입력하기 때문에 잘못 입력하면 컴파일 시점에 에러를 발견할 수 없다.
    //  Querydsl 을 사용하면 JPQL 을 코드로 작성하도록 해준다.
    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc ")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    //  기존의 데이터베이스에서 사용하던 쿼리를 사용, 이 방법은 특정 DBMS 에 종속되므로 사용에 주의!!
    @Query(value = "select * from item i where i.item_detail like %:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);

}
