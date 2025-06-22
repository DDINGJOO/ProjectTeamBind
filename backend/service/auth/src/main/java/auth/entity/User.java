package auth.entity;


import auth.config.ProviderList;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user-auth")
@Getter
@Setter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "user_id",  nullable = false, unique = true)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", unique = true)
    private String password;

    @Column(name= "provider")
    @Enumerated(EnumType.STRING)
    private ProviderList provider;



    private Boolean isDeleted;
    private Boolean isActive;

    @Column(name = "resisted-at",  nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "email-verified")
    private boolean isEmailVerified;


}
