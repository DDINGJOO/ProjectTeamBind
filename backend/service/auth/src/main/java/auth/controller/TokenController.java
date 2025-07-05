package auth.controller;


import auth.service.token.RefreshTokenService;
import dto.auth.request.RefreshTokenRequest;
import dto.auth.response.LoginResponse;
import exception.error_code.token.TokenErrorCode;
import exception.excrptions.TokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import resposne.BaseResponse;
import token.JwtTokenProvider;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/token/v1")
@RequiredArgsConstructor
@Slf4j
public class TokenController {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<LoginResponse>> refreshToken(@RequestBody RefreshTokenRequest req) {
        try {
            log.info("Refresh Token 요청: {}", req);

            // 토큰 값 검증
            if (req.accessToken() == null || req.accessToken().isEmpty()) {
                throw new TokenException(TokenErrorCode.INVALID_TOKEN);
            }
            if (req.refreshToken() == null || req.refreshToken().isEmpty()) {
                throw new TokenException(TokenErrorCode.INVALID_TOKEN);
            }

            // 1. RefreshToken 유효성 확인
            if (!refreshTokenService.isValid(req.userId(), req.deviceId(), req.refreshToken())) {
                throw new TokenException(TokenErrorCode.INVALID_TOKEN);
            }

            String role = jwtTokenProvider.getClaim(req.accessToken(), "role");
            String deviceId = jwtTokenProvider.getClaim(req.refreshToken(), "deviceId");

            // 3. AccessToken 재발급
            String newAccessToken = jwtTokenProvider.issueAccessToken(req.userId(), Map.of("role", role));
            String newRefreshToken = jwtTokenProvider.issueRefreshToken(req.userId(), deviceId);

            return ResponseEntity.ok(BaseResponse.success(new LoginResponse(newAccessToken, newRefreshToken)));
        } catch (TokenException e) {
            log.warn("Refresh Token 요청 실패 userId={}, reason={}", req.userId(), e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
    }



    @DeleteMapping
    public ResponseEntity<BaseResponse<Void>> delete(@RequestParam String userId, @RequestParam String deviceId) {
        refreshTokenService.delete(userId, deviceId);
        return ResponseEntity.ok(BaseResponse.success());
    }

    @DeleteMapping("/all")
    public ResponseEntity<BaseResponse<Void>> deleteAll(@RequestParam String userId) {
        refreshTokenService.deleteAllByUser(userId);
        return ResponseEntity.ok(BaseResponse.success());
    }
}
