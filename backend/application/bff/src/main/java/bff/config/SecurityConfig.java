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
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            JwtAuthenticationWebFilter jwtFilter
    ) {
        return http
                .csrf().disable()
                .authorizeExchange(ex -> ex
                        .pathMatchers("/api/auth/v1/signup", "/api/auth/v1/login")
                        .permitAll()
                        .pathMatchers("/api/auth/v1/withdraw")
                        .authenticated()
                        .anyExchange()
                        .authenticated()
                )
                // Authentication 단계에 삽입
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}