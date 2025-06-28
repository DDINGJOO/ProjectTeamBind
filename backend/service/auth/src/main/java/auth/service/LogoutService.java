package auth.service;


import auth.service.token.RefreshTokenService;
import auth.service.token.TokenBlacklistService;
import exception.error_code.token.TokenErrorCode;
import exception.excrptions.TokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService {
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService blacklistService;

    public void logout(String accessToken, String userId, String deviceId) {
        // 1) 해당 세션 리프레시 토큰 삭제
        refreshTokenService.delete(userId, deviceId);

        // 2) 액세스 토큰 JTI 블랙리스트에 올리기
        try {
            blacklistService.revokeToken(accessToken);
        } catch (TokenException e) {
            log.warn("로그아웃 중 토큰 처리 실패 userId={}, reason={}", userId, e.getMessage());
            throw new TokenException(TokenErrorCode.INVALID_TOKEN);
        }
    }

    public void logoutAll(String accessToken, String userId) {
        // 1) 모든 디바이스의 리프레시 토큰 삭제
        refreshTokenService.deleteAllByUser(userId);

        // 2) 이 토큰만 블랙리스트 처리하면,
        //    다만 이미 발행된 다른 AccessToken은 여전히 유효하므로 아래 대안 고려
        blacklistService.revokeToken(accessToken);
    }
}