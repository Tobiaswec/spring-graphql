package fhhgb.springgraphql.entity;

public class RecipeInput {
    String name;
    String url;
    String imageUrl;
    String type;
    String description;

    public RecipeInput() {
    }

    public RecipeInput(String name,String description, String url, String imageUrl, String type) {
        this.name = name;
        this.url = url;
        this.imageUrl = imageUrl;
        this.type = type;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
