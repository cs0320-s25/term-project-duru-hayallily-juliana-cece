package edu.brown.cs.student;

import static org.junit.Assert.*;

import java.util.List;
import main.edu.brown.cs.student.main.server.model.GroceryList;
import main.edu.brown.cs.student.main.server.model.Ingredient;
import org.junit.Test;

public class GroceryListTest {

  @Test
  public void testGroceryListCreation() {
    GroceryList groceryList = new GroceryList("user123");

    assertEquals("user123", groceryList.getUserId());
    assertTrue(groceryList.getIngredientsByAisle().isEmpty());
    assertTrue(groceryList.getCheckedItems().isEmpty());
  }

  @Test
  public void testAddIngredient() {
    GroceryList groceryList = new GroceryList("user123");
    Ingredient apple = new Ingredient(1, "Apple", "Produce", 3.0, "");

    groceryList.addIngredient(apple);

    // Verify ingredient was added
    assertEquals(1, groceryList.getIngredientsByAisle().size());
    assertTrue(groceryList.getIngredientsByAisle().containsKey("Produce"));
    assertEquals(1, groceryList.getIngredientsByAisle().get("Produce").size());
    assertEquals(apple, groceryList.getIngredientsByAisle().get("Produce").get(0));
  }

  @Test
  public void testAddMultipleIngredients() {
    GroceryList groceryList = new GroceryList("user123");
    Ingredient apple = new Ingredient(1, "Apple", "Produce", 3.0, "");
    Ingredient banana = new Ingredient(2, "Banana", "Produce", 2.0, "");
    Ingredient milk = new Ingredient(3, "Milk", "Dairy", 1.0, "gallon");

    groceryList.addIngredient(apple);
    groceryList.addIngredient(banana);
    groceryList.addIngredient(milk);

    // Verify ingredients were added to correct aisles
    assertEquals(2, groceryList.getIngredientsByAisle().size());
    assertEquals(2, groceryList.getIngredientsByAisle().get("Produce").size());
    assertEquals(1, groceryList.getIngredientsByAisle().get("Dairy").size());
  }

  @Test
  public void testAddSameIngredientTwice() {
    GroceryList groceryList = new GroceryList("user123");
    Ingredient milk1 = new Ingredient(1, "Milk", "Dairy", 1.0, "gallon");
    Ingredient milk2 = new Ingredient(1, "Milk", "Dairy", 0.5, "gallon");

    groceryList.addIngredient(milk1);
    groceryList.addIngredient(milk2);

    // Should update amount, not add duplicate
    assertEquals(1, groceryList.getIngredientsByAisle().size());
    assertEquals(1, groceryList.getIngredientsByAisle().get("Dairy").size());

    // Amount should be the sum
    Ingredient mergedMilk = groceryList.getIngredientsByAisle().get("Dairy").get(0);
    assertEquals(1.5, mergedMilk.getAmount(), 0.001);
  }

  @Test
  public void testRemoveIngredient() {
    GroceryList groceryList = new GroceryList("user123");
    Ingredient apple = new Ingredient(1, "Apple", "Produce", 3.0, "");
    Ingredient banana = new Ingredient(2, "Banana", "Produce", 2.0, "");

    groceryList.addIngredient(apple);
    groceryList.addIngredient(banana);

    assertEquals(2, groceryList.getIngredientsByAisle().get("Produce").size());

    // Remove one ingredient
    groceryList.removeIngredient(apple);

    assertEquals(1, groceryList.getIngredientsByAisle().get("Produce").size());
    assertEquals(banana, groceryList.getIngredientsByAisle().get("Produce").get(0));
  }

  @Test
  public void testCheckUncheckItem() {
    GroceryList groceryList = new GroceryList("user123");
    Ingredient apple = new Ingredient(1, "Apple", "Produce", 3.0, "");

    groceryList.addIngredient(apple);

    // Initially item is not checked
    assertTrue(groceryList.getCheckedItems().isEmpty());

    // Check the item
    groceryList.checkItem(apple);

    assertEquals(1, groceryList.getCheckedItems().size());
    assertEquals(apple, groceryList.getCheckedItems().get(0));

    // Uncheck the item
    groceryList.uncheckItem(apple);

    assertTrue(groceryList.getCheckedItems().isEmpty());
  }

  @Test
  public void testGetAllIngredients() {
    GroceryList groceryList = new GroceryList("user123");
    Ingredient apple = new Ingredient(1, "Apple", "Produce", 3.0, "");
    Ingredient banana = new Ingredient(2, "Banana", "Produce", 2.0, "");
    Ingredient milk = new Ingredient(3, "Milk", "Dairy", 1.0, "gallon");

    groceryList.addIngredient(apple);
    groceryList.addIngredient(banana);
    groceryList.addIngredient(milk);

    List<Ingredient> allIngredients = groceryList.getAllIngredients();

    assertEquals(3, allIngredients.size());
    assertTrue(allIngredients.contains(apple));
    assertTrue(allIngredients.contains(banana));
    assertTrue(allIngredients.contains(milk));
  }

  @Test
  public void testMoveCheckedItemsToPantry() {
    GroceryList groceryList = new GroceryList("user123");
    Ingredient apple = new Ingredient(1, "Apple", "Produce", 3.0, "");
    Ingredient banana = new Ingredient(2, "Banana", "Produce", 2.0, "");
    Ingredient milk = new Ingredient(3, "Milk", "Dairy", 1.0, "gallon");

    groceryList.addIngredient(apple);
    groceryList.addIngredient(banana);
    groceryList.addIngredient(milk);

    // Check some items
    groceryList.checkItem(apple);
    groceryList.checkItem(milk);

    // Move checked items
    List<Ingredient> movedItems = groceryList.moveCheckedItemsToPantry();

    // Verify moved items
    assertEquals(2, movedItems.size());
    assertTrue(movedItems.contains(apple));
    assertTrue(movedItems.contains(milk));

    // Verify they're removed from grocery list
    assertEquals(1, groceryList.getAllIngredients().size());
    assertTrue(groceryList.getAllIngredients().contains(banana));

    // Verify checked items list is cleared
    assertTrue(groceryList.getCheckedItems().isEmpty());
  }
}
