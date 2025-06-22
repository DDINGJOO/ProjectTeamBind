package userProfile.entity;

import jakarta.persistence.*;
import lombok.*;
import userProfile.config.Genre;

@Entity
@Table(
        name = "user_genre",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_profile_id", "genre"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;
}