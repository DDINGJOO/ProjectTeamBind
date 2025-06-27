package auth.service;

import auth.entity.User;
import auth.entity.UserRole;
import auth.repository.UserRoleRepository;
import eurm.UserRoleType;
import exception.error_code.auth.AuthErrorCode;
import exception.excrptions.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserRoleGrantServiceTest {

    private final User user = User.builder().id(1L).build();
    @InjectMocks
    private UserRoleGrantService userRoleGrantService;
    @Mock
    private UserRoleRepository userRoleRepository;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void grantDefaultRole_정상() {
        userRoleGrantService.grantDefaultRole(user);

        verify(userRoleRepository).save(argThat(role ->
                role.getUser().equals(user) &&
                        role.getRole() == UserRoleType.User &&
                        role.getGrantedBy() == 0L
        ));
    }

    @Test
    void grantRole_정상() {
        User granter = User.builder().id(2L).build();
        UserRole granterRole = UserRole.builder().user(granter).role(UserRoleType.Admin).build();
        UserRole targetRole = UserRole.builder().user(user).role(UserRoleType.User).build();

        when(userRoleRepository.findByUser(granter)).thenReturn(Optional.of(granterRole));
        when(userRoleRepository.findByUser(user)).thenReturn(Optional.of(targetRole));

        userRoleGrantService.grantRole(user, granter, UserRoleType.Admin);

        verify(userRoleRepository).save(targetRole);
    }

    @Test
    void grantRole_권한없는_사용자가_시도시_예외() {
        User granter = User.builder().id(2L).build();
        UserRole granterRole = UserRole.builder().user(granter).role(UserRoleType.User).build();

        when(userRoleRepository.findByUser(granter)).thenReturn(Optional.of(granterRole));

        assertThatThrownBy(() -> userRoleGrantService.grantRole(user, granter, UserRoleType.Admin))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(AuthErrorCode.UNAUTHORIZED_ROLE_ASSIGNMENT.getMessage());
    }
}
