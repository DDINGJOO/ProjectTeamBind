package bff.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf().disable()
                .authorizeExchange(exchanges -> exchanges
                        // swagger-ui, api-docs, webjars 등은 인증 없이 허용
                        .pathMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()
                                .anyExchange()
                                .permitAll()
                        // 나머지 API는 인증 필요
                )
                // JWT 리소스 서버로 구성했다면 아래처럼 추가
        // 만약 기본 formLogin이나 httpBasic을 쓴다면 필요에 따라 비활성화/활성화
        //.httpBasic().and().formLogin().disable()
        ;
        return http.build();
    }
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            JwtAuthenticationWebFilter jwtFilter
    ) {
        return http
                .csrf().disable()
                .authorizeExchange(ex -> ex
                        .pathMatchers("/api/auth/v1/signup", "/api/auth/v1/login", "api/auth/confirmEmail")
                        .permitAll()
                        .anyExchange()
                        .permitAll()
                )
                // Authentication 단계에 삽입
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}