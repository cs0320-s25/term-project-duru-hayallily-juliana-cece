package main.edu.brown.cs.student.main.server.model;

import java.util.*;

public class GroceryList {
  private String userId;
  private Map<String, List<Ingredient>> ingredientsByAisle;
  private List<Ingredient> checkedItems;

  public GroceryList() {
    this.ingredientsByAisle = new HashMap<>();
    this.checkedItems = new ArrayList<>();
  }

  public GroceryList(String userId) {
    this.userId = userId;
    this.ingredientsByAisle = new HashMap<>();
    this.checkedItems = new ArrayList<>();
  }

  // Add an ingredient to the grocery list, organizing by aisle
  public void addIngredient(Ingredient ingredient) {
    String aisle = ingredient.getAisle() != null ? ingredient.getAisle() : "Other";

    if (!ingredientsByAisle.containsKey(aisle)) {
      ingredientsByAisle.put(aisle, new ArrayList<>());
    }

    // Check if ingredient already exists in the list
    boolean exists = false;
    for (Ingredient existingIngredient : ingredientsByAisle.get(aisle)) {
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
      ingredientsByAisle.get(aisle).add(ingredient);
    }
  }

  // Add a list of ingredients
  public void addIngredients(List<Ingredient> ingredients) {
    for (Ingredient ingredient : ingredients) {
      addIngredient(ingredient);
    }
  }

  // Remove an ingredient from the grocery list
  public void removeIngredient(Ingredient ingredient) {
    String aisle = ingredient.getAisle() != null ? ingredient.getAisle() : "Other";

    if (ingredientsByAisle.containsKey(aisle)) {
      ingredientsByAisle.get(aisle).removeIf(i -> i.getName().equalsIgnoreCase(ingredient.getName()));

      // Remove the aisle if it's empty
      if (ingredientsByAisle.get(aisle).isEmpty()) {
        ingredientsByAisle.remove(aisle);
      }
    }
  }

  // Mark an item as checked
  public void checkItem(Ingredient ingredient) {
    if (!checkedItems.contains(ingredient)) {
      checkedItems.add(ingredient);
    }
  }

  // Uncheck an item
  public void uncheckItem(Ingredient ingredient) {
    checkedItems.removeIf(i -> i.getName().equalsIgnoreCase(ingredient.getName()));
  }

  // Get all ingredients in a flat list
  public List<Ingredient> getAllIngredients() {
    List<Ingredient> allIngredients = new ArrayList<>();
    for (List<Ingredient> aisleIngredients : ingredientsByAisle.values()) {
      allIngredients.addAll(aisleIngredients);
    }
    return allIngredients;
  }

  // Move checked items to pantry
  public List<Ingredient> moveCheckedItemsToPantry() {
    List<Ingredient> itemsToMove = new ArrayList<>(checkedItems);

    // Remove checked items from grocery list
    for (Ingredient ingredient : checkedItems) {
      removeIngredient(ingredient);
    }

    // Clear checked items
    checkedItems.clear();

    return itemsToMove;
  }

  // Getters and setters
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Map<String, List<Ingredient>> getIngredientsByAisle() {
    return ingredientsByAisle;
  }

  public void setIngredientsByAisle(Map<String, List<Ingredient>> ingredientsByAisle) {
    this.ingredientsByAisle = ingredientsByAisle;
  }

  public List<Ingredient> getCheckedItems() {
    return checkedItems;
  }

  public void setCheckedItems(List<Ingredient> checkedItems) {
    this.checkedItems = checkedItems;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Grocery List:\n");

    for (Map.Entry<String, List<Ingredient>> entry : ingredientsByAisle.entrySet()) {
      sb.append("\n").append(entry.getKey()).append(":\n");

      for (Ingredient ingredient : entry.getValue()) {
        sb.append("- ").append(ingredient.toString());
        if (checkedItems.contains(ingredient)) {
          sb.append(" ✓");
        }
        sb.append("\n");
      }
    }

    return sb.toString();
  }
}
