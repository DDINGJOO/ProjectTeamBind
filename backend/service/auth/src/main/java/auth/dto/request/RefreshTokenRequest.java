package auth.dto.request;

public record RefreshTokenRequest(
        String userId,
        String deviceId,
        String refreshToken
) {
}
