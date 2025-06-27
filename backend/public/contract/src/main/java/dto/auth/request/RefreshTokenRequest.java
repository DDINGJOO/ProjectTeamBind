package dto.auth.request;

public record RefreshTokenRequest(
        String userId,
        String deviceId,
        String refreshToken
) {
}
