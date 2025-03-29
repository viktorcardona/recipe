package abn.recipe.validators;

import abn.recipe.exception.ValidationException;
import abn.recipe.model.SearchRecipeRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class RecipeSearchValidator {

    public void validateSearchRecipeRequest(SearchRecipeRequest recipesRequest) {
        var errors = new HashMap<String, String>();

        if (recipesRequest.servings() != null && recipesRequest.servings() < 1) {
            errors.put("servings", "Servings must be at least 1.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation errors occurred during recipe retrieval.", errors);
        }
    }
}
