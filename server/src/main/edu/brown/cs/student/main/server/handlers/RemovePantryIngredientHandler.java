package main.edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import main.edu.brown.cs.student.main.server.model.Ingredient;
import main.edu.brown.cs.student.main.server.model.User;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemovePantryIngredientHandler implements Route {
  private final JsonAdapter<Map<String, Object>> adapter;
  private final Map<String, User> users;

  public RemovePantryIngredientHandler(Map<String, User> users) {
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

      // Extract userId and ingredientName
      String userId = (String) requestMap.get("userId");
      String ingredientName = (String) requestMap.get("ingredientName");

      if (userId == null || ingredientName == null) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("message", "User ID and ingredient name are required");
        response.status(400);
        return adapter.toJson(responseMap);
      }

      // Check if user exists
      if (!users.containsKey(userId)) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("message", "User not found");
        response.status(404);
        return adapter.toJson(responseMap);
      }

      // Get the user
      User user = users.get(userId);

      // Find and remove the ingredient from the pantry
      List<Ingredient> allIngredients = user.getPantry().getAllIngredients();
      Ingredient targetIngredient = null;

      for (Ingredient ingredient : allIngredients) {
        if (ingredient.getName().equalsIgnoreCase(ingredientName)) {
          targetIngredient = ingredient;
          break;
        }
      }

      if (targetIngredient == null) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("message", "Ingredient not found in pantry");
        response.status(404);
        return adapter.toJson(responseMap);
      }

      // Remove from pantry
      user.getPantry().removeIngredient(targetIngredient);

      responseMap.put("result", "success");
      responseMap.put("message", "Ingredient removed from pantry");
      responseMap.put("ingredient", ingredientName);

    } catch (Exception e) {
      responseMap.put("result", "error_processing");
      responseMap.put("message", "Failed to remove ingredient from pantry: " + e.getMessage());
      response.status(500);
    }

    return adapter.toJson(responseMap);
  }
}