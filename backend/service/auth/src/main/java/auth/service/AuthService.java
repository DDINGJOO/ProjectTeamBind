package auth.service;


import auth.config.ProviderList;
import auth.config.UserRoleType;
import auth.dto.request.LoginRequest;
import auth.dto.request.SignUpRequest;
import auth.entity.User;
import auth.repository.UserRepository;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ValidatorFactory validatorFactory;
    private final UserRoleGrantService userRoleGrantService;
    private final Snowflake  snowflake;
    private final PasswordEncoder passwordEncoder;

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
                .isEmailVerified(false)
                .isDeleted(false)
                .build();
        userRepository.save(user);

        userRoleGrantService.grantDefaultRole(user);
    }


    @Transactional
    public void deleteUser(Long id)
    {
        User user =  userRepository.findById(id)
                .orElseThrow(()->new AuthException(AuthErrorCode.USER_NOT_FOUND));
        user.setIsDeleted(true);
        userRepository.save(user);
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

    @Transactional
     public void grantRole(Long grantedId, Long granterId, UserRoleType role) {
        User granted =  userRepository.findById(grantedId).orElseThrow(()->new AuthException(AuthErrorCode.USER_NOT_FOUND));
        User granter = userRepository.findById(granterId).orElseThrow(()->new AuthException(AuthErrorCode.USER_NOT_FOUND));
        userRoleGrantService.grantRole(granted,  granter, role );

    }
    private String passwordEncode(String password)
    {
        return passwordEncoder.encode(password);
    }

    public void login (LoginRequest req)
    {
        var user = userRepository.findByEmail(req.getEmail()).orElseThrow(
                ()-> new AuthException(AuthErrorCode.USER_NOT_FOUND));


        if(!user.getPassword() .equals(passwordEncode(req.getPassword())))
        {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD);
        }
    }

}