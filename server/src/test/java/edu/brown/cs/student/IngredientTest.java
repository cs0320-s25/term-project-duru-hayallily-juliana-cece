package edu.brown.cs.student;

import static org.junit.Assert.*;

import main.edu.brown.cs.student.main.server.model.Ingredient;
import org.junit.Test;

public class IngredientTest {

  @Test
  public void testIngredientCreation() {
    Ingredient ingredient = new Ingredient(1, "Salt", "Spices", 1.0, "tsp");

    assertEquals(1, ingredient.getId());
    assertEquals("Salt", ingredient.getName());
    assertEquals("Spices", ingredient.getAisle());
    assertEquals(1.0, ingredient.getAmount(), 0.001);
    assertEquals("tsp", ingredient.getUnit());
    assertEquals("1.0 tsp Salt", ingredient.getOriginalString());
    assertFalse(ingredient.isContainsAllergen());
    assertEquals(0, ingredient.getPossibleAllergens().length);
  }

  @Test
  public void testSetAllergenInfo() {
    Ingredient ingredient = new Ingredient(2, "Milk", "Dairy", 2.0, "cups");

    // Initially no allergens
    assertFalse(ingredient.isContainsAllergen());

    // Set allergen info
    ingredient.setContainsAllergen(true);
    String[] allergens = {"dairy"};
    ingredient.setPossibleAllergens(allergens);

    assertTrue(ingredient.isContainsAllergen());
    assertEquals(1, ingredient.getPossibleAllergens().length);
    assertEquals("dairy", ingredient.getPossibleAllergens()[0]);
  }

  @Test
  public void testEquals() {
    Ingredient ingredient1 = new Ingredient(3, "Sugar", "Baking", 0.5, "cup");
    Ingredient ingredient2 = new Ingredient(3, "Brown Sugar", "Baking", 0.25, "cup");
    Ingredient ingredient3 = new Ingredient(4, "Sugar", "Baking", 0.5, "cup");

    // Ingredients are equal if they have the same ID, regardless of other properties
    assertEquals(ingredient1, ingredient2);
    assertNotEquals(ingredient1, ingredient3);
  }

  @Test
  public void testToString() {
    Ingredient ingredient = new Ingredient(5, "Eggs", "Refrigerated", 2.0, "");

    assertEquals("2.0  Eggs", ingredient.toString());

    // Test with allergen
    ingredient.setContainsAllergen(true);
    assertEquals("2.0  Eggs (Contains allergen)", ingredient.toString());
  }
}
