package auth.entity;

import eurm.UserRoleType;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserRoleId implements Serializable {

    private Long user;
    private UserRoleType role;
}
