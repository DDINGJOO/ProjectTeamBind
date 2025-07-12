package bff.client.auth;


import bff.client.ClientMethodFactory;
import bff.client.impl.ClientMethodFactoryImpl;
import dto.auth.request.LoginRequest;
import dto.auth.request.RefreshTokenRequest;
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
    private final String TOKEN_URI = "/api/auth/token/v1";
    private final ClientMethodFactory clientMethodFactory;
    public AuthClient(@Qualifier("authWebClient") WebClient webClient) {

        clientMethodFactory = new ClientMethodFactoryImpl(webClient);
    }

    public Mono<ResponseEntity<BaseResponse<?>>> signUp(SignUpRequest req) {
        return clientMethodFactory.post(req, BASE_URI + "/signup");
    }

    public Mono<ResponseEntity<BaseResponse<?>>> login(LoginRequest req) {
        return clientMethodFactory.post(req, BASE_URI + "/login");
    }
    public Mono<ResponseEntity<BaseResponse<?>>> withdraw(Long userid, String reason) {
        String url = BASE_URI + "/withdraw?userId=" + userid + "&reason=" + reason;
        return clientMethodFactory.post(url);
    }

    public Mono<ResponseEntity<BaseResponse<?>>> confirmEmail(Long userId, String code) {
        String url = BASE_URI + "/email?userId=" + userId + "&code=" + code;
        return clientMethodFactory.post(url);
    }


    public Mono<ResponseEntity<BaseResponse<?>>> refresh(RefreshTokenRequest req) {
        return clientMethodFactory.post(req, TOKEN_URI + "/refresh");

    }









}

