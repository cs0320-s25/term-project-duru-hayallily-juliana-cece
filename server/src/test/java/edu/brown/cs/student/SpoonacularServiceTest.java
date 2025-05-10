package edu.brown.cs.student;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.edu.brown.cs.student.main.server.model.Ingredient;
import main.edu.brown.cs.student.main.server.model.Recipe;
import main.edu.brown.cs.student.main.server.service.MockSpoonacularService;
import org.junit.Before;
import org.junit.Test;

public class SpoonacularServiceTest {

  private MockSpoonacularService mockService;

  @Before
  public void setUp() {
    mockService = new MockSpoonacularService();
  }

  @Test
  public void testSearchRecipes() throws Exception {
    Map<String, String> searchParams = new HashMap<>();
    searchParams.put("query", "cookies");

    List<Recipe> results = mockService.searchRecipes(searchParams);

    // Verify we got results
    assertFalse(results.isEmpty());
  }

  @Test
  public void testGetRecipeById() throws Exception {
    Recipe recipe = mockService.getRecipeById(1);

    // Verify the recipe was returned
    assertNotNull(recipe);
    assertEquals(1, recipe.getId());
    assertEquals("Chocolate Chip Cookies", recipe.getTitle());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetRecipeByIdNonExistent() throws Exception {
    // Should throw an exception for non-existent ID
    mockService.getRecipeById(999);
  }

  @Test
  public void testCheckIngredientForAllergens() {
    Ingredient milk = new Ingredient(1, "Milk", "Dairy", 1.0, "cup");
    List<String> allergens = new ArrayList<>();
    allergens.add("dairy");

    // Initially no allergen
    assertFalse(milk.isContainsAllergen());

    boolean result = mockService.checkIngredientForAllergens(milk, allergens);

    // Should be flagged as allergen
    assertTrue(result);
    assertTrue(milk.isContainsAllergen());
    assertEquals(1, milk.getPossibleAllergens().length);
    assertEquals("dairy", milk.getPossibleAllergens()[0]);
  }

  @Test
  public void testCheckIngredientForAllergensNoMatch() {
    Ingredient apple = new Ingredient(2, "Apple", "Produce", 1.0, "");
    List<String> allergens = new ArrayList<>();
    allergens.add("dairy");
    allergens.add("nuts");

    boolean result = mockService.checkIngredientForAllergens(apple, allergens);

    // Should not be flagged as allergen
    assertFalse(result);
    assertFalse(apple.isContainsAllergen());
  }

  @Test
  public void testCheckRecipeForAllergens() {
    Recipe recipe = mockService.getRecipeById(1); // Chocolate Chip Cookies
    List<String> allergens = new ArrayList<>();
    allergens.add("dairy");

    mockService.checkRecipeForAllergens(recipe, allergens);

    // Check if dairy ingredients were flagged
    boolean foundAllergen = false;
    for (Ingredient ingredient : recipe.getIngredients()) {
      if (ingredient.getName().equals("Butter")) {
        assertTrue(ingredient.isContainsAllergen());
        foundAllergen = true;
      }
    }

    assertTrue("Butter should be flagged as containing dairy", foundAllergen);
  }
}
