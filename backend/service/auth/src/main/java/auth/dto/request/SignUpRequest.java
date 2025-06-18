package auth.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SignUpRequest {

    String email;
    String password;
    String passwordConfirm;
}
