package auth.service.validator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailValidatorTest {

    private final EmailValidator validator = new EmailValidator();

    @Test
    void 유효한_이메일_통과() {
        assertThat(validator.validate("test@example.com")).isTrue();
    }

    @Test
    void 유효하지_않은_이메일_실패() {
        assertThat(validator.validate("invalid-email")).isFalse();
        assertThat(validator.validate("user@com")).isFalse();
        assertThat(validator.validate(null)).isFalse();
    }
}
