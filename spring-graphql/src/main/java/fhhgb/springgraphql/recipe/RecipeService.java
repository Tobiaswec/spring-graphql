package fhhgb.springgraphql.recipe;

import fhhgb.springgraphql.entity.Ingredient;
import fhhgb.springgraphql.entity.Recipe;
import fhhgb.springgraphql.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class RecipeService {

    @Autowired
    private RecipeRepository repository;

    public Recipe fetchRecipe(int id) {
        return repository.findById(id).get();
    }

    public List<Ingredient> fetchRecipeIngredients(int id) {
        Optional<Recipe> opt = repository.findById(id);
        if (opt.isPresent()) {
            return opt.get().getIngredients();
        }
        return new ArrayList<>();
    }

    public Recipe persistRecipe(Recipe recipe) {
        return repository.save(recipe);
    }

    public List<Recipe> getAllRecipes(String type) {
        if (type == null || type.isEmpty()) {
            return repository.findAll();
        }
        return repository.findAllByType(type);
    }

}
