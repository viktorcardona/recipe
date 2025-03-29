package abn.recipe.exception;

import java.io.Serializable;

public class ObjectNotFoundException extends RuntimeException {

    private static final String MESSAGE = "No record found with the given identifier exists: [%s#%s]";

    private final String entityName;
    private final Serializable identifier;

    public ObjectNotFoundException(Serializable identifier, String entityName) {
        this.identifier = identifier;
        this.entityName = entityName;
    }

    @Override
    public String getMessage() {
        return MESSAGE.formatted(entityName, identifier);
    }
}
