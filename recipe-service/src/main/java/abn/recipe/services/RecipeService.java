package abn.recipe.services;

import abn.recipe.exception.ObjectNotFoundException;
import abn.recipe.mappers.RecipeMapper;
import abn.recipe.model.SearchRecipeRequest;
import abn.recipe.persistence.entity.DishType;
import abn.recipe.persistence.entity.Ingredient;
import abn.recipe.persistence.entity.Recipe;
import abn.recipe.persistence.filters.RecipeSpecificationBuilder;
import abn.recipe.persistence.repositories.IngredientRepository;
import abn.recipe.persistence.repositories.RecipeRepository;
import abn.recipe.persistence.utils.SortUtils;
import abn.recipe.spec.spec.RecipeDto;
import abn.recipe.validators.RecipeSearchValidator;
import abn.recipe.validators.RecipeValidator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class RecipeService {

    private static final String RECIPE_OBJECT_NAME = "Recipe";
    private static final String INGREDIENT_OBJECT_NAME = "Ingredient";

    private final RecipeRepository recipeRepository;
    private final RecipeSpecificationBuilder specificationBuilder;
    private final SortUtils sortUtils;
    private final IngredientRepository ingredientRepository;
    private final RecipeValidator recipeValidator;
    private final RecipeSearchValidator recipeSearchValidator;
    private final RecipeMapper recipeMapper;

    @Transactional
    public Long addRecipe(RecipeDto recipeDto) {
        recipeValidator.validateRecipeCreation(recipeDto);
        var recipe = recipeRepository.save(recipeMapper.dtoToEntity(recipeDto));
        return recipe.getId();
    }

    @Transactional
    public void updateRecipe(Long recipeId, RecipeDto recipeDto) {
        recipeDto.setId(recipeId);
        recipeValidator.validateRecipeUpdate(recipeId, recipeDto);
        var existingRecipe = getRecipeById(recipeId);
        existingRecipe.setName(recipeDto.getName());
        existingRecipe.setDishType(DishType.valueOf(recipeDto.getDishType().name()));
        existingRecipe.setInstructions(recipeDto.getInstructions());
        existingRecipe.setServings(recipeDto.getServings());
        existingRecipe.setIngredients(updateIngredients(existingRecipe, recipeDto));
        recipeRepository.save(existingRecipe);
    }

    @Transactional
    public void deleteRecipe(Long recipeId) {
        getRecipeById(recipeId);
        recipeRepository.deleteById(recipeId);
    }

    @Transactional(readOnly = true)
    public RecipeDto getRecipe(Long recipeId) {
        return recipeMapper.entityToDto(getRecipeById(recipeId));
    }

    @Transactional(readOnly = true)
    public Page<RecipeDto> fetchRecipes(SearchRecipeRequest recipesRequest) {
        recipeSearchValidator.validateSearchRecipeRequest(recipesRequest);
        var sort = sortUtils.getSort(recipesRequest.sortBy(), recipesRequest.sortDirection());
        var pageable = PageRequest.of(recipesRequest.page(), recipesRequest.pageSize(), sort);
        var specification = specificationBuilder.buildRecipesSpecification(recipesRequest);
        var recipes = recipeRepository.findAll(specification, pageable);
        return recipes.map(recipeMapper::entityToDto);
    }

    private Recipe getRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ObjectNotFoundException(recipeId, RECIPE_OBJECT_NAME));
    }

    private Ingredient getIngredientById(Long ingredientId) {
        return ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ObjectNotFoundException(ingredientId, INGREDIENT_OBJECT_NAME));
    }

    private Set<Ingredient> updateIngredients(Recipe existingRecipe, RecipeDto recipeDto) {
        var updatedIngredients = new HashSet<Ingredient>();
        var dtoIngredientIds = new ArrayList<Long>();

        for (var ingredientDto : recipeDto.getIngredients()) {
            if (ingredientDto.getId() != null) {
                // Existing ingredient.
                var existingIngredient = getIngredientById(ingredientDto.getId());
                recipeMapper.updateIngredientFromDto(ingredientDto, existingIngredient);
                updatedIngredients.add(existingIngredient);
                dtoIngredientIds.add(ingredientDto.getId());
            } else {
                // New ingredient.
                var newIngredient = recipeMapper.dtoToIngredient(ingredientDto);
                newIngredient.setRecipe(existingRecipe);
                updatedIngredients.add(newIngredient);
            }
        }

        ingredientRepository.deleteByRecipeIdAndNotInIngredientIds(existingRecipe.getId(), dtoIngredientIds);
        return updatedIngredients;
    }

}
