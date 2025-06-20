package auth.service.validator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator();

    @Test
    void 강력한_비밀번호_통과() {
        assertThat(validator.validate("Str0ng!Passw0rd")).isTrue();
    }

    @Test
    void 약한_비밀번호_실패() {
        assertThat(validator.validate("weakpass")).isFalse();
        assertThat(validator.validate("1234567890")).isFalse();
        assertThat(validator.validate(null)).isFalse();
    }
}
