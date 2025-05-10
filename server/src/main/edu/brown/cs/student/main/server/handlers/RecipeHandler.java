package main.edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import main.edu.brown.cs.student.main.server.model.Recipe;
import main.edu.brown.cs.student.main.server.model.User;
import main.edu.brown.cs.student.main.server.service.SpoonacularService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.*;

public class RecipeHandler implements Route {
  private final SpoonacularService spoonacularService;
  private final JsonAdapter<Map<String, Object>> adapter;
  private final Map<String, User> users;

  public RecipeHandler(SpoonacularService spoonacularService, Map<String, User> users) {
    this.spoonacularService = spoonacularService;
    this.users = users;

    Moshi moshi = new Moshi.Builder().build();
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    this.adapter = moshi.adapter(type);
  }

  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    response.type("application/json");

    try {
      // Extract query parameters
      Map<String, String> searchParams = new HashMap<>();

      // Extract parameters from the query
      if (request.queryParams("query") != null) {
        searchParams.put("query", request.queryParams("query"));
      }
      if (request.queryParams("cuisine") != null) {
        searchParams.put("cuisine", request.queryParams("cuisine"));
      }
      if (request.queryParams("excludeCuisine") != null) {
        searchParams.put("excludeCuisine", request.queryParams("excludeCuisine"));
      }
      if (request.queryParams("diet") != null) {
        searchParams.put("diet", request.queryParams("diet"));
      }
      if (request.queryParams("intolerances") != null) {
        searchParams.put("intolerances", request.queryParams("intolerances"));
      }
      if (request.queryParams("equipment") != null) {
        searchParams.put("equipment", request.queryParams("equipment"));
      }
      if (request.queryParams("includeIngredients") != null) {
        searchParams.put("includeIngredients", request.queryParams("includeIngredients"));
      }
      if (request.queryParams("excludeIngredients") != null) {
        searchParams.put("excludeIngredients", request.queryParams("excludeIngredients"));
      }
      if (request.queryParams("type") != null) {
        searchParams.put("type", request.queryParams("type"));
      }

      // Add additional parameters from Spoonacular API
      searchParams.put("instructionsRequired", "true");
      searchParams.put("fillIngredients", "true");
      searchParams.put("addRecipeInformation", "true");

      // Check for user allergens
      String userId = request.queryParams("userId");
      if (userId != null && users.containsKey(userId)) {
        User user = users.get(userId);
        List<String> allergies = user.getAllergies();

        if (!allergies.isEmpty()) {
          String intolerancesParam = searchParams.getOrDefault("intolerances", "");
          if (!intolerancesParam.isEmpty()) {
            intolerancesParam += ",";
          }
          intolerancesParam += String.join(",", allergies);
          searchParams.put("intolerances", intolerancesParam);
        }

        // Add diet preferences if available
        List<String> diets = user.getDiets();
        if (!diets.isEmpty()) {
          String dietParam = searchParams.getOrDefault("diet", "");
          if (!dietParam.isEmpty()) {
            dietParam += ",";
          }
          dietParam += String.join(",", diets);
          searchParams.put("diet", dietParam);
        }
      }

      // Search for recipes
      List<Recipe> recipes = spoonacularService.searchRecipes(searchParams);

      // If a user ID was provided, check pantry for each recipe
      if (userId != null && users.containsKey(userId)) {
        User user = users.get(userId);
        for (Recipe recipe : recipes) {
          Map<String, Object> pantryComparison = user.getPantry().compareWithRecipe(recipe);
          recipe.setAvailableIngredients((int) pantryComparison.get("availableCount"));
          recipe.setTotalIngredients((int) pantryComparison.get("totalCount"));
          recipe.setMissingIngredients((List<main.edu.brown.cs.student.main.server.model.Ingredient>) pantryComparison.get("missingIngredients"));
        }
      }

      responseMap.put("result", "success");
      responseMap.put("recipes", recipes);

    } catch (IllegalArgumentException e) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("message", e.getMessage());
      response.status(400);
    } catch (Exception e) {
      responseMap.put("result", "error_datasource");
      responseMap.put("message", "Failed to fetch recipes: " + e.getMessage());
      response.status(500);
    }

    return adapter.toJson(responseMap);
  }
}