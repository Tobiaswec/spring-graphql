package fhhgb.springgraphql.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "ingredients")
public class Ingredient {
    @Id
    @GeneratedValue
    int id;

    @Column
    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    Recipe recipe;

    public Ingredient(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Ingredient(String name, Recipe recipe) {
        this.name = name;
        this.recipe = recipe;
    }

    public Ingredient() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}