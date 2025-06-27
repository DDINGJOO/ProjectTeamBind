package userProfile.entity;

import eurm.Genre;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_genre",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_profile_id", "genre"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserGerne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;
}