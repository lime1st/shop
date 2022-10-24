package com.shop.controller;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.repository.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Log4j2
@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping(value = "/new")
    public String memberForm(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping(value = "/new")
    public String memberForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model){
        //  검증하려는 객체의 앞에 @Valid 를 선언하고, 파라미터로 bindingResult 객체를 추가한다.
        //  검사 후 결과는 bindingResult 에 담아준다.
        //  bindingResult.hasErrors()를 호출하여 에러가 있다면 회원 가입페이지로 이동

        if(bindingResult.hasErrors())
            return "member/memberForm";

        try{
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            Member savedMember = memberService.saveMember(member);
            log.info("===================savedMember" + savedMember.toString());
        }catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String loginMember(){
        return "/member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
        return "/member/memberLoginForm";
    }
}
