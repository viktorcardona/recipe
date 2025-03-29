package abn.recipe.validators;

import abn.recipe.exception.ValidationException;
import abn.recipe.persistence.repositories.RecipeRepository;
import abn.recipe.persistence.repositories.UnitOfMeasureRepository;
import abn.recipe.spec.spec.IngredientDto;
import abn.recipe.spec.spec.RecipeDto;
import abn.recipe.spec.spec.UnitOfMeasureDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeValidatorTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UnitOfMeasureRepository unitOfMeasureRepository;

    private RecipeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RecipeValidator(recipeRepository, unitOfMeasureRepository);
    }

    @Test
    void validateRecipeCreation_whenValidRecipe_exceptionIsNotThrown() {
        var recipeDto = new RecipeDto();
        recipeDto.setName("Valid Recipe");
        recipeDto.setServings(1);
        recipeDto.setIngredients(new HashSet<>());

        when(recipeRepository.existsByName("Valid Recipe")).thenReturn(false);

        assertDoesNotThrow(() -> validator.validateRecipeCreation(recipeDto));
    }

    @Test
    void validateRecipeCreation_whenRecipeNameExists_throwsException() {
        var recipeDto = new RecipeDto();
        recipeDto.setName("Existing Recipe");
        recipeDto.setServings(1);
        recipeDto.setIngredients(new HashSet<>());

        when(recipeRepository.existsByName("Existing Recipe")).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class, () -> validator.validateRecipeCreation(recipeDto));

        assertThat(exception.getErrors()).containsKey("name");
        assertThat(exception.getErrors()).containsEntry("name", "Recipe with this name already exists.");
    }

    @Test
    void validateRecipeCreation_whenInvalidServings_throwsException() {
        var recipeDto = new RecipeDto();
        recipeDto.setName("Test Recipe");
        recipeDto.setServings(0);
        recipeDto.setIngredients(new HashSet<>());

        when(recipeRepository.existsByName("Test Recipe")).thenReturn(false);

        ValidationException exception = assertThrows(ValidationException.class, () -> validator.validateRecipeCreation(recipeDto));

        assertThat(exception.getErrors()).containsKey("servings");
        assertThat(exception.getErrors()).containsEntry("servings", "Servings must be at least 1.");
    }

    @Test
    void validateRecipeCreation_whenInvalidIngredientAmount_throwsException() {
        var ingredientDto = new IngredientDto();
        ingredientDto.setAmount(BigDecimal.ZERO);
        UnitOfMeasureDto unitOfMeasureDto = new UnitOfMeasureDto();
        unitOfMeasureDto.setId(1L);
        ingredientDto.setUnitOfMeasure(unitOfMeasureDto);
        ingredientDto.setName("Ingredient");

        var recipeDto = new RecipeDto();
        recipeDto.setName("Test Recipe");
        recipeDto.setServings(1);
        recipeDto.setIngredients(new HashSet<>(Collections.singletonList(ingredientDto)));

        when(recipeRepository.existsByName("Test Recipe")).thenReturn(false);

        var exception = assertThrows(ValidationException.class, () -> validator.validateRecipeCreation(recipeDto));

        assertThat(exception.getErrors()).containsKey("ingredient.amount");
        assertThat(exception.getErrors()).containsEntry("ingredient.amount", "Amount must be greater than zero.");
    }

    @Test
    void validateRecipeCreation_whenInvalidIngredientUnitOfMeasureId_throwsException() {
        var ingredientDto = new IngredientDto();
        ingredientDto.setAmount(BigDecimal.ONE);
        ingredientDto.setName("Ingredient");

        var recipeDto = new RecipeDto();
        recipeDto.setName("Test Recipe");
        recipeDto.setServings(1);
        recipeDto.setIngredients(new HashSet<>(Collections.singletonList(ingredientDto)));

        when(recipeRepository.existsByName("Test Recipe")).thenReturn(false);

        var exception = assertThrows(ValidationException.class, () -> validator.validateRecipeCreation(recipeDto));

        assertThat(exception.getErrors()).containsKey("ingredient.unitOfMeasureId");
        assertThat(exception.getErrors()).containsEntry("ingredient.unitOfMeasureId", "Unit of measure is mandatory for ingredient 'Ingredient'");
    }

    @Test
    void validateRecipeCreation_whenUnitOfMeasureNotFound_throwsException() {
        var ingredientDto = new IngredientDto();
        ingredientDto.setAmount(BigDecimal.ONE);
        UnitOfMeasureDto unitOfMeasureDto = new UnitOfMeasureDto();
        unitOfMeasureDto.setId(1L);
        ingredientDto.setUnitOfMeasure(unitOfMeasureDto);
        ingredientDto.setName("Ingredient");

        var recipeDto = new RecipeDto();
        recipeDto.setName("Test Recipe");
        recipeDto.setServings(1);
        recipeDto.setIngredients(new HashSet<>(Collections.singletonList(ingredientDto)));

        when(recipeRepository.existsByName("Test Recipe")).thenReturn(false);
        when(unitOfMeasureRepository.existsById(1L)).thenReturn(false);

        var exception = assertThrows(ValidationException.class, () -> validator.validateRecipeCreation(recipeDto));

        assertThat(exception.getErrors()).containsKey("ingredient.unitOfMeasureId");
        assertThat(exception.getErrors()).containsEntry("ingredient.unitOfMeasureId", "Unit of measure '1' for ingredient 'Ingredient' not found");
    }

    @Test
    void validateRecipeUpdate_whenValidRecipe_exceptionIsNotThrown() {
        var recipeDto = new RecipeDto();
        recipeDto.setName("Valid Recipe");
        recipeDto.setServings(1);
        recipeDto.setIngredients(new HashSet<>());

        when(recipeRepository.existsByNameAndIdNot("Valid Recipe", 1L)).thenReturn(false);

        assertDoesNotThrow(() -> validator.validateRecipeUpdate(1L, recipeDto));
    }

    @Test
    void validateRecipeUpdate_whenRecipeNameExists_throwsException() {
        var recipeDto = new RecipeDto();
        recipeDto.setName("Existing Recipe");
        recipeDto.setServings(1);
        recipeDto.setIngredients(new HashSet<>());

        when(recipeRepository.existsByNameAndIdNot("Existing Recipe", 1L)).thenReturn(true);

        var exception = assertThrows(ValidationException.class, () -> validator.validateRecipeUpdate(1L, recipeDto));

        assertThat(exception.getErrors()).containsKey("name");
        assertThat(exception.getErrors()).containsEntry("name", "Recipe with this name already exists.");
    }

    @Test
    void validateRecipeUpdate_whenInvalidServings_throwsException() {
        var recipeDto = new RecipeDto();
        recipeDto.setName("Test Recipe");
        recipeDto.setServings(0);
        recipeDto.setIngredients(new HashSet<>());

        when(recipeRepository.existsByNameAndIdNot("Test Recipe", 1L)).thenReturn(false);

        var exception = assertThrows(ValidationException.class, () -> validator.validateRecipeUpdate(1L, recipeDto));

        assertThat(exception.getErrors()).containsKey("servings");
        assertThat(exception.getErrors()).containsEntry("servings", "Servings must be at least 1.");
    }

    @Test
    void validateRecipeUpdate_whenInvalidIngredientAmount_throwsException() {
        var ingredientDto = new IngredientDto();
        ingredientDto.setAmount(BigDecimal.ZERO);
        UnitOfMeasureDto unitOfMeasureDto = new UnitOfMeasureDto();
        unitOfMeasureDto.setId(1L);
        ingredientDto.setUnitOfMeasure(unitOfMeasureDto);
        ingredientDto.setName("Ingredient");

        var recipeDto = new RecipeDto();
        recipeDto.setName("Test Recipe");
        recipeDto.setServings(1);
        recipeDto.setIngredients(new HashSet<>(Collections.singletonList(ingredientDto)));

        when(recipeRepository.existsByNameAndIdNot("Test Recipe", 1L)).thenReturn(false);

        var exception = assertThrows(ValidationException.class, () -> validator.validateRecipeUpdate(1L, recipeDto));

        assertThat(exception.getErrors()).containsKey("ingredient.amount");
        assertThat(exception.getErrors()).containsEntry("ingredient.amount", "Amount must be greater than zero.");
    }

    @Test
    void validateRecipeUpdate_whenInvalidIngredientUnitOfMeasureId_throwsException() {
        var ingredientDto = new IngredientDto();
        ingredientDto.setAmount(BigDecimal.ONE);
        ingredientDto.setName("Ingredient");

        var recipeDto = new RecipeDto();
        recipeDto.setName("Test Recipe");
        recipeDto.setServings(1);
        recipeDto.setIngredients(new HashSet<>(Collections.singletonList(ingredientDto)));

        when(recipeRepository.existsByNameAndIdNot("Test Recipe", 1L)).thenReturn(false);

        var exception = assertThrows(ValidationException.class, () -> validator.validateRecipeUpdate(1L, recipeDto));

        assertThat(exception.getErrors()).containsKey("ingredient.unitOfMeasureId");
        assertThat(exception.getErrors()).containsEntry("ingredient.unitOfMeasureId", "Unit of measure is mandatory for ingredient 'Ingredient'");
    }

    @Test
    void validateRecipeUpdate_whenUnitOfMeasureNotFound_throwsException() {
        var ingredientDto = new IngredientDto();
        ingredientDto.setAmount(BigDecimal.ONE);
        UnitOfMeasureDto unitOfMeasureDto = new UnitOfMeasureDto();
        unitOfMeasureDto.setId(1L);
        ingredientDto.setUnitOfMeasure(unitOfMeasureDto);
        ingredientDto.setName("Ingredient");

        var recipeDto = new RecipeDto();
        recipeDto.setName("Test Recipe");
        recipeDto.setServings(1);
        recipeDto.setIngredients(new HashSet<>(Collections.singletonList(ingredientDto)));

        when(recipeRepository.existsByNameAndIdNot("Test Recipe", 1L)).thenReturn(false);
        when(unitOfMeasureRepository.existsById(1L)).thenReturn(false);

        var exception = assertThrows(ValidationException.class, () -> validator.validateRecipeUpdate(1L, recipeDto));

        assertThat(exception.getErrors()).containsKey("ingredient.unitOfMeasureId");
        assertThat(exception.getErrors()).containsEntry("ingredient.unitOfMeasureId", "Unit of measure '1' for ingredient 'Ingredient' not found");
    }

}