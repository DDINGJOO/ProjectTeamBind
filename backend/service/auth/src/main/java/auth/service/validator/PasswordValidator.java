package auth.service.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;



@Component
public class PasswordValidator implements Validator<String> {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=(?:.*[A-Z]){1,})(?=(?:.*[a-z]){1,})(?=(?:.*\\d){1,})(?=(?:.*[\\W_]){1,}).{10,}$"

    );

    @Override
    public boolean validate(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public ValidatorType getType() {
        return ValidatorType.PASSWORD;
    }
}