package main.edu.brown.cs.student.main.server.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.edu.brown.cs.student.main.server.model.Ingredient;
import main.edu.brown.cs.student.main.server.model.Recipe;

/**
 * Mock implementation of SpoonacularService for testing
 */
public class MockSpoonacularService extends SpoonacularService {

  private final Map<Integer, Recipe> mockRecipes;
  private final List<Recipe> searchResults;

  public MockSpoonacularService() {
    super("mock-api-key");
    this.mockRecipes = new HashMap<>();
    this.searchResults = new ArrayList<>();
    initializeMockData();
  }

  private void initializeMockData() {
    // Create a few mock recipes
    Recipe recipe1 = new Recipe();
    recipe1.setId(1);
    recipe1.setTitle("Chocolate Chip Cookies");
    recipe1.setReadyInMinutes(30);
    recipe1.setServings(12);
    recipe1.setVegetarian(true);
    recipe1.setVegan(false);
    recipe1.setGlutenFree(false);
    recipe1.setDairyFree(false);

    List<String> instructions1 = new ArrayList<>();
    instructions1.add("Preheat oven to 350°F.");
    instructions1.add("Mix butter and sugars until creamy.");
    instructions1.add("Add eggs and vanilla.");
    instructions1.add("Combine dry ingredients and add to mixture.");
    instructions1.add("Fold in chocolate chips.");
    instructions1.add("Bake for 10-12 minutes.");
    recipe1.setInstructions(instructions1);

    recipe1.addIngredient(new Ingredient(1001, "Butter", "Dairy", 1.0, "cup"));
    recipe1.addIngredient(new Ingredient(1002, "Sugar", "Baking", 0.75, "cup"));
    recipe1.addIngredient(new Ingredient(1003, "Brown Sugar", "Baking", 0.75, "cup"));
    recipe1.addIngredient(new Ingredient(1004, "Eggs", "Refrigerated", 2.0, ""));
    recipe1.addIngredient(new Ingredient(1005, "Vanilla Extract", "Baking", 1.0, "tsp"));
    recipe1.addIngredient(new Ingredient(1006, "All-Purpose Flour", "Baking", 2.25, "cups"));
    recipe1.addIngredient(new Ingredient(1007, "Baking Soda", "Baking", 1.0, "tsp"));
    recipe1.addIngredient(new Ingredient(1008, "Salt", "Spices", 0.5, "tsp"));
    recipe1.addIngredient(new Ingredient(1009, "Chocolate Chips", "Baking", 2.0, "cups"));

    Recipe recipe2 = new Recipe();
    recipe2.setId(2);
    recipe2.setTitle("Vegetable Stir Fry");
    recipe2.setReadyInMinutes(20);
    recipe2.setServings(4);
    recipe2.setVegetarian(true);
    recipe2.setVegan(true);
    recipe2.setGlutenFree(true);
    recipe2.setDairyFree(true);

    List<String> instructions2 = new ArrayList<>();
    instructions2.add("Heat oil in a large wok or skillet.");
    instructions2.add("Add garlic and ginger, sauté for 30 seconds.");
    instructions2.add("Add vegetables and stir fry for 5-7 minutes.");
    instructions2.add("Mix sauce ingredients and add to the pan.");
    instructions2.add("Stir fry for another 2 minutes until sauce thickens.");
    instructions2.add("Serve over rice or noodles.");
    recipe2.setInstructions(instructions2);

    recipe2.addIngredient(new Ingredient(2001, "Olive Oil", "Oil, Vinegar, Salad Dressing", 2.0, "tbsp"));
    recipe2.addIngredient(new Ingredient(2002, "Garlic", "Produce", 3.0, "cloves"));
    recipe2.addIngredient(new Ingredient(2003, "Ginger", "Produce", 1.0, "tbsp"));
    recipe2.addIngredient(new Ingredient(2004, "Bell Pepper", "Produce", 1.0, ""));
    recipe2.addIngredient(new Ingredient(2005, "Broccoli", "Produce", 2.0, "cups"));
    recipe2.addIngredient(new Ingredient(2006, "Carrots", "Produce", 2.0, ""));
    recipe2.addIngredient(new Ingredient(2007, "Soy Sauce", "Ethnic Foods", 3.0, "tbsp"));
    recipe2.addIngredient(new Ingredient(2008, "Sesame Oil", "Oil, Vinegar, Salad Dressing", 1.0, "tsp"));

    // Store mock recipes
    mockRecipes.put(recipe1.getId(), recipe1);
    mockRecipes.put(recipe2.getId(), recipe2);

    // Add to search results
    searchResults.add(recipe1);
    searchResults.add(recipe2);
  }

  @Override
  public List<Recipe> searchRecipes(Map<String, String> searchParams) {
    // For testing, just return all mock recipes
    // In a more complex test, we could filter based on searchParams
    return new ArrayList<>(searchResults);
  }

  @Override
  public Recipe getRecipeById(int id) {
    Recipe recipe = mockRecipes.get(id);
    if (recipe == null) {
      throw new IllegalArgumentException("Recipe not found with ID: " + id);
    }
    return recipe;
  }

  @Override
  public boolean checkIngredientForAllergens(Ingredient ingredient, List<String> allergens) {
    if (allergens == null || allergens.isEmpty()) {
      return false;
    }

    // Simple check for testing
    String ingredientName = ingredient.getName().toLowerCase();
    for (String allergen : allergens) {
      if (ingredientName.contains(allergen.toLowerCase())) {
        ingredient.setContainsAllergen(true);
        ingredient.setPossibleAllergens(new String[]{allergen});
        return true;
      }
    }

    return false;
  }

  @Override
  public void checkRecipeForAllergens(Recipe recipe, List<String> allergens) {
    if (allergens == null || allergens.isEmpty()) {
      return;
    }

    for (Ingredient ingredient : recipe.getIngredients()) {
      checkIngredientForAllergens(ingredient, allergens);
    }
  }
}
