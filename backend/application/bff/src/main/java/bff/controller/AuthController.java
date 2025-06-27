package bff.controller;


import bff.client.auth.AuthClient;
import dto.auth.request.SignUpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import resposne.BaseResponse;


@RestController
@RequestMapping("/api/auth/v1")
public class AuthController {

    private final AuthClient authClient;

    public AuthController(AuthClient authClient) {
        this.authClient = authClient;
    }

    @PostMapping("/signup")
    public Mono<ResponseEntity<BaseResponse<?>>> signUp(@RequestBody SignUpRequest req) {
        // BFF → Auth 서비스 호출 후, 그대로 클라이언트에 응답
        return authClient.signUp(req);
    }
}
