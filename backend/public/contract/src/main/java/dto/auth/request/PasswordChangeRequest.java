package dto.auth.request;

public record PasswordChangeRequest(
        Long userId,
        String newPassword,
        String newPasswordConfirm
) {
}
