package bff.client.auth;


import dto.auth.request.LoginRequest;
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
    private final String BASE_URI = "/api/auth/v1";
    private final WebClient webClient;
    public AuthClient(@Qualifier("authWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ResponseEntity<BaseResponse<?>>> signUp(SignUpRequest req) {
        return post(req, BASE_URI + "/signup");
    }

    public Mono<ResponseEntity<BaseResponse<?>>> login(LoginRequest req) {
        return post(req, BASE_URI + "/login");
    }
    public Mono<ResponseEntity<BaseResponse<?>>> withdraw(Long userid, String reason) {
        return post(BASE_URI + "/withdraw?"
                +"userId=" + userid
                +"&reason=" + reason
        );
    }

    public Mono<ResponseEntity<BaseResponse<?>>> confirmEmail(Long userId, String code) {
        return post(BASE_URI + "/confirmEmail?userId=" + userId + "&code=" + code);
    }



    private Mono<ResponseEntity<BaseResponse<?>>> post(Object req, String uri) {
        return webClient.post()
                .uri(uri)
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

    private Mono<ResponseEntity<BaseResponse<?>>> post(String uri) {
        return webClient.post()
                .uri(uri)
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

