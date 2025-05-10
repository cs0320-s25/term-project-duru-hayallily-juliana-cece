package main.edu.brown.cs.student.main.server.model;

import java.util.*;

public class Pantry {
  private String userId;
  private Map<String, List<Ingredient>> ingredientsByCategory;

  public Pantry() {
    this.ingredientsByCategory = new HashMap<>();
  }

  public Pantry(String userId) {
    this.userId = userId;
    this.ingredientsByCategory = new HashMap<>();
  }

  // Add an ingredient to the pantry
  public void addIngredient(Ingredient ingredient) {
    String category = ingredient.getAisle() != null ? ingredient.getAisle() : "Other";

    if (!ingredientsByCategory.containsKey(category)) {
      ingredientsByCategory.put(category, new ArrayList<>());
    }

    // Check if ingredient already exists in the pantry
    boolean exists = false;
    for (Ingredient existingIngredient : ingredientsByCategory.get(category)) {
      if (existingIngredient.getName().equalsIgnoreCase(ingredient.getName())) {
        // If the same ingredient exists, update the amount
        existingIngredient.setAmount(existingIngredient.getAmount() + ingredient.getAmount());
        existingIngredient.setOriginalString(existingIngredient.getAmount() + " " +
            existingIngredient.getUnit() + " " +
            existingIngredient.getName());
        exists = true;
        break;
      }
    }

    if (!exists) {
      ingredientsByCategory.get(category).add(ingredient);
    }
  }

  // Add multiple ingredients to the pantry
  public void addIngredients(List<Ingredient> ingredients) {
    for (Ingredient ingredient : ingredients) {
      addIngredient(ingredient);
    }
  }

  // Remove an ingredient from the pantry
  public void removeIngredient(Ingredient ingredient) {
    String category = ingredient.getAisle() != null ? ingredient.getAisle() : "Other";

    if (ingredientsByCategory.containsKey(category)) {
      ingredientsByCategory.get(category).removeIf(i -> i.getName().equalsIgnoreCase(ingredient.getName()));

      // Remove the category if it's empty
      if (ingredientsByCategory.get(category).isEmpty()) {
        ingredientsByCategory.remove(category);
      }
    }
  }

  // Update an ingredient's amount (e.g., when using in a recipe)
  public void updateIngredientAmount(String ingredientName, double newAmount) {
    for (List<Ingredient> categoryIngredients : ingredientsByCategory.values()) {
      for (Ingredient ingredient : categoryIngredients) {
        if (ingredient.getName().equalsIgnoreCase(ingredientName)) {
          ingredient.setAmount(newAmount);
          ingredient.setOriginalString(ingredient.getAmount() + " " +
              ingredient.getUnit() + " " +
              ingredient.getName());
          return;
        }
      }
    }
  }

  // Check if the pantry contains an ingredient (exact match)
  public boolean containsIngredient(String ingredientName) {
    for (List<Ingredient> categoryIngredients : ingredientsByCategory.values()) {
      for (Ingredient ingredient : categoryIngredients) {
        if (ingredient.getName().equalsIgnoreCase(ingredientName)) {
          return true;
        }
      }
    }
    return false;
  }

  // Check if pantry has enough of an ingredient for a recipe
  public boolean hasEnoughOf(Ingredient recipeIngredient) {
    for (List<Ingredient> categoryIngredients : ingredientsByCategory.values()) {
      for (Ingredient pantryIngredient : categoryIngredients) {
        if (pantryIngredient.getName().equalsIgnoreCase(recipeIngredient.getName())) {
          // If units match, do a direct comparison
          if (pantryIngredient.getUnit().equalsIgnoreCase(recipeIngredient.getUnit())) {
            return pantryIngredient.getAmount() >= recipeIngredient.getAmount();
          }
          // If units don't match, assume we have it (unit conversion is complex)
          // In a full implementation, you would want to add unit conversion logic here
          return true;
        }
      }
    }
    return false;
  }

  // Get all ingredients in the pantry as a flat list
  public List<Ingredient> getAllIngredients() {
    List<Ingredient> allIngredients = new ArrayList<>();
    for (List<Ingredient> categoryIngredients : ingredientsByCategory.values()) {
      allIngredients.addAll(categoryIngredients);
    }
    return allIngredients;
  }

  // Compare pantry to recipe and return available/missing ingredients
  public Map<String, Object> compareWithRecipe(Recipe recipe) {
    int available = 0;
    List<Ingredient> missing = new ArrayList<>();

    for (Ingredient recipeIngredient : recipe.getIngredients()) {
      if (hasEnoughOf(recipeIngredient)) {
        available++;
      } else {
        missing.add(recipeIngredient);
      }
    }

    Map<String, Object> result = new HashMap<>();
    result.put("availableCount", available);
    result.put("totalCount", recipe.getIngredients().size());
    result.put("missingIngredients", missing);

    return result;
  }

  // Getters and setters
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Map<String, List<Ingredient>> getIngredientsByCategory() {
    return ingredientsByCategory;
  }

  public void setIngredientsByCategory(Map<String, List<Ingredient>> ingredientsByCategory) {
    this.ingredientsByCategory = ingredientsByCategory;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Pantry Contents:\n");

    for (Map.Entry<String, List<Ingredient>> entry : ingredientsByCategory.entrySet()) {
      sb.append("\n").append(entry.getKey()).append(":\n");

      for (Ingredient ingredient : entry.getValue()) {
        sb.append("- ").append(ingredient.toString()).append("\n");
      }
    }

    return sb.toString();
  }
}
