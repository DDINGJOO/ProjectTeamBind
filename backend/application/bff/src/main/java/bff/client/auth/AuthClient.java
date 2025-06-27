package bff.client.auth;


import dto.auth.request.SignUpRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import resposne.BaseResponse;


@Component
public class AuthClient {
    private final WebClient webClient;

    public AuthClient(@Qualifier("authWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ResponseEntity<BaseResponse<?>>> signUp(SignUpRequest req) {
        return webClient.post()
                .uri("/api/auth/v1/signup")
                .bodyValue(req)
                .exchangeToMono(response ->
                        response.bodyToMono(new ParameterizedTypeReference<BaseResponse<?>>() {})
                                .map(body -> {
                                    if (!body.isSuccess()) {
                                        return ResponseEntity
                                                .status(HttpStatus.BAD_REQUEST)
                                                .body(body);
                                    }
                                    return ResponseEntity.ok(body);
                                })
                );
    }
}

