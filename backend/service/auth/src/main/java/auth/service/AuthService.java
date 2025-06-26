package auth.service;


import auth.config.ProviderList;
import auth.config.UserRoleType;
import auth.dto.request.LoginRequest;
import auth.dto.request.SignUpRequest;
import auth.dto.response.LoginResponse;
import auth.entity.User;
import auth.entity.UserRole;
import auth.repository.UserRepository;
import auth.repository.UserRoleRepository;
import auth.service.eventPublish.EventPublish;
import auth.service.validator.Validator;
import auth.service.validator.ValidatorFactory;
import auth.service.validator.ValidatorType;
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
    private final RefreshTokenService  refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final Snowflake  snowflake;

    //TODO : refact plz
    private final EventPublish  eventPublish;

    @Transactional
    public void registerUser(SignUpRequest req) {
        // 이메일 중복 체크
        validateUser(req);

        // 사용자 저장
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


        //todo : impl make comfirmurl
        eventPublish.emailConfirmedEvent(user.getId(),user.getEmail(), "http://localhost:9001/auth/confirm");
        userRoleGrantService.grantDefaultRole(user);
    }




    private void validateUser(SignUpRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new AuthException(AuthErrorCode.DUPLICATE_EMAIL);
        }

        // 이메일 유효성 검증
        Validator<String> emailValidator = validatorFactory.getValidator(ValidatorType.EMAIL);
        if (!emailValidator.validate(req.getEmail())) {
            throw new AuthException(AuthErrorCode.INVALID_EMAIL);
        }

        // 비밀번호 유효성 검증
        Validator<String> passwordValidator = validatorFactory.getValidator(ValidatorType.PASSWORD);
        if (!passwordValidator.validate(req.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD);
        }
        //비밀번호 확인 오휴
        if(!req.getPassword().equals(req.getPasswordConfirm())) {
            throw new AuthException(AuthErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }
    }




    public LoginResponse login(LoginRequest req) {
        // 1. 사용자 조회
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        checkUserHealth(req, user);


        // 3. 권한 조회
        UserRoleType role = userRoleRepository.findByUser(user)
                .map(UserRole::getRole)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_ROLE_NOT_FOUND));



        // 4. 토큰 발급
        String accessToken = jwtTokenProvider.issueAccessToken(String.valueOf(user.getId()), Map.of("role", role.name()));
        String refreshToken = jwtTokenProvider.issueRefreshToken(String.valueOf(user.getId()), req.getDeviceId());

        // 5. Refresh 토큰 저장 (Redis)
        refreshTokenService.save(String.valueOf(user.getId()), req.getDeviceId(), refreshToken, jwtTokenProvider.getRefreshTokenTTL());


        // 6. 응답 반환
        return new LoginResponse(accessToken, refreshToken);
    }

    private void checkUserHealth(LoginRequest req , User user) {
        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD);
        }
        if(!user.isEmailVerified())
        {
            throw new AuthException(AuthErrorCode.EMAIL_NOT_CONFIRMD);
        }
        if(!user.getIsActive())
        {
            throw new AuthException(AuthErrorCode.INVALID_USER);
        }
        if(user.getIsDeleted())
        {
            throw new AuthException(AuthErrorCode.DELETED_USER);
        }

    }


    @Transactional
    public void changePassword(Long userId, String newPassword, String newPasswordConfirm) {
        if(!passwordEncoder.matches(newPassword, newPasswordConfirm)) {
            throw new AuthException(AuthErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_ROLE_NOT_FOUND)
                );
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }




    @Transactional
    public void confirmedEmail(Long userId){
        var user = userRepository.findById(userId).orElseThrow(
                ()-> new AuthException(AuthErrorCode.USER_NOT_FOUND)
        );

        if(user.isEmailVerified())
        {
            throw new AuthException(AuthErrorCode.IS_ALREADY_CONFIRMED);
        }

        user.setEmailVerified(true);
        user.setIsActive(true);
        userRepository.save(user);
        eventPublish.createUserEvent(user.getId(), user.getEmail());
    }

    private String passwordEncode(String password)
    {
        return passwordEncoder.encode(password);
    }
}