package com.shop.repository.service;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor        //  final 이나 @NonNull 이 붙은 필드에 생성자를 생성해준다. 빈에 생성자가 1개이고 생성자의 파라미터 타입이 빈으로 등록이 가능하다면 @Autowired 어노테이션 없이 의존성 주입이 가능하다.
public class MemberService implements UserDetailsService {
    //  UserDetailsService 인터페이스는 데이터베이스에서 회원 정보를 가져오는 역할을 한다.
    //  loadUserByUsername() 메소드가 존재하며, 회원 정보를 조회하여 사용자의 정ㅂ와 권한을 갖는 UserDetails 인터페이스를 반환한다.

    private final MemberRepository memberRepository;

    public Member saveMember(Member member){

        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member){

        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Member member = memberRepository.findByEmail(email);

        if(member == null)
            throw new UsernameNotFoundException(email);


        //  스프링 시큐리티에서 회원의 정보를 담기 위해 사용하는 인터페이스는 UserDetails 다.
        //  이 인터페이스를 직접 구현하거나 스프링 시큐리티에서 제공하는 User 클래스를 사용
        //  User 클래스는 UserDetails 인터페이스르르 구현하고 있는 클래스

        //  email 은 유일하므로 username 으로 사용한다.
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }
}
