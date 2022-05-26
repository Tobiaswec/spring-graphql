package fhhgb.springgraphql.entity;


import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue
    int id;

    @Column
    String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "recipe", fetch = FetchType.LAZY)
    List<Ingredient> ingredients = new ArrayList<>();

    @Column
    String description;

    @Column(name = "url", length = 10000)
    String url;

    @Column(name = "image_url", length = 10000)
    String imageUrl;

    @Column(name = "type")
    String type;

    @Column(name = "date")
    OffsetDateTime date = OffsetDateTime.now();

    public Recipe() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return this.id == recipe.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, ingredients, description, url, imageUrl, type, date);
    }

    public Recipe(String name, List<Ingredient> ingredients, String description, String url, String imageUrl, String type) {
        this.name = name;
        this.ingredients = ingredients;
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    public Recipe(String name, String description, String url, String imageUrl, String type) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
        this.type = type;
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

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

