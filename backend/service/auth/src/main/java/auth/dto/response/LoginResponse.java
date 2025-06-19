package auth.dto.response;


import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    String access_token;
    String refresh_token;
}
