package userProfile.entity;

import eurm.UpdatableProfileColumn;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "user_profile_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserProfileHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private UpdatableProfileColumn fieldChanged;

    @Column(length = 255)
    private String oldValue;

    @Column(length = 255)
    private String newValue;

    @Column(nullable = false)
    private String changedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt;

    public static UserProfileHistory of(Long userId,
                                        UpdatableProfileColumn field,
                                        String oldValue,
                                        String newValue,
                                        String changedBy) {
        return UserProfileHistory.builder()
                .userId(userId)
                .fieldChanged(field)
                .oldValue(oldValue)
                .newValue(newValue)
                .changedBy(changedBy)
                .build();
    }

    @PrePersist
    public void onCreate() {
        this.changedAt = LocalDateTime.now();
    }
}