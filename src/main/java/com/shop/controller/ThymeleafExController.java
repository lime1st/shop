package com.shop.controller;

import com.shop.dto.ItemDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/thymeleaf")
public class ThymeleafExController {

    @GetMapping(value = "/ex01")
    public String thymeleafExample01(Model model){
        //  Thymeleaf view 모델 속성에 "data" 이름으로 주어진 문자열 추가
        //  Thymeleaf view 모델의 "속성(Attribute)"은 렌더링 페이지에서는 변수(Variable)에 해당함
        model.addAttribute("data", "타임리프 예제 입니다.");

        //  resources/templates/thymeleafEx 디렉토리에 있는 thymeleafEx01.html 파일 렌더링
        return "thymeleafEx/thymeleafEx01";
    }

    @GetMapping(value = "/ex02")
    public String thymeleafExample02(Model model){
        
        //  테스트용 객체 생성
        ItemDto itemDto = new ItemDto();
        itemDto.setItemNm("상품 상세 설명");
        itemDto.setItemNm("테스트 상품1");
        itemDto.setPrice(10000);
        itemDto.setRegTime(LocalDateTime.now());

        model.addAttribute("itemDto", itemDto);
        return "thymeleafEx/thymeleafEx02";
    }

    @GetMapping(value = "/ex03")
    public String thymeleafExample03(Model model){

        model.addAttribute("itemDtoList", createSampleData());
        return "thymeleafEx/thymeleafEx03";
    }

    @GetMapping(value = "/ex04")
    public String thymeleafExample04(Model model){

        model.addAttribute("itemDtoList", createSampleData());
        return "thymeleafEx/thymeleafEx04";
    }

    @GetMapping(value = "/ex05")
    public String thymeleafExample05(){
        return "thymeleafEx/thymeleafEx05";
    }

    @GetMapping(value = "/ex06")
    public String thymeleafExample06(String param1, String param2, Model model){
        model.addAttribute("param1", param1);
        model.addAttribute("param2", param2);
        return "thymeleafEx/thymeleafEx06";
    }

    @GetMapping(value = "/ex07")
    public String thymeleafExample07(){
        return "thymeleafEx/thymeleafEx07";
    }

    public List<ItemDto> createSampleData(){
        List<ItemDto> itemDtoList = new ArrayList<>();

        for(int i = 1; i <= 10; i++){

            ItemDto itemDto = new ItemDto();
            itemDto.setItemDetail("상품 상세 설명" + i);
            itemDto.setItemNm("테스트 상품" + i);
            itemDto.setPrice(1000*i);
            itemDto.setRegTime(LocalDateTime.now());

            itemDtoList.add(itemDto);
        }
        return itemDtoList;
    }
}
