package bff.controller;


import bff.client.auth.AuthClient;
import dto.auth.request.RefreshTokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import resposne.BaseResponse;

@RestController
@RequestMapping("/api/token/v1")
@RequiredArgsConstructor
@Slf4j
public class TokenController {

    private final AuthClient authClient;


    @PostMapping("/refresh")
    public Mono<ResponseEntity<BaseResponse<?>>> refreshToken(
            Authentication authentication,
            @RequestBody RefreshTokenRequest refreshToken

    ) {
        String userId = authentication.getName();
        if(refreshToken.userId() != null && !refreshToken.userId().equals(userId)) {
            return Mono.just(ResponseEntity.ok(BaseResponse.error("refreshToken.userId is not matched")));
        }
        log.info("CALLED /api/token/v1/refresh /n"+refreshToken.toString());
        return authClient.refresh(refreshToken);
    }

}
