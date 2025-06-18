package bff.dto.request;


/**
 * Author : MyungJoo
 * Date : 2025-06-18
 */
public record SignupRequest(
        String email,
        String password,
        String passwordCheck
) {
}
