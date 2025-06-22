package userProfile.entity;

import jakarta.persistence.*;
import userProfile.config.UpdatableProfileColumn;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile_history")
public class UserProfileHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private UpdatableProfileColumn fieldChanged; // NICKNAME, PROFILE_IMAGE, LOCATION 등

    private String oldValue;
    private String newValue;

    private LocalDateTime changedAt;

    private String changedBy; // 유저 ID 또는 SYSTEM
}
