package auth.service;


import auth.entity.User;
import auth.entity.UserRole;
import auth.repository.UserRepository;
import auth.repository.UserRoleRepository;
import eurm.UserRoleType;
import exception.error_code.auth.AuthErrorCode;
import exception.excrptions.AuthException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserRoleGrantService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;


    @Transactional
    public void grantRole(Long grantedId, Long granterId, UserRoleType role) {
        User granted =  userRepository.findById(grantedId).orElseThrow(()->new AuthException(AuthErrorCode.USER_NOT_FOUND));
        User granter = userRepository.findById(granterId).orElseThrow(()->new AuthException(AuthErrorCode.USER_NOT_FOUND));
        grantRole(granted,  granter, role );

    }


    @Transactional
    public void grantDefaultRole(User user) {
        UserRole role = UserRole.builder()
                .role(UserRoleType.User)
                .user(user)
                .grantedAt(LocalDateTime.now())
                .grantedBy(0L)
                .build();
        userRoleRepository.save(role);
    }


    @Transactional
    public void grantRole(User user, User granter, UserRoleType role) {
        ensureGranterHasPrivilege(granter);

        UserRole targetRole = userRoleRepository.findByUser(user)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_ROLE_NOT_FOUND));

        targetRole.setGrantedAt(LocalDateTime.now());
        targetRole.setRole(role);
        targetRole.setGrantedBy(granter.getId());
        userRoleRepository.save(targetRole);
    }



    private void ensureGranterHasPrivilege(User granter) {
        UserRole granterRole = userRoleRepository.findByUser(granter)
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHORIZED_ROLE_ASSIGNMENT));

        if (granterRole.getRole().equals(UserRoleType.User) ||
                granterRole.getRole().equals(UserRoleType.Guest)) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED_ROLE_ASSIGNMENT);
        }
    }


}