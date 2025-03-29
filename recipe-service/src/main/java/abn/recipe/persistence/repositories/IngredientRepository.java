package abn.recipe.persistence.repositories;

import abn.recipe.persistence.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    @Modifying
    @Query("DELETE FROM Ingredient i WHERE i.recipe.id = :recipeId AND i.id NOT IN :ingredientIds")
    void deleteByRecipeIdAndNotInIngredientIds(@Param("recipeId") Long recipeId, @Param("ingredientIds") List<Long> ingredientIds);
}
