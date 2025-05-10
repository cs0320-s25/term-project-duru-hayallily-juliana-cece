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
import java.util.HashMap;
import java.util.Map;

public class PantryCompareHandler implements Route {
  private final SpoonacularService spoonacularService;
  private final JsonAdapter<Map<String, Object>> adapter;
  private final Map<String, User> users;

  public PantryCompareHandler(SpoonacularService spoonacularService, Map<String, User> users) {
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
      // Extract user ID and recipe ID from request
      String userId = request.queryParams("userId");
      String recipeIdStr = request.queryParams("recipeId");

      if (userId == null || recipeIdStr == null) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("message", "User ID and recipe ID are required");
        response.status(400);
        return adapter.toJson(responseMap);
      }

      int recipeId = Integer.parseInt(recipeIdStr);

      // Check if user exists
      if (!users.containsKey(userId)) {
        responseMap.put("result", "error_not_found");
        responseMap.put("message", "User not found");
        response.status(404);
        return adapter.toJson(responseMap);
      }

      // Get recipe details
      Recipe recipe = spoonacularService.getRecipeById(recipeId);

      // Compare with pantry
      User user = users.get(userId);
      Map<String, Object> comparison = user.getPantry().compareWithRecipe(recipe);

      responseMap.put("result", "success");
      responseMap.put("recipe", recipe);
      responseMap.put("availableCount", comparison.get("availableCount"));
      responseMap.put("totalCount", comparison.get("totalCount"));
      responseMap.put("missingIngredients", comparison.get("missingIngredients"));

    } catch (NumberFormatException e) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("message", "Invalid recipe ID format");
      response.status(400);
    } catch (Exception e) {
      responseMap.put("result", "error_processing");
      responseMap.put("message", "Failed to compare pantry with recipe: " + e.getMessage());
      response.status(500);
    }

    return adapter.toJson(responseMap);
  }
}