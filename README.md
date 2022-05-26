# Spring GraphQL



## Zielsetzung
Das Ziel dieses Projekts ist es anhand einer simplen Essens-Rezept Applikation zu zeigen, wie man GraphQL in eine Spring-Anwendung integriert und wie man mit dem Framework Apollo über eine Angular-Anwendung darauf zugreifen kann.

Es soll möglich sein, Informationen über der Rezepte in verschiedenen Detail-Graden abzufragen ohne, dass irgendwelche diesbezügliche Anpassungen im Backend vorzunehmen sind, um eine bestmögliche "Seperation of Concerns" umzusetzten.

Es soll eine Übersichtsseite geben, bei der nur der Titel, ein Foto und das Erstelldatum eines Rezepts zu sehen sind.
Weiters soll es eine Detailseite geben, auf der viele weitere Informationen über das Rezept zu sehen sind (z.B. Zutaten, Zubereitung).


## Architektur

![alt text](/images/arch.png)

## Umsetzung

### Integration von GraphQL in Spring
Definieren der Typen und Inputs
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
Um den Datentyp OffsetDateTime nutzen zu können, wie im Code zu sehen ist, muss das Sonatype Repository und eine Bibliothek eingebunden werden.
Einbinden des Sonatype Repository (wird seit 25.05.2022 nicht mehr benötigt):

```gradle
maven { url "https://s01.oss.sonatype.org/content/repositories/snapshots"}
```
Einbinden der Datetime Bibliothek:

```gradle
implementation 'com.tailrocks.graphql:graphql-datetime-spring-boot-starter:5.0.0-SNAPSHOT'
```

Definieren der Queries und Mutationen

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

Wenn mehrere Rezepte mit den dazugehörigen Zutaten abgefragt werden sollen, führt dies zu unnötig vielen SELECT-Statements, da die Zutaten für jedes Rezept einzeln abgefragt werden (N+1 Problem). Um dies zu verhindern, haben wir BatchMapping eingesetzt.

Anwendung von ```@BatchMapping``` zur Behebung des N+1 problems

```java
@BatchMapping
public Map<Recipe, List<Ingredient>>ingredients(List<Recipe> recipe) {
    return repository.findAllByRecipeIsIn(recipe).stream().collect(Collectors.groupingBy(Ingredient::getRecipe));
}
```

Generiertes SELECT-Statement ohne BatchMapping


![alt text](/images/withoutBatchLoading.png)

Generiertes SELECT-Statement mit BatchMapping


![alt text](/images/withBatchLoading.png)

### GraphiQL
Um die Weboberfläche GraphiQL zu aktivieren muss eine Bibliothek importiert werden.
```gradle
implementation 'com.graphql-java-kickstart:graphiql-spring-boot-starter:11.1.0'
```
Anschließend ist die Oberfläche erreichbar: http://localhost:8888/api/graphiql

![alt text](/images/graphiql.jpg)

### Zugriff mit Apollo


Definieren der Queries

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

Ausführen eines GraphQL-Queries in ngOnInit()

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


Ausführen einer GraphQL-Mutation mit Parametern in ```createRecipe()```

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

## Ergebnis
Übersichts-Seite der Angular-App

![alt text](/images/UI-1.png)


Detail-Ansicht der einzelnen Rezepte

![alt text](/images/UI-2.png)


Erzeugen eines neuen Rezepts

![alt text](/images/UI-3.png)

Neues Rezept ist nun auf der Übersichts-Seite sichtbar

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
Da "Spring for GraphQL" erst seit wenigen Monaten existiert, gestaltete sich die Integration etwas aufwändiger als wir es von Spring gewohnt waren, doch die ofizielle Dokumentation und die Links, welche Sie uns zur Verfügung gestellt haben, waren äußerst hilfreich. Sehr viel Logik wird über Annotationen abstrahiert, was den erzeugten Code sehr überschaubar hält. 

Wir sehen den großten Vorteil von GraphQL in der Funktionalität, Daten in beliebiger Granularität abfragen zu können, ohne irgendwelchen spezifischen REST-Endpunkte umzusetzten. Dieser Vorteil kommt jedoch erst zu tragen, wenn beispielsweise sehr viele verschiedene Frontend-Applikationen, verschiedene Ausprägungen von Daten benötigen, was bei unserem kleinen Beispiel nicht wirklich der Fall war. Ebenfalls erspart man sich die Definition von vielen verschieden DTOs. Durch die ganz klare "Seperation of Concerns" gestaltete sich das Auslesen der Daten im Frontend sehr angenehm und es werden keine nicht benötigten Daten übertragen.


## Installationsanleitung
Das start.ps1 Skript ausführen, dieses führ die Mutlistage-Dockerfiles über das docker-compose aus.

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



