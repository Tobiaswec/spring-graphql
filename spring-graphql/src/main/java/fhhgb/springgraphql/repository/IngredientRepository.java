package fhhgb.springgraphql.repository;

import fhhgb.springgraphql.entity.Ingredient;
import fhhgb.springgraphql.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.graphql.data.GraphQlRepository;

import java.util.List;

@GraphQlRepository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer>, QuerydslPredicateExecutor<Ingredient> {

    public List<Ingredient> findAllByRecipeIsIn(List<Recipe> recipeList);
}
