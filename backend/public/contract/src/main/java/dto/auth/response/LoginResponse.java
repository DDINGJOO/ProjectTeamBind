package dto.auth.response;


import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    String access_token;
    String refresh_token;
    String deviceId;
}
