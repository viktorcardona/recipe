package abn.recipe.validators;

import abn.recipe.exception.ValidationException;
import abn.recipe.persistence.repositories.RecipeRepository;
import abn.recipe.persistence.repositories.UnitOfMeasureRepository;
import abn.recipe.spec.spec.RecipeDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@AllArgsConstructor
public class RecipeValidator {

    private final RecipeRepository recipeRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;

    public void validateRecipeCreation(RecipeDto recipeDto) {
        var errors = commonValidations(recipeDto, new HashMap<>());

        if (recipeRepository.existsByName(recipeDto.getName())) {
            errors.put("name", "Recipe with this name already exists.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation errors occurred during recipe creation.", errors);
        }
    }

    public void validateRecipeUpdate(Long recipeId, RecipeDto recipeDto) {
        var errors = commonValidations(recipeDto, new HashMap<>());

        if (recipeRepository.existsByNameAndIdNot(recipeDto.getName(), recipeId)) {
            errors.put("name", "Recipe with this name already exists.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation errors occurred during recipe update.", errors);
        }
    }

    private Map<String, String> commonValidations(RecipeDto recipeDto, Map<String, String> errors) {
        if (recipeDto.getServings() == null || recipeDto.getServings() < 1) {
            errors.put("servings", "Servings must be at least 1.");
        }

        for (var ingredient : Optional.ofNullable(recipeDto.getIngredients()).orElse(Collections.emptySet())) {

            if (ingredient.getAmount() == null || ingredient.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                errors.put("ingredient.amount", "Amount must be greater than zero.");
            }

            if (ingredient.getUnitOfMeasure() == null || ingredient.getUnitOfMeasure().getId() == null) {
                errors.put("ingredient.unitOfMeasureId", "Unit of measure is mandatory for ingredient '%s'"
                        .formatted(ingredient.getName()));
            }

            if (ingredient.getUnitOfMeasure() != null
                    && ingredient.getUnitOfMeasure().getId() != null
                    && !unitOfMeasureRepository.existsById(ingredient.getUnitOfMeasure().getId())) {
                errors.put("ingredient.unitOfMeasureId", "Unit of measure '%s' for ingredient '%s' not found"
                        .formatted(ingredient.getUnitOfMeasure().getId(), ingredient.getName()));
            }
        }

        return errors;
    }
}
