package userProfile.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(
        name = "user_interest",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_profile_id", "interest"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserInstrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private eurm.Instrument instrument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

}
