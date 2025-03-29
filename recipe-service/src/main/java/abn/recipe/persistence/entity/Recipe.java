package abn.recipe.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Long lockVersion = 1L;
    private String name;
    @Enumerated(value = EnumType.STRING)
    private DishType dishType;
    private Integer servings;
    private String instructions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipe")
    private Set<Ingredient> ingredients = new HashSet<>();

    public void setIngredients(Set<Ingredient> ingredients) {
        this.ingredients = ingredients;
        if (ingredients != null) {
            ingredients.forEach(ingredient -> ingredient.setRecipe(this));
        }
    }
}
