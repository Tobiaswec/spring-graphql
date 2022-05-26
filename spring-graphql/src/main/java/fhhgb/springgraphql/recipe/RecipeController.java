/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fhhgb.springgraphql.recipe;

import fhhgb.springgraphql.entity.Ingredient;
import fhhgb.springgraphql.entity.Recipe;
import fhhgb.springgraphql.entity.RecipeInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import fhhgb.springgraphql.repository.IngredientRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class RecipeController {

    @Autowired
    private RecipeService service;

    @Autowired
    private IngredientRepository repository;

    @QueryMapping
    public Recipe recipe(@Argument int id) {
        return service.fetchRecipe(id);
    }

    @QueryMapping
    public List<Recipe> recipes(@Argument String type) {
        return service.getAllRecipes(type);
    }

    @QueryMapping
    public List<Ingredient> ingredients(Recipe recipe) {
        return service.fetchRecipeIngredients(recipe.getId());
    }

    @BatchMapping
    public Map<Recipe, List<Ingredient>> ingredients(List<Recipe> recipe) {
        return repository.findAllByRecipeIsIn(recipe).stream().collect(Collectors.groupingBy(Ingredient::getRecipe));
    }

    @MutationMapping
    public Recipe postRecipe(@Argument RecipeInput recipe) {
        return service.persistRecipe(new Recipe(recipe.getName(), recipe.getDescription(), recipe.getUrl(), recipe.getImageUrl(), recipe.getType()));
    }

}
