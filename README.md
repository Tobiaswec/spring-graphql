# Spring GraphQL



## Objective
The goal of this project is to show how to integrate GraphQL into a Spring application and how to access it with the Apollo framework via an Angular application using a simple food recipe application.

It should be possible to retrieve information about the recipes in different levels of detail without having to make any related adjustments in the backend to implement the best possible "Seperation of Concerns".

There should be an overview page, where only the title, a photo and the creation date of a recipe are visible.
Furthermore, there should be a detail page where a lot of additional information about the recipe can be seen (e.g. ingredients, preparation).


## Architecture

![alt text](/images/arch.png)

## Implementation

### Integration of GraphQL with Spring
Defining the types and inputs
```
input RecipeInput {
    name: String!
    url: String!
    imageUrl: String!
    type:String!
}

type Recipe {
    id: Int!
    name: String!
    description: String!
    url: String!
    imageUrl: String!
    type:String!
    date:OffsetDateTime
    ingredients: [Ingredient]
}


type Ingredient {
    id: Int!
    name: String!
}

enum MenuType {
    MEAT
    VEGETARIAN
    VEGAN
}

scalar OffsetDateTime
```
To be able to use the OffsetDateTime data type, as can be seen in the code, the Sonatype repository and a library must be included.
Include the Sonatype repository (no longer needed since 25.05.2022):

```gradle
maven { url "https://s01.oss.sonatype.org/content/repositories/snapshots"}
```
Include the Datetime library:

```gradle
implementation 'com.tailrocks.graphql:graphql-datetime-spring-boot-starter:5.0.0-SNAPSHOT'
```

Defining the queries and mutations

```java
type Query {
    recipe(id: Int): Recipe
    recipes(type:String):[Recipe]!
}

type Mutation {
    postRecipe(recipe:RecipeInput):Recipe
    postIngredient(recipeId:Int,ingredientName:String):Recipe
}
```

Query-Mappings

```java
@QueryMapping
public Recipe recipe(@Argument int id) {
	return service.fetchRecipe(id);
}

@QueryMapping
public List<Recipe> recipes(@Argument String type) {
	return service.getAllRecipes(type);
}
```

Mutation-Mappings

```java
@MutationMapping
public Recipe postRecipe(@Argument RecipeInput recipe) {
	return service.persistRecipe(new Recipe(recipe.getName(), recipe.getDescription(), recipe.getUrl(), recipe.getImageUrl(), recipe.getType()));
}
```

#### N+1 Problem
If several recipes with the corresponding ingredients are to be queried, this leads to an unnecessarily large number of SELECT statements, since the ingredients for each recipe are queried individually (N+1 problem). To prevent this, we used BatchMapping.

Using  ```@BatchMapping``` to solve the N+1 problem

```java
@BatchMapping
public Map<Recipe, List<Ingredient>>ingredients(List<Recipe> recipe) {
    return repository.findAllByRecipeIsIn(recipe).stream().collect(Collectors.groupingBy(Ingredient::getRecipe));
}
```

Generated SELECT statements without BatchMapping


![alt text](/images/withoutBatchLoading.png)

Generated SELECT statement with BatchMapping

![alt text](/images/withBatchLoading.png)

### GraphiQL
To activate the GraphiQL web interface, a library must be imported.
```gradle
implementation 'com.graphql-java-kickstart:graphiql-spring-boot-starter:11.1.0'
```
After that, the interface is accessible: http://localhost:8888/api/graphiql

![alt text](/images/graphiql.jpg)

### Access with Apollo


Defining the Queries

```ts
import { gql } from "apollo-angular";


const RECIPE_DESCRIPTIONS = gql`
    query{

        recipes(type: null){
            id,
            name,
            imageUrl,
	    date
        }
    }`;

    const RECIPE_DETAILS = gql`
    query($id: Int!){

        recipe(id: $id){
            id,
            name,
            type,
            description,
            imageUrl,
            ingredients{name}
        }
    }`;


    const RECIPE_CREATE = gql`
    mutation postRecipe($recipe:RecipeInput) {
        postRecipe(recipe:$recipe){
		    id
         name
        }
    }`;

export {RECIPE_DESCRIPTIONS,RECIPE_DETAILS,RECIPE_CREATE};
```

Execute GraphQL queries in ngOnInit()

```ts
@Component({
  selector: 'recipe-list',
  templateUrl: './recipe-list.component.html',
  styleUrls: ['./recipe-list.component.css']
})
export class RecipeListComponent implements OnInit {

  recipes: Subject<Recipe[]>= new Subject<Recipe[]>();

  constructor(private apollo:Apollo) {}

  ngOnInit(): void {
    this.apollo.watchQuery<any>({
      query: RECIPE_DESCRIPTIONS,
      fetchPolicy: "no-cache"
    })
      .valueChanges
      .subscribe(({ data }) => {    
        this.recipes.next(data.recipes)
      });  
  }
}
```


Execute a GraphQL mutation with parameters in ```createRecipe()```

```ts
export class RecipeCreateComponent implements OnInit {

  constructor(private apollo:Apollo)  { }

  recipeName:string="";
  recipeUrl:string="";

  ngOnInit(): void {}

  createRecipe(){
      this.apollo.mutate({
        mutation: RECIPE_CREATE,
        variables: {
          "recipe": {
            "name": this.recipeName,
            "url": "url",
            "imageUrl": this.recipeUrl,
            "type": "MEAT"
        }  
        }
      }).subscribe(({ data }) => {
        console.log('got data', data);
        alert("created new recipe");
      },(error) => {
        console.log('there was an error sending the query', error);
      });
  }

}
```

## Result
Angular app overview page

![alt text](/images/UI-1.png)


Detail view of the individual recipes

![alt text](/images/UI-2.png)


Create a new recipe

![alt text](/images/UI-3.png)

New recipe is now visible on the overview page

![alt text](/images/UI-4.png)


## GraphQL-Query Examples

Query:

```graphql
{
    recipe(id:1){
        id
        name
        ingredients{
            name
        }
    }
}
```

Mutation:

```graphql
mutation postRecipe($recipe:RecipeInput){
  postRecipe(recipe:$recipe){
		id
    name
  }
 }
```
Mutation Variables:
```graphql
{
    "recipe": {
        "name": "sad",
        "url": "url",
        "imageUrl": "imageurl",
        "type": "MEAT"
    }
}
```

## Conclusion
Since "Spring for GraphQL" has only been around for a few months, the integration was a bit more involved than we were used to with Spring, but the official documentation and links you provided were extremely helpful. A lot of logic is abstracted via annotations, which keeps the generated code very manageable. 

We see the biggest advantage of GraphQL in the functionality to query data at any granularity without implementing any specific REST endpoints. However, this advantage only comes into play when, for example, a large number of different frontend applications require different specifications of data, which was not really the case in our small example. Likewise one saves the definition of many different DTOs. Due to the very clear "Seperation of Concerns", the reading of the data in the frontend turned out to be very pleasant and no unneeded data is transferred.



## Installation guide
Run the start.ps1 script, this will run the mutlistage dockerfiles via the docker-compose.

.\start.ps1 

<br>
Database (MySQL):

recipe_db: jdbc:mysql://localhost:3306/graphql_recipe_db
<br>
Credentials in Properties

<br>
GrapghQL Service: http://localhost:8888/api/graphql
<br>
GrapghiQL: http://localhost:8888/api/graphiql
<br>
Recipe-Web: http://localhost:4200/
<br>
Recipe-Web in Docker: http://localhost:8080/



