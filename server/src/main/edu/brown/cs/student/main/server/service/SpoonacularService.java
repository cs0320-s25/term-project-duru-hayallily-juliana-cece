package main.edu.brown.cs.student.main.server.service;


import main.edu.brown.cs.student.main.server.model.*;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SpoonacularService {
  private static final String BASE_URL = "https://api.spoonacular.com";
  private static final String SEARCH_ENDPOINT = "/recipes/complexSearch";
  private static final String GET_RECIPE_ENDPOINT = "/recipes/{id}/information";

  private final String apiKey;
  private final HttpClient httpClient;
  private final Moshi moshi;
  private final JsonAdapter<Map<String, Object>> mapAdapter;
  private final JsonAdapter<List<Map<String, Object>>> listMapAdapter;

  public SpoonacularService(String apiKey) {
    this.apiKey = apiKey;
    this.httpClient = HttpClient.newHttpClient();
    this.moshi = new Moshi.Builder().build();
    Type mapType = Types.newParameterizedType(Map.class, String.class, Object.class);
    Type listMapType = Types.newParameterizedType(List.class, mapType);
    this.mapAdapter = moshi.adapter(mapType);
    this.listMapAdapter = moshi.adapter(listMapType);
  }

  /**
   * Search for recipes based on various parameters
   */
  public List<Recipe> searchRecipes(Map<String, String> searchParams) throws IOException, InterruptedException {
    // Build the query with API key
    StringBuilder queryBuilder = new StringBuilder(BASE_URL + SEARCH_ENDPOINT);
    queryBuilder.append("?apiKey=").append(apiKey);

    // Add additional parameters
    for (Map.Entry<String, String> param : searchParams.entrySet()) {
      if (param.getValue() != null && !param.getValue().trim().isEmpty()) {
        queryBuilder.append("&")
            .append(param.getKey())
            .append("=")
            .append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8));
      }
    }

    // Add fill ingredients and additional recipe info
    if (!searchParams.containsKey("fillIngredients")) {
      queryBuilder.append("&fillIngredients=true");
    }
    if (!searchParams.containsKey("addRecipeInformation")) {
      queryBuilder.append("&addRecipeInformation=true");
    }

    // Build the request
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(queryBuilder.toString()))
        .GET()
        .build();

    // Send the request and get the response
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    // Check if the request was successful
    if (response.statusCode() >= 200 && response.statusCode() < 300) {
      // Parse the response JSON
      Map<String, Object> jsonResponse = mapAdapter.fromJson(response.body());
      List<Map<String, Object>> resultsArray = (List<Map<String, Object>>) jsonResponse.get("results");

      // Convert JSON to Recipe objects
      List<Recipe> recipes = new ArrayList<>();
      for (Map<String, Object> recipeMap : resultsArray) {
        Recipe recipe = parseRecipeFromSearchResult(recipeMap);
        recipes.add(recipe);
      }

      return recipes;
    } else {
      throw new IOException("Error searching recipes: " + response.statusCode() + " " + response.body());
    }
  }

  /**
   * Get detailed information about a specific recipe
   */
  public Recipe getRecipeById(int id) throws IOException, InterruptedException {
    // Build the URL
    String url = BASE_URL + GET_RECIPE_ENDPOINT.replace("{id}", String.valueOf(id))
        + "?apiKey=" + apiKey
        + "&includeNutrition=false";

    // Build the request
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .GET()
        .build();

    // Send the request and get the response
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    // Check if the request was successful
    if (response.statusCode() >= 200 && response.statusCode() < 300) {
      // Parse the response JSON
      Map<String, Object> jsonRecipe = mapAdapter.fromJson(response.body());
      return parseFullRecipeDetails(jsonRecipe);
    } else {
      throw new IOException("Error getting recipe: " + response.statusCode() + " " + response.body());
    }
  }

  /**
   * Parse a recipe from search results
   */
  private Recipe parseRecipeFromSearchResult(Map<String, Object> jsonRecipe) {
    Recipe recipe = new Recipe();

    // Basic recipe info
    recipe.setId(((Double) jsonRecipe.get("id")).intValue());
    recipe.setTitle((String) jsonRecipe.get("title"));

    if (jsonRecipe.containsKey("image")) {
      recipe.setImage((String) jsonRecipe.get("image"));
    }

    if (jsonRecipe.containsKey("readyInMinutes")) {
      recipe.setReadyInMinutes(((Double) jsonRecipe.get("readyInMinutes")).intValue());
    }

    if (jsonRecipe.containsKey("servings")) {
      recipe.setServings(((Double) jsonRecipe.get("servings")).intValue());
    }

    // Diet info
    if (jsonRecipe.containsKey("vegetarian")) {
      recipe.setVegetarian((Boolean) jsonRecipe.get("vegetarian"));
    }
    if (jsonRecipe.containsKey("vegan")) {
      recipe.setVegan((Boolean) jsonRecipe.get("vegan"));
    }
    if (jsonRecipe.containsKey("glutenFree")) {
      recipe.setGlutenFree((Boolean) jsonRecipe.get("glutenFree"));
    }
    if (jsonRecipe.containsKey("dairyFree")) {
      recipe.setDairyFree((Boolean) jsonRecipe.get("dairyFree"));
    }

    // Diets
    if (jsonRecipe.containsKey("diets") && jsonRecipe.get("diets") instanceof List) {
      List<String> diets = new ArrayList<>();
      for (Object diet : (List<?>) jsonRecipe.get("diets")) {
        diets.add((String) diet);
      }
      recipe.setDiets(diets);
    }

    // Parse ingredients if available
    if (jsonRecipe.containsKey("missedIngredients") && jsonRecipe.get("missedIngredients") instanceof List) {
      parseIngredients(recipe, (List<Map<String, Object>>) jsonRecipe.get("missedIngredients"));
    }

    if (jsonRecipe.containsKey("usedIngredients") && jsonRecipe.get("usedIngredients") instanceof List) {
      parseIngredients(recipe, (List<Map<String, Object>>) jsonRecipe.get("usedIngredients"));
    }

    return recipe;
  }

  /**
   * Parse a full recipe detail from the API
   */
  private Recipe parseFullRecipeDetails(Map<String, Object> jsonRecipe) {
    Recipe recipe = new Recipe();

    // Basic recipe info
    recipe.setId(((Double) jsonRecipe.get("id")).intValue());
    recipe.setTitle((String) jsonRecipe.get("title"));

    if (jsonRecipe.containsKey("image")) {
      recipe.setImage((String) jsonRecipe.get("image"));
    }

    if (jsonRecipe.containsKey("readyInMinutes")) {
      recipe.setReadyInMinutes(((Double) jsonRecipe.get("readyInMinutes")).intValue());
    }

    if (jsonRecipe.containsKey("servings")) {
      recipe.setServings(((Double) jsonRecipe.get("servings")).intValue());
    }

    if (jsonRecipe.containsKey("sourceUrl")) {
      recipe.setSourceUrl((String) jsonRecipe.get("sourceUrl"));
    }

    // Diet info
    if (jsonRecipe.containsKey("vegetarian")) {
      recipe.setVegetarian((Boolean) jsonRecipe.get("vegetarian"));
    }
    if (jsonRecipe.containsKey("vegan")) {
      recipe.setVegan((Boolean) jsonRecipe.get("vegan"));
    }
    if (jsonRecipe.containsKey("glutenFree")) {
      recipe.setGlutenFree((Boolean) jsonRecipe.get("glutenFree"));
    }
    if (jsonRecipe.containsKey("dairyFree")) {
      recipe.setDairyFree((Boolean) jsonRecipe.get("dairyFree"));
    }

    // Diets
    if (jsonRecipe.containsKey("diets") && jsonRecipe.get("diets") instanceof List) {
      List<String> diets = new ArrayList<>();
      for (Object diet : (List<?>) jsonRecipe.get("diets")) {
        diets.add((String) diet);
      }
      recipe.setDiets(diets);
    }

    // Parse ingredients
    if (jsonRecipe.containsKey("extendedIngredients") && jsonRecipe.get("extendedIngredients") instanceof List) {
      List<Map<String, Object>> ingredients = (List<Map<String, Object>>) jsonRecipe.get("extendedIngredients");
      for (Map<String, Object> ingredientMap : ingredients) {
        Ingredient ingredient = new Ingredient();

        ingredient.setId(((Double) ingredientMap.get("id")).intValue());
        ingredient.setName((String) ingredientMap.get("name"));

        if (ingredientMap.containsKey("aisle") && ingredientMap.get("aisle") != null) {
          ingredient.setAisle((String) ingredientMap.get("aisle"));
        }

        if (ingredientMap.containsKey("amount") && ingredientMap.get("amount") != null) {
          ingredient.setAmount((Double) ingredientMap.get("amount"));
        }

        if (ingredientMap.containsKey("unit") && ingredientMap.get("unit") != null) {
          ingredient.setUnit((String) ingredientMap.get("unit"));
        }

        if (ingredientMap.containsKey("original") && ingredientMap.get("original") != null) {
          ingredient.setOriginalString((String) ingredientMap.get("original"));
        } else {
          ingredient.setOriginalString(ingredient.getAmount() + " " +
              ingredient.getUnit() + " " +
              ingredient.getName());
        }

        recipe.addIngredient(ingredient);
      }
    }

    // Parse instructions
    if (jsonRecipe.containsKey("analyzedInstructions") && jsonRecipe.get("analyzedInstructions") instanceof List) {
      List<Map<String, Object>> instructions = (List<Map<String, Object>>) jsonRecipe.get("analyzedInstructions");
      List<String> stepsList = new ArrayList<>();

      for (Map<String, Object> instructionObj : instructions) {
        if (instructionObj.containsKey("steps") && instructionObj.get("steps") instanceof List) {
          List<Map<String, Object>> steps = (List<Map<String, Object>>) instructionObj.get("steps");
          for (Map<String, Object> step : steps) {
            if (step.containsKey("step") && step.get("step") != null) {
              stepsList.add((String) step.get("step"));
            }
          }
        }
      }

      recipe.setInstructions(stepsList);
    }

    return recipe;
  }

  /**
   * Helper method to parse ingredients from search results
   */
  private void parseIngredients(Recipe recipe, List<Map<String, Object>> ingredientsArray) {
    for (Map<String, Object> ingredientMap : ingredientsArray) {
      Ingredient ingredient = new Ingredient();

      ingredient.setId(((Double) ingredientMap.get("id")).intValue());
      ingredient.setName((String) ingredientMap.get("name"));

      if (ingredientMap.containsKey("aisle") && ingredientMap.get("aisle") != null) {
        ingredient.setAisle((String) ingredientMap.get("aisle"));
      }

      if (ingredientMap.containsKey("amount") && ingredientMap.get("amount") != null) {
        ingredient.setAmount((Double) ingredientMap.get("amount"));
      }

      if (ingredientMap.containsKey("unit") && ingredientMap.get("unit") != null) {
        ingredient.setUnit((String) ingredientMap.get("unit"));
      }

      if (ingredientMap.containsKey("original") && ingredientMap.get("original") != null) {
        ingredient.setOriginalString((String) ingredientMap.get("original"));
      } else {
        ingredient.setOriginalString(ingredient.getAmount() + " " +
            ingredient.getUnit() + " " +
            ingredient.getName());
      }

      recipe.addIngredient(ingredient);
    }
  }

  /**
   * Check if an ingredient contains any of the user's allergens
   */
  public boolean checkIngredientForAllergens(Ingredient ingredient, List<String> allergens) {
    if (allergens == null || allergens.isEmpty()) {
      return false;
    }

    // Simple check: see if the ingredient name contains any allergen name
    String ingredientName = ingredient.getName().toLowerCase();
    for (String allergen : allergens) {
      if (ingredientName.contains(allergen.toLowerCase())) {
        ingredient.setContainsAllergen(true);
        ingredient.setPossibleAllergens(new String[]{allergen});
        return true;
      }
    }

    // In a real implementation, you would use a more sophisticated check,
    // possibly calling another API endpoint to check for allergens

    return false;
  }

  /**
   * Check all ingredients in a recipe for allergens
   */
  public void checkRecipeForAllergens(Recipe recipe, List<String> allergens) {
    if (allergens == null || allergens.isEmpty()) {
      return;
    }

    for (Ingredient ingredient : recipe.getIngredients()) {
      checkIngredientForAllergens(ingredient, allergens);
    }
  }
}