package auth.service.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;



@Component
public class EmailValidator implements Validator<String> {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"

    );

    @Override
    public boolean validate(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public ValidatorType getType() {
        return ValidatorType.EMAIL;
    }


}