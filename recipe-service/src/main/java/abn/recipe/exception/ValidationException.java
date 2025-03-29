package abn.recipe.exception;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ValidationException  extends RuntimeException {

    private final Map<String, String> errors;

    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = Objects.requireNonNull(errors);
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public String getErrorString() {
        return errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
    }
}