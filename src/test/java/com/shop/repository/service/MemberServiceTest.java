package com.shop.repository.service;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional      //  테스트 클래스에서 Transactional 을 사용하면 테스트 실행 후 롤백 처리가 된다
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@emaili.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword("1234");

        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void saveMemberTest(){

        Member member = createMember();
        Member savedMember = memberService.saveMember(member);

        //  Junit 의 Assertions 클래스의 assertEquals 메소드를 이용하여 저장하려고 요청했던 값과
        //  실제 저장된 데이터를 비교한다. 첫 번째 파라미터에는 기대 값, 두 번째 파라미터에는 실제로 저장된 값을 넣어준다.
        assertEquals(member.getEmail(), savedMember.getEmail());
        assertEquals(member.getName(), savedMember.getName());
        assertEquals(member.getAddress(), savedMember.getAddress());
        assertEquals(member.getPassword(), savedMember.getPassword());
        assertEquals(member.getRole(), savedMember.getRole());
    }

    @Test
    @DisplayName("중복 회원 가입 테스트")
    public void saveDuplicateMemberTest(){
        Member member1 = createMember();
        Member member2 = createMember();
        memberService.saveMember(member1);

        Throwable e = assertThrows(IllegalStateException.class, () ->{
            memberService.saveMember(member2);
        });

        assertEquals("이미 가입된 회원입니다.", e.getMessage());
    }

}