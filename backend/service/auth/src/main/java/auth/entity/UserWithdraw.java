package auth.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_withdraw", indexes = {
        @Index(name = "idx_withdraw_date", columnList = "withdrawDate")
})

@Getter
@Setter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor

public class UserWithdraw {

    @Id
    @Column(name ="user_id" , nullable = false, unique = true)
    Long userId;

    @Column(name ="reason")
    String reason;

    @Column(name = "withdraw_date")
    LocalDateTime withdrawDate;
}
