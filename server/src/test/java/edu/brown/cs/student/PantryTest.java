package edu.brown.cs.student;

import main.edu.brown.cs.student.main.server.model.Ingredient;
import main.edu.brown.cs.student.main.server.model.Pantry;
import main.edu.brown.cs.student.main.server.model.Recipe;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

public class PantryTest {
    
    @Test
    public void testPantryCreation() {
        Pantry pantry = new Pantry("user123");
        
        assertEquals("user123", pantry.getUserId());
        assertTrue(pantry.getIngredientsByCategory().isEmpty());
    }
    
    @Test
    public void testAddIngredient() {
        Pantry pantry = new Pantry("user123");
        Ingredient salt = new Ingredient(1, "Salt", "Spices", 1.0, "tsp");
        
        pantry.addIngredient(salt);
        
        // Check if ingredient was added
        assertEquals(1, pantry.getIngredientsByCategory().size());
        assertTrue(pantry.getIngredientsByCategory().containsKey("Spices"));
        assertEquals(1, pantry.getIngredientsByCategory().get("Spices").size());
        assertEquals(salt, pantry.getIngredientsByCategory().get("Spices").get(0));
    }
    
    @Test
    public void testAddSameIngredientTwice() {
        Pantry pantry = new Pantry("user123");
        Ingredient salt1 = new Ingredient(1, "Salt", "Spices", 1.0, "tsp");
        Ingredient salt2 = new Ingredient(1, "Salt", "Spices", 2.0, "tsp");
        
        pantry.addIngredient(salt1);
        pantry.addIngredient(salt2);
        
        // Should update the amount, not add a new entry
        assertEquals(1, pantry.getIngredientsByCategory().size());
        assertEquals(1, pantry.getIngredientsByCategory().get("Spices").size());
        
        // Amount should be the sum
        Ingredient mergedSalt = pantry.getIngredientsByCategory().get("Spices").get(0);
        assertEquals(3.0, mergedSalt.getAmount(), 0.001);
    }
    
    @Test
    public void testRemoveIngredient() {
        Pantry pantry = new Pantry("user123");
        Ingredient salt = new Ingredient(1, "Salt", "Spices", 1.0, "tsp");
        Ingredient pepper = new Ingredient(2, "Pepper", "Spices", 0.5, "tsp");
        
        pantry.addIngredient(salt);
        pantry.addIngredient(pepper);
        
        assertEquals(2, pantry.getIngredientsByCategory().get("Spices").size());
        
        // Remove one ingredient
        pantry.removeIngredient(salt);
        
        assertEquals(1, pantry.getIngredientsByCategory().get("Spices").size());
        assertEquals(pepper, pantry.getIngredientsByCategory().get("Spices").get(0));
    }
    
    @Test
    public void testContainsIngredient() {
        Pantry pantry = new Pantry("user123");
        Ingredient salt = new Ingredient(1, "Salt", "Spices", 1.0, "tsp");
        
        pantry.addIngredient(salt);
        
        assertTrue(pantry.containsIngredient("Salt"));
        assertFalse(pantry.containsIngredient("Pepper"));
        
        // Case insensitive check
        assertTrue(pantry.containsIngredient("salt"));
    }
    
    @Test
    public void testHasEnoughOf() {
        Pantry pantry = new Pantry("user123");
        Ingredient pantryMilk = new Ingredient(1, "Milk", "Dairy", 2.0, "cups");
        pantry.addIngredient(pantryMilk);
        
        // Test with enough quantity
        Ingredient recipe1Milk = new Ingredient(1, "Milk", "Dairy", 1.0, "cups");
        assertTrue(pantry.hasEnoughOf(recipe1Milk));
        
        // Test with exact quantity
        Ingredient recipe2Milk = new Ingredient(1, "Milk", "Dairy", 2.0, "cups");
        assertTrue(pantry.hasEnoughOf(recipe2Milk));
        
        // Test with not enough quantity
        Ingredient recipe3Milk = new Ingredient(1, "Milk", "Dairy", 3.0, "cups");
        assertFalse(pantry.hasEnoughOf(recipe3Milk));
        
        // Test with different unit (should return true as we're not doing unit conversion)
        Ingredient recipe4Milk = new Ingredient(1, "Milk", "Dairy", 500.0, "ml");
        assertTrue(pantry.hasEnoughOf(recipe4Milk));
    }
    
    @Test
    public void testCompareWithRecipe() {
        Pantry pantry = new Pantry("user123");
        pantry.addIngredient(new Ingredient(1, "Flour", "Baking", 500.0, "g"));
        pantry.addIngredient(new Ingredient(2, "Sugar", "Baking", 200.0, "g"));
        pantry.addIngredient(new Ingredient(3, "Butter", "Dairy", 100.0, "g"));
        
        Recipe recipe = new Recipe();
        recipe.addIngredient(new Ingredient(1, "Flour", "Baking", 250.0, "g"));
        recipe.addIngredient(new Ingredient(2, "Sugar", "Baking", 150.0, "g"));
        recipe.addIngredient(new Ingredient(3, "Butter", "Dairy", 100.0, "g"));
        recipe.addIngredient(new Ingredient(4, "Eggs", "Refrigerated", 2.0, ""));
        
        Map<String, Object> result = pantry.compareWithRecipe(recipe);
        
        assertEquals(3, result.get("availableCount"));
        assertEquals(4, result.get("totalCount"));
        
        List<Ingredient> missingIngredients = (List<Ingredient>) result.get("missingIngredients");
        assertEquals(1, missingIngredients.size());
        assertEquals("Eggs", missingIngredients.get(0).getName());
    }
}