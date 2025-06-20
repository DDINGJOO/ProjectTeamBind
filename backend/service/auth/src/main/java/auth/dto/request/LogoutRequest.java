package auth.dto.request;

public record LogoutRequest(

        String userId,
        String deviceId,
        String accessToken
) {
}
