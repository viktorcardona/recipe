package abn.recipe.persistence.filters;

import abn.recipe.model.SearchRecipeRequest;
import abn.recipe.persistence.entity.DishType;
import abn.recipe.persistence.entity.Recipe;
import abn.recipe.spec.spec.DishTypeDto;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;

@Component
public class RecipeSpecificationBuilder {

    public Specification<Recipe> buildRecipesSpecification(SearchRecipeRequest recipesRequest) {
        return (root, query, criteriaBuilder) -> {
            Specification<Recipe> spec = Specification.where(null);

            spec = applyDishTypeFilter(spec, recipesRequest);
            spec = applyServingsFilter(spec, recipesRequest);
            spec = applyIngredientsFilter(spec, recipesRequest);
            spec = applyInstructionsFilter(spec, recipesRequest);

            return spec.toPredicate(root, query, criteriaBuilder);
        };
    }

    private Specification<Recipe> applyDishTypeFilter(Specification<Recipe> spec, SearchRecipeRequest recipesRequest) {
        if (!isEmpty(recipesRequest.dishType())) {
            var dishTypeEnums = recipesRequest.dishType().stream().map(DishTypeDto::name).map(DishType::valueOf).toList();
            if (Boolean.TRUE.equals(recipesRequest.dishTypeIsIncluded())) {
                spec = spec.and((rootRecipe, queryRecipe, criteriaBuilderRecipe) -> rootRecipe.get("dishType").in(dishTypeEnums));
            } else {
                spec = spec.and((rootRecipe, queryRecipe, criteriaBuilderRecipe) -> criteriaBuilderRecipe.not(rootRecipe.get("dishType").in(dishTypeEnums)));
            }
        }
        return spec;
    }

    private Specification<Recipe> applyServingsFilter(Specification<Recipe> spec, SearchRecipeRequest recipesRequest) {
        if (recipesRequest.servings() != null) {
            spec = spec.and((rootRecipe, queryRecipe, criteriaBuilderRecipe) -> criteriaBuilderRecipe.equal(rootRecipe.get("servings"), recipesRequest.servings()));
        }
        return spec;
    }

    private Specification<Recipe> applyIngredientsFilter(Specification<Recipe> spec, SearchRecipeRequest recipesRequest) {
        if (!isEmpty(recipesRequest.ingredients())) {
            if (Boolean.TRUE.equals(recipesRequest.ingredientsIsIncluded())) {
                spec = spec.and((rootRecipe, queryRecipe, criteriaBuilderRecipe) -> {
                    Join<Object, Object> ingredientsJoin = rootRecipe.join("ingredients");
                    return ingredientsJoin.get("name").in(recipesRequest.ingredients());
                });
            } else {
                spec = spec.and((rootRecipe, queryRecipe, criteriaBuilderRecipe) -> {
                    var subQuery = queryRecipe.subquery(Long.class);
                    var subRoot = subQuery.from(Recipe.class);
                    var subIngredientsJoin = subRoot.join("ingredients");

                    subQuery.select(subRoot.get("id"))
                            .where(subIngredientsJoin.get("name").in(recipesRequest.ingredients()));

                    return criteriaBuilderRecipe.not(rootRecipe.get("id").in(subQuery));
                });
            }
        }
        return spec;
    }

    private Specification<Recipe> applyInstructionsFilter(Specification<Recipe> spec, SearchRecipeRequest recipesRequest) {
        if (hasText(recipesRequest.instructionsContains())) {
            spec = spec.and((rootRecipe, queryRecipe, criteriaBuilderRecipe) -> criteriaBuilderRecipe.like(criteriaBuilderRecipe.lower(rootRecipe.get("instructions")), "%" + recipesRequest.instructionsContains().toLowerCase() + "%"));
        }
        return spec;
    }
}
