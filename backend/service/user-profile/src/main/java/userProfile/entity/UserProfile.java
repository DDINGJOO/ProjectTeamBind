package userProfile.entity;


import eurm.City;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_profile")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserProfile {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 32)
    private String nickname;

    private String email; // 카프카 기반 업데이트 (컬럼명 소문자)
    private String profileImageUrl; //카프카 기반 업데이트

    private String phoneNumber;
    private Boolean smsAgree;

    @Column(length = 8)
    private String gender;

    @Column(length = 200)
    private String introduction;

    @Enumerated(EnumType.STRING)
    private City city;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(
            mappedBy = "userProfile",      // ← UserGerne.userProfile 과 매칭
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<UserInstrument> userInstruments = new HashSet<>();

    @Builder.Default
    @OneToMany(
            mappedBy = "userProfile",      // ← UserGerne.userProfile 과 매칭
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<UserGerne> userGerne = new HashSet<>();
    @Column
    private LocalDateTime deletedAt;

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}