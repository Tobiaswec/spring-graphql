import { gql } from "apollo-angular";


const RECIPE_DESCRIPTIONS = gql`
    query{

        recipes(type: null){
            id,
            name,
            imageUrl,date
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
