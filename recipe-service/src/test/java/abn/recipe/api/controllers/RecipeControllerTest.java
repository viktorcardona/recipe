package abn.recipe.api.controllers;

import abn.recipe.spec.spec.*;
import abn.recipe.core.AbstractRecipeServiceTests;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import static abn.recipe.core.RandomStringUtil.randomStringWithLength;
import static org.assertj.core.api.Assertions.assertThat;

class RecipeControllerTest  extends AbstractRecipeServiceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void recipe_whenCrudRecipe_apiWorks() throws Exception {

        //create recipe in memory
        var recipe = buildRecipeDtoWithDishTypeAndName(DishTypeDto.PESCATARIAN, "Fish and Ships");

        //add recipe into DB
        var recipeId = addRecipe(recipe);
        assertThat(recipeId).isNotNull();

        //retrieve recipe from DB
        var recipeRetrieve = getRecipeById(recipeId);

        //check recipe equals to recipe retrieve from DB
        assertEquals(recipe, recipeRetrieve);

        //update recipe
        var newRecipeName = randomStringWithLength(10);
        var newRecipeInstructions = randomStringWithLength(10);
        recipeRetrieve.setName(newRecipeName);
        recipeRetrieve.setInstructions(newRecipeInstructions);
        updateRecipe(recipeId, recipeRetrieve);

        //retrieve updated recipe from DB
        var recipeUpdateRetrieved = getRecipeById(recipeId);

        //check retrieved updated recipe from DB
        assertThat(recipeUpdateRetrieved.getName()).isEqualTo(newRecipeName);
        assertThat(recipeUpdateRetrieved.getInstructions()).isEqualTo(newRecipeInstructions);

        //delete recipe from DB
        deleteRecipe(recipeId);
    }

    @Test
    void recipe_whenFilterRecipes_apiReturnsRequestedRecipes() throws Exception {

        //create recipes in memory
        var recipe1Vegetarian = buildRecipeDtoWithDishTypeAndName(DishTypeDto.VEGETARIAN, "Grilled Vegetables");
        var recipe2MeatBase = buildRecipeDtoWithDishTypeAndName(DishTypeDto.MEAT_BASED, "Grill Meat");

        //add recipes into DB
        var recipeIdVegetarian = addRecipe(recipe1Vegetarian);
        var recipeIdMeatBase = addRecipe(recipe2MeatBase);
        assertThat(recipeIdVegetarian).isNotNull();
        assertThat(recipeIdMeatBase).isNotNull();

        //retrieve recipes from DB filter by dish type
        var recipesIncludedVegetarian = getRecipesWithIncluded(DishTypeDto.VEGETARIAN);
        var recipesExcludedVegetarian = getRecipesWithExcluded(DishTypeDto.VEGETARIAN);

        //check retrieved vegetarian recipe from DB
        assertThat(recipesIncludedVegetarian).hasSize(1);
        assertThat(recipesIncludedVegetarian.getFirst().getDishType()).isEqualTo(DishTypeDto.VEGETARIAN);

        //check retrieved not-vegetarian recipe from DB
        assertThat(recipesExcludedVegetarian).hasSize(1);
        assertThat(recipesExcludedVegetarian.getFirst().getDishType()).isEqualTo(DishTypeDto.MEAT_BASED);

        //delete recipe from DB
        deleteRecipe(recipeIdVegetarian);
        deleteRecipe(recipeIdMeatBase);
    }

    private void assertEquals(RecipeDto recipe, RecipeDto recipeRetrieve) {
        var firstIngredient = recipe.getIngredients().iterator().next();
        assertThat(recipeRetrieve).isNotNull();
        assertThat(recipeRetrieve.getName()).isEqualTo(recipe.getName());
        assertThat(recipeRetrieve.getDishType()).isEqualTo(recipe.getDishType());
        assertThat(recipeRetrieve.getServings()).isEqualTo(recipe.getServings());
        assertThat(recipeRetrieve.getInstructions()).isEqualTo(recipe.getInstructions());
        assertThat(recipeRetrieve.getIngredients()).isNotNull();
        assertThat(recipeRetrieve.getIngredients()).hasSize(recipe.getIngredients().size());
        var ingredientRetrieve = recipeRetrieve.getIngredients().iterator().next();
        assertThat(ingredientRetrieve.getName()).isEqualTo(firstIngredient.getName());
        assertThat(ingredientRetrieve.getAmount()).isEqualTo(firstIngredient.getAmount());
        assertThat(ingredientRetrieve.getUnitOfMeasure()).isEqualTo(firstIngredient.getUnitOfMeasure());
    }

    private RecipeDto buildRecipeDtoWithDishTypeAndName(DishTypeDto dishType, String name) throws Exception {
        var recipe = new RecipeDto();
        recipe.setName(name);
        recipe.servings(4);
        recipe.setDishType(dishType);
        recipe.setInstructions("instructions to prepare the special dish in the oven");
        var ingredients = new HashSet<IngredientDto>();
        var ingredient = new IngredientDto();
        ingredient.setName("meat");
        ingredient.setAmount(BigDecimal.TEN);
        ingredient.setUnitOfMeasure(getFirstUnitOfMeasure());
        ingredients.add(ingredient);
        recipe.setIngredients(ingredients);
        return recipe;
    }

    private Long addRecipe(RecipeDto recipeDto) throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        var locationHeader = result.getResponse().getHeader("Location");
        assertThat(locationHeader).contains("/v1/recipe/");
        var locationParts = locationHeader.split("/");
        return Long.parseLong(locationParts[locationParts.length - 1]);
    }

    private RecipeDto getRecipeById(Long recipeId) throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/recipe/{recipeId}", recipeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var responseContent = result.getResponse().getContentAsString();
        return objectMapper.readValue(responseContent, RecipeDto.class);
    }

    private List<RecipeDto> getRecipesWithIncluded(DishTypeDto dishType) throws Exception {
        return getRecipesWith(dishType, true);
    }

    private List<RecipeDto> getRecipesWithExcluded(DishTypeDto dishType) throws Exception {
        return getRecipesWith(dishType, false);
    }

    private List<RecipeDto> getRecipesWith(DishTypeDto dishType, boolean included) throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("page_size", "10")
                        .param("dishType", dishType.name())
                        .param("dishTypeIsIncluded", String.valueOf(included)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        var responseContent = result.getResponse().getContentAsString();
        return objectMapper.readValue(responseContent, RecipeListDto.class).getRecipes();
    }

    private void updateRecipe(Long recipeId, RecipeDto recipeDto) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/recipe/{recipeId}", recipeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeDto)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    private void deleteRecipe(Long recipeId) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/recipe/{recipeId}", recipeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    private UnitOfMeasureDto getFirstUnitOfMeasure() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/units")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var responseContent = result.getResponse().getContentAsString();
        List<UnitOfMeasureDto> actualUnits = objectMapper.readValue(responseContent, new TypeReference<>() {});
        return actualUnits.getFirst();
    }

}