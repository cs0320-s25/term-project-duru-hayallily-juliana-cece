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
import java.util.Map;

public class AddGroceryIngredientHandler implements Route {
  private final JsonAdapter<Map<String, Object>> adapter;
  private final Map<String, User> users;

  public AddGroceryIngredientHandler(Map<String, User> users) {
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
        // Create a new user if not exists
        users.put(userId, new User(userId, "", ""));
      }

      // Get the user
      User user = users.get(userId);

      // Create a new ingredient from the string
      Ingredient ingredient = new Ingredient();
      ingredient.setName(ingredientName);
      ingredient.setOriginalString(ingredientName);
      ingredient.setAisle("Other"); // Default aisle for manually added items
      ingredient.setAmount(1.0); // Default amount
      ingredient.setUnit(""); // No unit for string-based items

      // Add to grocery list
      user.getGroceryList().addIngredient(ingredient);

      responseMap.put("result", "success");
      responseMap.put("message", "Ingredient added to grocery list");
      responseMap.put("ingredient", ingredientName);

    } catch (Exception e) {
      responseMap.put("result", "error_processing");
      responseMap.put("message", "Failed to add ingredient to grocery list: " + e.getMessage());
      response.status(500);
    }

    return adapter.toJson(responseMap);
  }
}