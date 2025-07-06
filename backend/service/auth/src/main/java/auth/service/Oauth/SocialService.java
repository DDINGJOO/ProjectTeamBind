package auth.service.Oauth;


import auth.entity.User;
import auth.entity.UserRole;
import auth.repository.UserRepository;
import auth.repository.UserRoleRepository;
import auth.service.UserRoleGrantService;
import auth.service.token.RefreshTokenStore;
import dto.auth.response.LoginResponse;
import eurm.ProviderList;
import eurm.UserRoleType;
import exception.error_code.auth.AuthErrorCode;
import exception.excrptions.AuthException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import primaryIdProvider.Snowflake;
import token.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class SocialService {

    private final UserRepository userRepository;
    private final Snowflake snowflake;
    private final UserRoleGrantService userRoleGrantService;
    private final UserRoleRepository userRoleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStore refreshTokenStore;


    @Transactional
    public User createUser(ProviderList provider, String email) {
        if (userRepository.existsByEmail(email)) {
            throw new AuthException(AuthErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .id(snowflake.nextId())
                .email(email)
                .provider(provider)
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .isEmailVerified(true)
                .isDeleted(false)
                .build();
        userRepository.save(user);
        userRoleGrantService.grantDefaultRole(user);
        return user;
    }

    public LoginResponse login(ProviderList provider, User user) {

        if (!user.getProvider().equals(provider)) {
            throw new AuthException(AuthErrorCode.INVALID_PROVIDER);
        }
        if (!user.getIsActive()) {
            throw new AuthException(AuthErrorCode.INVALID_USER);
        }
        if (user.getIsDeleted()) {
            throw new AuthException(AuthErrorCode.DELETED_USER);
        }

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
}
