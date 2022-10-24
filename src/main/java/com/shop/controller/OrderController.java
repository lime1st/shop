package com.shop.controller;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.repository.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {
    //  주문 요청 처리, 상품 주문에서 웹 페이지의 새로 고침 없이 서버에 주문을 요청하기 위해 비동기로 처리

    private final OrderService orderService;

    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto,
                                              BindingResult bindingResult, Principal principal){
        //  스프링에서 비동기 처리시 RequestBody 와 ResponseBody 어노테이션 사용
        //  RequestBody: HTTP 요청의 본문 body 에 담긴 내용을 자바 객체로 전달
        //  ResponseBody: 자바 객체를 HTTP 요청의 body 로 전달
        //  OrderDto View 에서 넘어온 주문 정보


        if(bindingResult.hasErrors()){      //  주문 정보를 받는 orderDto 객체에 데이터 바인딩 시 에러가 있는지 검사
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for(FieldError fieldError : fieldErrors)
                sb.append(fieldError.getDefaultMessage());

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);   //  에러 반환
        }

        String email = principal.getName(); //
        Long orderId;

        try{
            orderId = orderService.order(orderDto, email);
        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    //  구매 이력 조회
    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page")Optional<Integer> page, Principal principal, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);

        //  현재 로그인한 회원은 이메일과 페이징 객체를 파라미터로 전달하여 화면에 전달한 주문 목록 데이터를 리턴 받음
        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "order/orderHist";
    }

    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal){

        //  주문 번호를 받아서 주문 취소 로직을 호출하는 메소드
        
        //  js 에서 취소할 주문 번호 조작이 가능하므로 검증
        if(!orderService.validateOrder(orderId, principal.getName()))
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);

        orderService.cancelOrder(orderId);  //  주문 취소

        //  orderId가 Long 이므로
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}
