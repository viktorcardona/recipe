package abn.recipe.mappers;

import abn.recipe.persistence.entity.Ingredient;
import abn.recipe.persistence.entity.Recipe;
import abn.recipe.spec.spec.IngredientDto;
import abn.recipe.spec.spec.RecipeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RecipeMapper {

    @Mapping(source = "dishType", target = "dishType")
    RecipeDto entityToDto(Recipe entity);

    @Mapping(source = "dishType", target = "dishType")
    Recipe dtoToEntity(RecipeDto recipeDto);

    Ingredient dtoToIngredient(IngredientDto ingredientDto);

    void updateIngredientFromDto(IngredientDto ingredientDto, @MappingTarget Ingredient ingredient);
}
