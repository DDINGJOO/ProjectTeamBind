package bff.controller;


import bff.client.auth.AuthClient;
import dto.auth.request.LoginRequest;
import dto.auth.request.SignUpRequest;
import exception.error_code.auth.AuthErrorCode;
import exception.excrptions.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import resposne.BaseResponse;


@RestController
@RequestMapping("/api/auth/v1")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthClient authClient;

    @PostMapping("/signup")
    public Mono<ResponseEntity<BaseResponse<?>>> signUp(@RequestBody SignUpRequest req) {
        return authClient.signUp(req);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<BaseResponse<?>>> login(@RequestBody LoginRequest req) {
        return authClient.login(req);
    }


    @PostMapping("/withdraw")
    public Mono<ResponseEntity<BaseResponse<?>>> withdraw(
            Authentication authentication,
            @RequestParam Long userId,
            @RequestParam String reason
    ) {
        String userIdFromToken = authentication.getName();       // sub
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");
        // ROLE_User 같은 형태 → 필요하면 앞 5글자("ROLE_") 제거
        String simpleRole = role.replace("ROLE_", "");

        // userId 체크
        if (!userIdFromToken.equals(userId.toString())) {
            throw new AuthException(AuthErrorCode.USER_NOT_FOUND);
        }

        log.info("요청자 ID={}, 권한={}", userIdFromToken, simpleRole);
        return authClient.withdraw(userId, reason);
    }
}