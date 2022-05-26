import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Apollo } from 'apollo-angular';
import { RECIPE_DETAILS } from '../graphql/graphql.queries';

@Component({
  selector: 'recipe-details',
  templateUrl: './recipe-details.component.html',
  styleUrls: ['./recipe-details.component.css']
})
export class RecipeDetailsComponent implements OnInit {

  constructor(private router: Router,
    private route: ActivatedRoute,private apollo:Apollo ) { }

  recipeId:number=-1;
  private sub: any;
  recipe: any={name:""};
  loading=true
  error:any;

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
       this.recipeId = +params['id']; 

       this.apollo
       .watchQuery<any>({query:RECIPE_DETAILS,  
         variables: {id: this.recipeId}
         })
       .valueChanges.subscribe((result:any) => {
           this.recipe=result.data.recipe;
           this.loading=result.loading;
           this.error=result.error;
         }
       );
    });


  }
}
