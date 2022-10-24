package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing          //  JPA 의 Auditing 기능을 활성화 한다. 다른 책에서는 보통 main 메서드가 있는 클래스에 적용했다
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider(){      //  등록자와 수정자를 처리해 주는 AuditorAware 를 빈으로 등록
        return new AuditorAwareImpl();
    }
}
