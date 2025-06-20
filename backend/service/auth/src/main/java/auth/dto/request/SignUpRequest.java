package auth.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {

    String email;
    String password;
    String passwordConfirm;

    public SignUpRequest(String mail, String s) {
        this.email = mail;
        this.password = s;

    }

    public SignUpRequest(String mail, String s, String differentPass)
    {
        this.email = mail;
        this.password = s;
        this.passwordConfirm = differentPass;

    }
}
