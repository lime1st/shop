package com.shop.config;

import com.shop.repository.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    MemberService memberService;

    @Override
    protected void configure(HttpSecurity http) throws Exception{

        http.formLogin()
                //  오타!! 특히 주의!!!
                .loginPage("/members/login")                //  loginPage URL 설정
                    .defaultSuccessUrl("/")                     //  로그인 성공 시 이동할 URL
                    .usernameParameter("email")                 //  로그인에 사용할 파라미터
                    .failureUrl("/members/login/error")  //  로그인 실패 시 이동할 URL
                .and()
                .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")) //  로그아웃 URL
                    .logoutSuccessUrl("/");                     //  로그아웃 성공 시 이동할 URL

        http.authorizeRequests()                            //  시큐리티 처리에 HttpServletRequest 를 이용한다.
                //  permitAll() 을 통해 모든 사용자가 인증없이 해당 경로에 접근할 수 있도록 설정
                //  메인페이지"/", 회원관련 URL"/members/**", 상품상세"/item/**", 상품 이미지"/images/**"
                .mvcMatchers("/", "/members/**", "/item/**", "/images/**").permitAll()
                //  ADMIN role일 경우에만 "/admin/**"에 접근
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                //  나머지 경로 들은 모두 인증 요구
                .anyRequest().authenticated();

        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        //  Spring Security 에서 인증은 AuthenticationManager 를 통해 이루어진다.
        //  AuthenticationManagerBuilder 가 AuthenticationManager 를 생성한다.
        //  userDetailService 를 구현하고 있는 객체로 memberService 를 지정해주며,
        //  비밀번호 암호화를 위해 passwordEncoder 를 지정해 준다.

        auth.userDetailsService(memberService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws  Exception{
        //  static 디렉터리의 하위 파일들은 인증을 무시하도록 설정
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
    }
}
