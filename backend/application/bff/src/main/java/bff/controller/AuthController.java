package bff.controller;


import bff.client.auth.AuthClient;
import dto.auth.request.LoginRequest;
import dto.auth.request.SignUpRequest;
import exception.error_code.bff.BffErrorCode;
import exception.excrptions.BffException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(summary = "회원 가입", description = "이메일·비밀번호로 회원 가입을 진행합니다.")
    @PostMapping("/signup")
    public Mono<ResponseEntity<BaseResponse<?>>> signUp(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "SignUpRequest DTO", required = true
            )
            @RequestBody SignUpRequest req
    ) {
        log.info("CALLED /api/auth/v1/signup /n"+req.toString());
        return authClient.signUp(req);
    }

    @Operation(summary = "로그인", description = "이메일·비밀번호로 로그인하고 토큰을 발급받습니다.")
    @PostMapping("/login")
    public Mono<ResponseEntity<BaseResponse<?>>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "LoginRequest DTO", required = true
            )
            @RequestBody LoginRequest req
    ) {
        log.info("CALLED /api/auth/v1/login /n"+req.toString());
        return authClient.login(req);
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "인증된 사용자만 자신의 계정을 탈퇴합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )

    @PostMapping("/withdraw")
    public Mono<ResponseEntity<BaseResponse<?>>> withdraw(
            Authentication authentication,
            @Parameter(description = "탈퇴할 사용자 ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "탈퇴 사유", required = true)
            @RequestParam String reason
    ) {
        String userIdFromToken = authentication.getName();       // sub
        String simpleRole = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("")
                .replace("ROLE_", "");

        if (!userIdFromToken.equals(userId.toString())) {
            throw new BffException(BffErrorCode.NOT_MATCHED_TOKEN);
        }

        log.info("CALLED /api/auth/v1/withdraw /n" +
                "요청자 ID={}, 권한={}", userIdFromToken, simpleRole);
        return authClient.withdraw(userId, reason);
    }

    @Operation(
            summary = "이메일 인증",
            description = " 이메일 인증을 합니다."
    )
    @PostMapping("/confirmEmail")
    public Mono<ResponseEntity<BaseResponse<?>>> confirmEmail(
            @RequestParam Long userId,
            @RequestParam String code
    )
    {
        log.info("CALLED /api/auth/v1/confirmEmail /n"+
                "요청자 ID {}, 코드  {} ",userId, code);
        return authClient.confirmEmail(userId, code);
    }
}

