package auth.controller;


import auth.service.RefreshTokenService;
import dto.auth.request.RefreshTokenRequest;
import dto.auth.response.LoginResponse;
import exception.error_code.token.TokenErrorCode;
import exception.excrptions.TokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import resposne.BaseResponse;
import token.JwtTokenProvider;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/token/v1")
@RequiredArgsConstructor
public class TokenController {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<LoginResponse>> refreshToken(@RequestBody RefreshTokenRequest req) {
        // 1. RefreshToken 유효성 확인
        if (!refreshTokenService.isValid(req.userId(), req.deviceId(), req.refreshToken())) {
            throw new TokenException(TokenErrorCode.INVALID_TOKEN);
        }

        String role = jwtTokenProvider.getClaim(req.refreshToken(), "role");

        // 3. AccessToken 재발급
        String newAccessToken = jwtTokenProvider.issueAccessToken(req.userId(), Map.of("role", role));

        return ResponseEntity.ok(BaseResponse.success(new LoginResponse(newAccessToken, req.refreshToken())));
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
