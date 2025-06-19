package auth.service;

import auth.config.ProviderList;
import auth.dto.request.SignUpRequest;
import auth.entity.User;
import auth.repository.UserRepository;
import auth.service.validator.Validator;
import auth.service.validator.ValidatorFactory;
import auth.service.validator.ValidatorType;
import exception.error_code.auth.AuthErrorCode;
import exception.excrptions.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import primaryIdProvider.Snowflake;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ValidatorFactory validatorFactory;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Snowflake snowflake;

    @Mock
    private Validator<String> emailValidator;
    @Mock
    private Validator<String> passwordValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(validatorFactory.getValidator(ValidatorType.EMAIL)).thenReturn(emailValidator);
        when(validatorFactory.getValidator(ValidatorType.PASSWORD)).thenReturn(passwordValidator);
    }

    @Test
    void 회원가입_정상처리() {
        SignUpRequest req = new SignUpRequest("test@example.com", "Aa1!aaaaaa", "Aa1!aaaaaa");


        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(emailValidator.validate(req.getEmail())).thenReturn(true);
        when(passwordValidator.validate(req.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(req.getPassword())).thenReturn("encoded");
        when(snowflake.nextId()).thenReturn(1L);

        authService.registerUser(req);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertEquals("test@example.com", saved.getEmail());
        assertEquals("encoded", saved.getPassword());
        assertEquals(ProviderList.SYSTEM, saved.getProvider());
    }

    @Test
    void 중복된_이메일이면_예외() {
        SignUpRequest request = new SignUpRequest("dup@example.com", "Aa1!Password");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        AuthException ex = assertThrows(AuthException.class, () -> authService.registerUser(request));
        assertEquals(AuthErrorCode.DUPLICATE_EMAIL, ex.getErrorCode());
    }

    @Test
    void 이메일_형식_오류() {
        SignUpRequest request = new SignUpRequest("bademail", "Aa1!Password");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(emailValidator.validate(request.getEmail())).thenReturn(false);

        AuthException ex = assertThrows(AuthException.class, () -> authService.registerUser(request));
        assertEquals(AuthErrorCode.INVALID_EMAIL, ex.getErrorCode());
    }

    @Test
    void 비밀번호_형식_오류() {
        SignUpRequest request = new SignUpRequest("test@example.com", "weakpass");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(emailValidator.validate(request.getEmail())).thenReturn(true);
        when(passwordValidator.validate(request.getPassword())).thenReturn(false);

        AuthException ex = assertThrows(AuthException.class, () -> authService.registerUser(request));
        assertEquals(AuthErrorCode.INVALID_PASSWORD, ex.getErrorCode());
    }




    @Test
    void registerUser_shouldThrow_whenPasswordMismatch() {
        // given
        SignUpRequest request = new SignUpRequest("test@example.com", "Aa1!aaaaaa", "DifferentPass");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(validatorFactory.getValidator(ValidatorType.EMAIL)).thenReturn(email -> true);
        when(validatorFactory.getValidator(ValidatorType.PASSWORD)).thenReturn(pw -> true);

        // when
        Throwable thrown = catchThrowable(() -> authService.registerUser(request));

        // then
        assertThat(thrown)
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(AuthErrorCode. PASSWORD_CONFIRM_MISMATCH.getMessage());
    }

}