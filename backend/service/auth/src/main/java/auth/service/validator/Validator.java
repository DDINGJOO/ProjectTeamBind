package auth.service.validator;


public interface Validator<T> {
    boolean validate(T target);
}