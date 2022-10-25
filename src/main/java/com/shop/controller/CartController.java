package com.shop.controller;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.repository.service.CartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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

@Controller
@Api(value = "CartController", tags = "Cart API")
@RequiredArgsConstructor
public class CartController {
    //  장바구니와 관련된 요청 처리하는 콘트롤러

    private final CartService cartService;

    @ApiOperation(value = "카트 상품")
    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                                              BindingResult bindingResult, Principal principal){
        if(bindingResult.hasErrors()){
            //  장바구니에 담을 상품 정보를 받는 cartItemDto 객체에 데이터 바인딩 시 에러가 있는 지 검사
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for(FieldError fieldError : fieldErrors)
                sb.append(fieldError.getDefaultMessage());

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName(); //  현재 로그인한 회원의 이메일 정보를 변수에 저장
        Long cartItemId;

        try {
            //  화면으로부터 넘어온 장바구니에 담을 상품 정보와 현재 로그인한 회원의 이메일 정보를 이용하여
            //  장바구니에 상품을 담는 로직을 호출
            cartItemId = cartService.addCart(cartItemDto, email);
        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        //  결과값으로 생성된 장바구니 상품 아이디와 요청이 성공하였다는 HTTP 응답 상태 코드를 반환
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    //  장바구니 페이지로 이동
    @ApiOperation(value = "장바구니 페이지로 이동")
    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model){
        //  현재 로그인한 사용자의 이메일 정보를 이용하여 장바구니에 담겨있는 상품 정보 조회
        List<CartDetailDto> cartDetailDtoList = cartService.getCartList(principal.getName());
        
        //  조회한 장바구니 상품 정보를 뷰로 전달
        model.addAttribute("cartItems", cartDetailDtoList);

        return "cart/cartList";
    }

    //  장바구니 상품의 수량을 업데이트하는 요청 처리
    //  상품의 수량만 업데이트 하기 때문에 Patch
    @ApiOperation(value = "카트 상품 update")
    @PatchMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                       int count, Principal principal){
        if(count <= 0)
            return new ResponseEntity<String>("최소 1개 이상 담아주세요.", HttpStatus.BAD_REQUEST);
        else if(!cartService.validateCartItem(cartItemId, principal.getName()))
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);

        cartService.updateCartItemCount(cartItemId, count);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    //  장바구니 상품 삭제 요청 처리
    @ApiOperation(value = "카트 상품 삭제")
    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                       Principal principal){
        if(!cartService.validateCartItem(cartItemId, principal.getName()))
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);

        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }
    
    //  장바구니 상품의 수량을 업데이트하는 요청 처리
    @ApiOperation(value = "카트 상품 주문")
    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto, 
                                                      Principal principal){
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();
        
        if(cartOrderDtoList == null || cartOrderDtoList.size() == 0)
            return new ResponseEntity<String>("주문할 상품을 선택해주세요.", HttpStatus.FORBIDDEN);

        for(CartOrderDto cartOrder : cartOrderDtoList){
            if(!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName()))
                return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}
