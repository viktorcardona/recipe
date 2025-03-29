package abn.recipe.persistence.filters;

import abn.recipe.model.SearchRecipeRequest;
import abn.recipe.spec.spec.DishTypeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RecipeSpecificationBuilderTest {

    private RecipeSpecificationBuilder specificationBuilder;

    @BeforeEach
    void setUp() {
        specificationBuilder = new RecipeSpecificationBuilder();
    }

    @Test
    void buildRecipesSpecification_whenNoFilters_returnsSpecification() {
        var request = SearchRecipeRequest.builder().build();
        var specification = specificationBuilder.buildRecipesSpecification(request);
        assertThat(specification).isNotNull();
    }

    @Test
    void buildRecipesSpecification_whenDishTypeIncluded_returnsSpecification() {
        var dishTypes = List.of(DishTypeDto.VEGETARIAN);
        var request = SearchRecipeRequest.builder()
                .dishType(dishTypes)
                .dishTypeIsIncluded(true)
                .build();
        var specification = specificationBuilder.buildRecipesSpecification(request);
        assertThat(specification).isNotNull();
    }

    @Test
    void buildRecipesSpecification_whenDishTypeExcluded_returnsSpecification() {
        var dishTypes = List.of(DishTypeDto.VEGETARIAN);
        var request = SearchRecipeRequest.builder()
                .dishType(dishTypes)
                .dishTypeIsIncluded(false)
                .build();
        var specification = specificationBuilder.buildRecipesSpecification(request);
        assertThat(specification).isNotNull();
    }

    @Test
    void buildRecipesSpecification_whenServingsProvided_returnsSpecification() {
        var request = SearchRecipeRequest.builder()
                .servings(4)
                .build();
        var specification = specificationBuilder.buildRecipesSpecification(request);
        assertThat(specification).isNotNull();
    }

    @Test
    void buildRecipesSpecification_whenIngredientsIncluded_returnsSpecification() {
        var ingredients = List.of("Tomato", "Onion");
        var request = SearchRecipeRequest.builder()
                .ingredients(ingredients)
                .ingredientsIsIncluded(true)
                .build();
        var specification = specificationBuilder.buildRecipesSpecification(request);
        assertThat(specification).isNotNull();
    }

    @Test
    void buildRecipesSpecification_whenIngredientsExcluded_returnsSpecification() {
        var ingredients = List.of("Chocolate", "Sugar");
        var request = SearchRecipeRequest.builder()
                .ingredients(ingredients)
                .ingredientsIsIncluded(false)
                .build();
        var specification = specificationBuilder.buildRecipesSpecification(request);
        assertThat(specification).isNotNull();
    }

    @Test
    void buildRecipesSpecification_whenInstructionsContains_returnsSpecification() {
        var request = SearchRecipeRequest.builder()
                .instructionsContains("bake")
                .build();
        var specification = specificationBuilder.buildRecipesSpecification(request);
        assertThat(specification).isNotNull();
    }

    @Test
    void buildRecipesSpecification_whenAllFilters_returnsSpecification() {
        var dishTypes = List.of(DishTypeDto.VEGETARIAN);
        var ingredients = List.of("Tomato", "Onion");
        var request = SearchRecipeRequest.builder()
                .dishType(dishTypes)
                .dishTypeIsIncluded(true)
                .servings(4)
                .ingredients(ingredients)
                .ingredientsIsIncluded(true)
                .instructionsContains("bake")
                .build();
        var specification = specificationBuilder.buildRecipesSpecification(request);
        assertThat(specification).isNotNull();
    }
}