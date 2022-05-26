package fhhgb.springgraphql.repository;

import fhhgb.springgraphql.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.graphql.data.GraphQlRepository;

import java.util.List;

@GraphQlRepository
public interface RecipeRepository extends JpaRepository<Recipe, Integer>, QuerydslPredicateExecutor<Recipe> {

    List<Recipe> findAllByType(String type);
}
