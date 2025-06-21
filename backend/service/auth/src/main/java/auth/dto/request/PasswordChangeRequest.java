package auth.dto.request;

public record PasswordChangeRequest(
        Long userId,
        String newPassword,
        String newPasswordConfirm
) {
}
