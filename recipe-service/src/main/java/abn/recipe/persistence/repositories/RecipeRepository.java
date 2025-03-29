package abn.recipe.persistence.repositories;

import abn.recipe.persistence.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}
