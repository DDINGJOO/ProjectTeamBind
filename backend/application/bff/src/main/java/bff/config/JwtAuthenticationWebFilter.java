package bff.config;

import exception.excrptions.TokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import token.JwtTokenProvider;
import tokenvalidation.TokenValidator;

@Component
@Slf4j
public class JwtAuthenticationWebFilter implements WebFilter {

    private final TokenValidator validator;
    private final JwtTokenProvider provider;

    public JwtAuthenticationWebFilter(TokenValidator validator,
                                      JwtTokenProvider provider) {
        this.validator = validator;
        this.provider  = provider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String header = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);
        log.info("header: {}", header);
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {

                // 1) 검증 (시그니처, 만료, 블랙리스트)
                validator.validate(token);
                // 2) Authentication 생성
                Authentication auth = provider.getAuthentication(token);
                // 3) SecurityContext에 담아서 다음으로
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
            } catch (TokenException ex) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }
        // 토큰 없으면 그냥 다음으로 (permitAll이 걸린 경로일 수 있음)
        return chain.filter(exchange);
    }
}