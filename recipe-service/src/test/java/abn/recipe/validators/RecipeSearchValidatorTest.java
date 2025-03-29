package abn.recipe.validators;

import abn.recipe.exception.ValidationException;
import abn.recipe.model.SearchRecipeRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RecipeSearchValidatorTest {

    private final RecipeSearchValidator validator = new RecipeSearchValidator();

    @Test
    void validateSearchRecipeRequest_whenValidRequest_exceptionIsNotThrown() {
        var request = SearchRecipeRequest.builder().build();
        assertDoesNotThrow(() -> validator.validateSearchRecipeRequest(request));
    }

    @Test
    void validateSearchRecipeRequest_whenInvalidServings_throwsException() {
        var request = SearchRecipeRequest.builder()
                .servings(0)
                .build();
        var exception = assertThrows(ValidationException.class, () -> validator.validateSearchRecipeRequest(request));

        assertThat(exception.getErrors()).containsKey("servings");
        assertThat(exception.getErrors()).containsEntry("servings", "Servings must be at least 1.");
    }

}