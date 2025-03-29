package abn.recipe.api.controllers;

import abn.recipe.api.mapper.PaginationMapper;
import abn.recipe.model.SearchRecipeRequest;
import abn.recipe.services.RecipeService;
import abn.recipe.spec.api.RecipeApi;
import abn.recipe.spec.spec.DishTypeDto;
import abn.recipe.spec.spec.RecipeDto;
import abn.recipe.spec.spec.RecipeListDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

@RestController
@AllArgsConstructor
public class RecipeController implements RecipeApi {

    private final RecipeService recipeService;
    private final PaginationMapper paginationMapper;

    @Override
    public ResponseEntity<RecipeDto> getRecipeById(@Parameter(name = "recipeId",description = "ID of the recipe to retrieve.",required = true,in = ParameterIn.PATH) @PathVariable("recipeId") Long recipeId) {
        return ResponseEntity.ok(recipeService.getRecipe(recipeId));
    }
    @Override
    public ResponseEntity<RecipeListDto> getRecipes(@Parameter(name = "page",description = "Pagination page number. Starts at 0, not 1",in = ParameterIn.QUERY) @RequestParam(value = "page",required = false,defaultValue = "0") @Valid Integer page, @Parameter(name = "page_size",description = "Pagination page elements size",in = ParameterIn.QUERY) @RequestParam(value = "page_size",required = false,defaultValue = "0") @Valid Integer pageSize, @Parameter(name = "sortBy",description = "The field to sort by.",in = ParameterIn.QUERY) @RequestParam(value = "sortBy",required = false) @Valid String sortBy, @Parameter(name = "sortDirection",description = "The sort direction (asc or desc).",in = ParameterIn.QUERY) @RequestParam(value = "sortDirection",required = false) @Valid String sortDirection, @Parameter(name = "dishType",description = "dish type",in = ParameterIn.QUERY) @RequestParam(value = "dishType",required = false) @Valid List<DishTypeDto> dishType, @Parameter(name = "dishTypeIsIncluded",description = "Indicates if the dish type should be included (true) or excluded (false).",in = ParameterIn.QUERY) @RequestParam(value = "dishTypeIsIncluded",required = false,defaultValue = "true") @Valid Boolean dishTypeIsIncluded, @Parameter(name = "servings",description = "servings",in = ParameterIn.QUERY) @RequestParam(value = "servings",required = false) @Valid Integer servings, @Parameter(name = "ingredients",description = "the ingredients (included or excluded) in the recipe",in = ParameterIn.QUERY) @RequestParam(value = "ingredients",required = false) @Valid List<String> ingredients, @Parameter(name = "ingredientsIsIncluded",description = "Indicates if the ingredients should be included (true) or excluded (false).",in = ParameterIn.QUERY) @RequestParam(value = "ingredientsIsIncluded",required = false,defaultValue = "true") @Valid Boolean ingredientsIsIncluded, @Parameter(name = "instructionsContains",description = "the instructions contains the given value",in = ParameterIn.QUERY) @RequestParam(value = "instructionsContains",required = false) @Valid String instructionsContains) {
        var searchRequest = SearchRecipeRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .dishType(dishType)
                .dishTypeIsIncluded(dishTypeIsIncluded)
                .servings(servings)
                .ingredients(ingredients)
                .ingredientsIsIncluded(ingredientsIsIncluded)
                .instructionsContains(instructionsContains)
                .build();
        var recipesPage = recipeService.fetchRecipes(searchRequest);
        var recipeList = new RecipeListDto();
        recipeList.setPagination(paginationMapper.mapPaginationDto(recipesPage));
        recipeList.setRecipes(recipesPage.stream().toList());
        return ResponseEntity.ok(recipeList);
    }

    @Override
    public ResponseEntity<Void> addRecipe(@Parameter(name = "RecipeDto",description = "",required = true) @RequestBody @Valid RecipeDto recipeDto) {
        var recipeId = recipeService.addRecipe(recipeDto);
        final UriComponentsBuilder uriComponentsBuilder = fromCurrentContextPath()
                .path("/v1/recipe/{id}");
        final UriComponents uriComponents = uriComponentsBuilder
                .buildAndExpand(recipeId);
        return created(uriComponents.toUri()).build();

    }

    @Override
    public ResponseEntity<Void> updateRecipe(@Parameter(name = "recipeId",description = "ID of the recipe to update.",required = true,in = ParameterIn.PATH) @PathVariable("recipeId") Long recipeId, @Parameter(name = "RecipeDto",description = "",required = true) @RequestBody @Valid RecipeDto recipeDto) {
        recipeService.updateRecipe(recipeId, recipeDto);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteRecipe(@Parameter(name = "recipeId",description = "ID of the recipe to delete.",required = true,in = ParameterIn.PATH) @PathVariable("recipeId") Long recipeId) {
        recipeService.deleteRecipe(recipeId);
        return ResponseEntity.noContent().build();
    }

}
