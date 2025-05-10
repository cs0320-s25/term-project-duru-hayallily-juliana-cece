package main.edu.brown.cs.student.main.server.model;

import java.util.*;

public class User {
  private String id;
  private String name;
  private String email;
  private Pantry pantry;
  private GroceryList groceryList;
  private List<Recipe> savedRecipes;
  private List<String> allergies;
  private List<String> diets;

  public User() {
    this.pantry = new Pantry();
    this.groceryList = new GroceryList();
    this.savedRecipes = new ArrayList<>();
    this.allergies = new ArrayList<>();
    this.diets = new ArrayList<>();
  }

  public User(String id, String name, String email) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.pantry = new Pantry(id);
    this.groceryList = new GroceryList(id);
    this.savedRecipes = new ArrayList<>();
    this.allergies = new ArrayList<>();
    this.diets = new ArrayList<>();
  }

  // Add a recipe to the user's saved recipes
  public void addRecipe(Recipe recipe) {
    if (!savedRecipes.contains(recipe)) {
      savedRecipes.add(recipe);
    }
  }

  // Remove a recipe from the user's saved recipes
  public void removeRecipe(Recipe recipe) {
    savedRecipes.removeIf(r -> r.getId() == recipe.getId());
  }

  // Add an allergy
  public void addAllergy(String allergy) {
    if (!allergies.contains(allergy.toLowerCase())) {
      allergies.add(allergy.toLowerCase());
    }
  }

  // Remove an allergy
  public void removeAllergy(String allergy) {
    allergies.removeIf(a -> a.equalsIgnoreCase(allergy));
  }

  // Add a diet preference
  public void addDiet(String diet) {
    if (!diets.contains(diet.toLowerCase())) {
      diets.add(diet.toLowerCase());
    }
  }

  // Remove a diet preference
  public void removeDiet(String diet) {
    diets.removeIf(d -> d.equalsIgnoreCase(diet));
  }

  // Check if a recipe is compatible with the user's allergies
  public boolean isRecipeSafeForAllergies(Recipe recipe) {
    if (allergies.isEmpty()) {
      return true;
    }

    return !recipe.hasAllergens(allergies);
  }

  // Check if a recipe is compatible with the user's diets
  public boolean isRecipeCompatibleWithDiets(Recipe recipe) {
    if (diets.isEmpty()) {
      return true;
    }

    for (String diet : diets) {
      boolean compatible = false;

      switch (diet.toLowerCase()) {
        case "vegetarian":
          compatible = recipe.isVegetarian();
          break;
        case "vegan":
          compatible = recipe.isVegan();
          break;
        case "gluten free":
          compatible = recipe.isGlutenFree();
          break;
        case "dairy free":
          compatible = recipe.isDairyFree();
          break;
        default:
          // For other diets, check if the recipe lists that diet
          compatible = recipe.getDiets().stream()
              .anyMatch(d -> d.equalsIgnoreCase(diet));
      }

      if (!compatible) {
        return false;
      }
    }

    return true;
  }

  // Getters and setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Pantry getPantry() {
    return pantry;
  }

  public void setPantry(Pantry pantry) {
    this.pantry = pantry;
  }

  public GroceryList getGroceryList() {
    return groceryList;
  }

  public void setGroceryList(GroceryList groceryList) {
    this.groceryList = groceryList;
  }

  public List<Recipe> getSavedRecipes() {
    return savedRecipes;
  }

  public void setSavedRecipes(List<Recipe> savedRecipes) {
    this.savedRecipes = savedRecipes;
  }

  public List<String> getAllergies() {
    return allergies;
  }

  public void setAllergies(List<String> allergies) {
    this.allergies = allergies;
  }

  public List<String> getDiets() {
    return diets;
  }

  public void setDiets(List<String> diets) {
    this.diets = diets;
  }
}
