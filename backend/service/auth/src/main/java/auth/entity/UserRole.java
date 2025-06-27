package auth.entity;

import eurm.UserRoleType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_role")
@Getter
@Setter
@Builder
@IdClass(UserRoleId.class)
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {


    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @Enumerated(EnumType.STRING)
    private UserRoleType role;

    private LocalDateTime grantedAt;

    private Long grantedBy;// "부여자 ID"를 의미합니다. 이 필드는 사용자가 역할을 부여받은 사람의 ID 저장합니다.
}