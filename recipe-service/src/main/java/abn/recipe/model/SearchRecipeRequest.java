package abn.recipe.model;

import abn.recipe.spec.spec.DishTypeDto;
import lombok.Builder;

import java.util.List;

@Builder
public record SearchRecipeRequest(Integer page,
                                  Integer pageSize,
                                  String sortBy,
                                  String sortDirection,
                                  List<DishTypeDto> dishType,
                                  Boolean dishTypeIsIncluded,
                                  Integer servings,
                                  List<String> ingredients,
                                  Boolean ingredientsIsIncluded,
                                  String instructionsContains) {

}
