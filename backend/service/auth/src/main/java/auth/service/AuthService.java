package auth.service;


import auth.entity.User;
import auth.entity.UserRole;
import auth.repository.UserRepository;
import auth.repository.UserRoleRepository;
import auth.service.code.CreateCodeService;
import auth.service.eventPublish.EventPublish;
import auth.service.token.RefreshTokenStore;
import auth.service.validator.Validator;
import auth.service.validator.ValidatorFactory;
import auth.service.validator.ValidatorType;
import dto.auth.request.LoginRequest;
import dto.auth.request.SignUpRequest;
import dto.auth.response.LoginResponse;
import eurm.ProviderList;
import eurm.UserRoleType;
import exception.error_code.auth.AuthErrorCode;
import exception.excrptions.AuthException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import primaryIdProvider.Snowflake;
import token.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ValidatorFactory validatorFactory;
    private final UserRoleGrantService userRoleGrantService;
    private final UserRoleRepository userRoleRepository;
    private final RefreshTokenStore refreshTokenStore;
    private final CreateCodeService createCode;

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final Snowflake snowflake;
    private final EventPublish eventPublish;

    @Transactional
    public void registerUser(SignUpRequest req) {
        validateUser(req);

        User user = User.builder()
                .id(snowflake.nextId())
                .email(req.getEmail())
                .password(passwordEncode(req.getPassword()))
                .provider(ProviderList.SYSTEM)
                .createdAt(LocalDateTime.now())
                .isActive(false)
                .isEmailVerified(false)
                .isDeleted(false)
                .build();
        userRepository.save(user);

        eventPublish.emailConfirmedEvent(
                user.getId(),
                user.getEmail(),
                createCode.createCode(user.getId())
        );
        userRoleGrantService.grantDefaultRole(user);
    }

    private void validateUser(SignUpRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new AuthException(AuthErrorCode.DUPLICATE_EMAIL);
        }

        Validator<String> emailValidator =
                validatorFactory.getValidator(ValidatorType.EMAIL);
        if (!emailValidator.validate(req.getEmail())) {
            throw new AuthException(AuthErrorCode.INVALID_EMAIL);
        }

        Validator<String> pwdValidator =
                validatorFactory.getValidator(ValidatorType.PASSWORD);
        if (!pwdValidator.validate(req.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD);
        }

        if (!req.getPassword().equals(req.getPasswordConfirm())) {
            throw new AuthException(AuthErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }
    }

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        checkUserHealth(req, user);

        UserRoleType role = userRoleRepository.findByUser(user)
                .map(UserRole::getRole)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_ROLE_NOT_FOUND));


        Long deviceId = (snowflake.nextId()%9999);
        // 1) 토큰 발급
        String accessToken  = jwtTokenProvider.issueAccessToken(
                String.valueOf(user.getId()),
                Map.of("role", role.name())
        );
        String refreshToken = jwtTokenProvider.issueRefreshToken(
                String.valueOf(user.getId()),
                deviceId.toString()
        );

        // 2) 리프레시 토큰 저장 (토큰 스토어에 위임)
        refreshTokenStore.save(
                String.valueOf(user.getId()),
                deviceId.toString(),
                refreshToken,
                jwtTokenProvider.getRefreshTokenTTL()
        );

        return new LoginResponse(accessToken, refreshToken,deviceId.toString());
    }

    private void checkUserHealth(LoginRequest req, User user) {
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD);
        }
        if (!user.isEmailVerified()) {
            throw new AuthException(AuthErrorCode.EMAIL_NOT_CONFIRMED);
        }
        if (!user.getIsActive()) {
            throw new AuthException(AuthErrorCode.INVALID_USER);
        }
        if (user.getIsDeleted()) {
            throw new AuthException(AuthErrorCode.DELETED_USER);
        }
    }

    @Transactional
    public void changePassword(Long userId, String newPassword, String newPasswordConfirm) {
        if (!newPassword.equals(newPasswordConfirm)) {
            throw new AuthException(AuthErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void confirmedEmail(Long userId ,String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        if (user.isEmailVerified()) {
            throw new AuthException(AuthErrorCode.IS_ALREADY_CONFIRMED);
        }

        if(!confirmCode(userId, code))
        {
            throw new AuthException(AuthErrorCode.EMAIL_NOT_CONFIRMED);
        }

        user.setEmailVerified(true);
        user.setIsActive(true);
        userRepository.save(user);

        eventPublish.createUserEvent(user.getId(), user.getEmail());
    }

    private boolean confirmCode(Long userId, String code)
    {
        String strUserId = String.valueOf(userId);

        if(strUserId.contains(code))
        {
            return true;
        }

        else
        {
            return false;
        }
    }

    private String passwordEncode(String password) {
        return passwordEncoder.encode(password);
    }


}