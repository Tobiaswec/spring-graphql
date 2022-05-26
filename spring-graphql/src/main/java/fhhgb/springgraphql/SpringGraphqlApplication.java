package fhhgb.springgraphql;

import fhhgb.springgraphql.entity.Ingredient;
import fhhgb.springgraphql.entity.MenuType;
import fhhgb.springgraphql.entity.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import fhhgb.springgraphql.repository.RecipeRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SpringGraphqlApplication {

	@Autowired
	private RecipeRepository repository;

	public static void main(String[] args) {


		SpringApplication.run(SpringGraphqlApplication.class, args);

		/*
		  {
       recipe(id: 1){
      	id
        name
        ingredients{
          name
        }
       }
    }
		 */
	}

	@PostConstruct
	private void loadInitData(){
		List<Ingredient> ingredients = new ArrayList<>();
		Recipe recipe = new Recipe();

		for (int i = 0; i < 5; i++) {
			ingredients.add(new Ingredient("ingredient"+i,recipe));
		}

		recipe.setName("Eierreis");
		recipe.setDescription("desc");
		recipe.setImageUrl("https://www.daskochrezept.de/sites/daskochrezept.de/files/styles/full_width_tablet_4_3/public/2022-02/2022_eierreis_aufmacher.jpg?h=422b3976&itok=ujbhsXM-");
		recipe.setType(MenuType.MEAT.name());
		recipe.setUrl("url");
		recipe.setIngredients(ingredients);

		repository.save(recipe);

		Recipe recipe2 = new Recipe();
		List<Ingredient> ingredients2 = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			ingredients2.add(new Ingredient("ingredientOther"+i,recipe2));
		}

		recipe2.setName("Guglhupf");
		recipe2.setDescription("desc");
		recipe2.setImageUrl("https://www.gutekueche.at/storage/media/recipe/31738/conv/einfacher-guglhupf-default.jpg");
		recipe2.setType(MenuType.MEAT.name());
		recipe2.setUrl("url");
		recipe2.setIngredients(ingredients2);

		repository.save(recipe2);


	}

}
