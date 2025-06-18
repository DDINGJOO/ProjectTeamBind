package auth.service.validator;


import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;


@Component
public class ValidatorFactory {

    private final Map<ValidatorType, Validator<String>> validatorMap = new EnumMap<>(ValidatorType.class);
    private final List<Validator<String>> validators;

    public ValidatorFactory(List<Validator<String>> validators) {
        this.validators = validators;
    }

    @PostConstruct
    public void initialize() {
        for (Validator<String> validator : validators) {
            if (validator instanceof EmailValidator emailValidator) {
                validatorMap.put(emailValidator.getType(), emailValidator);
            } else if (validator instanceof PasswordValidator passwordValidator) {
                validatorMap.put(passwordValidator.getType(), passwordValidator);
            }
        }
    }

    public Validator<String> getValidator(ValidatorType type) {
        return validatorMap.get(type);
    }
}