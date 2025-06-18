package auth.service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatorFactoryTest {

    private ValidatorFactory factory;

    @BeforeEach
    void setUp() {
        // 수동 DI
        factory = new ValidatorFactory(
                List.of(new EmailValidator(), new PasswordValidator())
        );
        factory.initialize();
    }

    @Test
    void EMAIL_검증기_조회_성공() {
        Validator<String> validator = factory.getValidator(ValidatorType.EMAIL);
        assertThat(validator).isNotNull();
        assertThat(validator.validate("test@example.com")).isTrue();
    }

    @Test
    void PASSWORD_검증기_조회_성공() {
        Validator<String> validator = factory.getValidator(ValidatorType.PASSWORD);
        assertThat(validator).isNotNull();
        assertThat(validator.validate("Str0ng!Passw0rd")).isTrue();
    }

    @Test
    void 존재하지_않는_타입_조회_시_NULL() {
        // ValidatorType이 더 추가된다면 대비
        assertThat(factory.getValidator(null)).isNull();
    }
}
