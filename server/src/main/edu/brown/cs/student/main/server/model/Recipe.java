package main.edu.brown.cs.student.main.server.model;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
  private int id;
  private String title;
  private String image;
  private int readyInMinutes;
  private int servings;
  private String sourceUrl;
  private List<Ingredient> ingredients;
  private List<String> instructions;
  private String cuisine;
  private List<String> diets;
  private boolean vegetarian;
  private boolean vegan;
  private boolean glutenFree;
  private boolean dairyFree;

  // For pantry comparison
  private int availableIngredients;
  private int totalIngredients;
  private List<Ingredient> missingIngredients;

  public Recipe() {
    this.ingredients = new ArrayList<>();
    this.instructions = new ArrayList<>();
    this.diets = new ArrayList<>();
    this.missingIngredients = new ArrayList<>();
  }

  // Getters and setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public int getReadyInMinutes() {
    return readyInMinutes;
  }

  public void setReadyInMinutes(int readyInMinutes) {
    this.readyInMinutes = readyInMinutes;
  }

  public int getServings() {
    return servings;
  }

  public void setServings(int servings) {
    this.servings = servings;
  }

  public String getSourceUrl() {
    return sourceUrl;
  }

  public void setSourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  public void setIngredients(List<Ingredient> ingredients) {
    this.ingredients = ingredients;
  }

  public void addIngredient(Ingredient ingredient) {
    if (this.ingredients == null) {
      this.ingredients = new ArrayList<>();
    }
    this.ingredients.add(ingredient);
  }

  public List<String> getInstructions() {
    return instructions;
  }

  public void setInstructions(List<String> instructions) {
    this.instructions = instructions;
  }

  public String getCuisine() {
    return cuisine;
  }

  public void setCuisine(String cuisine) {
    this.cuisine = cuisine;
  }

  public List<String> getDiets() {
    return diets;
  }

  public void setDiets(List<String> diets) {
    this.diets = diets;
  }

  public boolean isVegetarian() {
    return vegetarian;
  }

  public void setVegetarian(boolean vegetarian) {
    this.vegetarian = vegetarian;
  }

  public boolean isVegan() {
    return vegan;
  }

  public void setVegan(boolean vegan) {
    this.vegan = vegan;
  }

  public boolean isGlutenFree() {
    return glutenFree;
  }

  public void setGlutenFree(boolean glutenFree) {
    this.glutenFree = glutenFree;
  }

  public boolean isDairyFree() {
    return dairyFree;
  }

  public void setDairyFree(boolean dairyFree) {
    this.dairyFree = dairyFree;
  }

  public int getAvailableIngredients() {
    return availableIngredients;
  }

  public void setAvailableIngredients(int availableIngredients) {
    this.availableIngredients = availableIngredients;
  }

  public int getTotalIngredients() {
    return totalIngredients;
  }

  public void setTotalIngredients(int totalIngredients) {
    this.totalIngredients = totalIngredients;
  }

  public List<Ingredient> getMissingIngredients() {
    return missingIngredients;
  }

  public void setMissingIngredients(List<Ingredient> missingIngredients) {
    this.missingIngredients = missingIngredients;
  }

  public String getPantryStatus() {
    return availableIngredients + "/" + totalIngredients + " ingredients available";
  }

  public boolean hasAllergens(List<String> userAllergens) {
    if (userAllergens == null || userAllergens.isEmpty()) {
      return false;
    }

    for (Ingredient ingredient : ingredients) {
      if (ingredient.isContainsAllergen()) {
        return true;
      }
    }

    return false;
  }

  @Override
  public String toString() {
    return "Recipe{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", readyInMinutes=" + readyInMinutes +
        ", servings=" + servings +
        ", ingredients=" + ingredients.size() +
        '}';
  }
}
