import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Apollo } from 'apollo-angular';
import { RECIPE_CREATE } from '../graphql/graphql.queries';


@Component({
  selector: 'recipe-create',
  templateUrl: './recipe-create.component.html',
  styleUrls: ['./recipe-create.component.css']
})
export class RecipeCreateComponent implements OnInit {

  constructor(private apollo:Apollo)  { }

  recipeName:string="";
  recipeUrl:string="";

  ngOnInit(): void {
  }

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
