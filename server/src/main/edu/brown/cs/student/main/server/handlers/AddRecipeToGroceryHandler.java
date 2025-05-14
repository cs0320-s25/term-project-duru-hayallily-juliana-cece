package main.edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import main.edu.brown.cs.student.main.server.model.Ingredient;
import main.edu.brown.cs.student.main.server.model.Recipe;
import main.edu.brown.cs.student.main.server.model.User;
import main.edu.brown.cs.student.main.server.service.SpoonacularService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddRecipeToGroceryHandler implements Route {
  private final SpoonacularService spoonacularService;
  private final JsonAdapter<Map<String, Object>> adapter;
  private final Map<String, User> users;

  public AddRecipeToGroceryHandler(SpoonacularService spoonacularService, Map<String, User> users) {
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
      // Parse request body
      String body = request.body();
      Map<String, Object> requestMap = adapter.fromJson(body);

      if (requestMap == null) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("message", "Invalid JSON body");
        response.status(400);
        return adapter.toJson(responseMap);
      }

      // Extract userId and recipeId
      String userId = (String) requestMap.get("userId");
      Double recipeIdDouble = (Double) requestMap.get("recipeId");

      if (userId == null || recipeIdDouble == null) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("message", "User ID and recipe ID are required");
        response.status(400);
        return adapter.toJson(responseMap);
      }

      int recipeId = recipeIdDouble.intValue();

      // Check if user exists
      if (!users.containsKey(userId)) {
        // Create a new user if not exists
        users.put(userId, new User(userId, "", ""));
      }

      // Get the user
      User user = users.get(userId);

      // Get recipe details
      Recipe recipe = spoonacularService.getRecipeById(recipeId);

      // Compare recipe with pantry to get missing ingredients
      Map<String, Object> pantryComparison = user.getPantry().compareWithRecipe(recipe);
      List<Ingredient> missingIngredients = (List<Ingredient>) pantryComparison.get("missingIngredients");

      // Add missing ingredients to grocery list
      user.getGroceryList().addIngredients(missingIngredients);

      responseMap.put("result", "success");
      responseMap.put("message", "Recipe ingredients added to grocery list");
      responseMap.put("missingIngredientsCount", missingIngredients.size());
      responseMap.put("recipeTitle", recipe.getTitle());

    } catch (Exception e) {
      responseMap.put("result", "error_processing");
      responseMap.put("message", "Failed to add recipe to grocery list: " + e.getMessage());
      response.status(500);
    }

    return adapter.toJson(responseMap);
  }
}