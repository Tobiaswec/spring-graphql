import { query } from '@angular/animations';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Apollo, gql } from 'apollo-angular';
import { BehaviorSubject, map, Observable, Subject, Subscription } from 'rxjs';

import { RECIPE_DESCRIPTIONS, RECIPE_DETAILS } from "src/app/graphql/graphql.queries"


export interface Recipe{
  id:number,
  name : string;
  imageUrl:string;
  date:any;
}

@Component({
  selector: 'recipe-list',
  templateUrl: './recipe-list.component.html',
  styleUrls: ['./recipe-list.component.css']
})
export class RecipeListComponent implements OnInit {

  recipes: Subject<Recipe[]>= new Subject<Recipe[]>();
  loading=true;
  error:any;

  constructor(private apollo:Apollo) {
   }

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
