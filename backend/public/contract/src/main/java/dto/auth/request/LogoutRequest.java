package dto.auth.request;

public record LogoutRequest(

        String userId,
        String deviceId,
        String accessToken
) {
}
