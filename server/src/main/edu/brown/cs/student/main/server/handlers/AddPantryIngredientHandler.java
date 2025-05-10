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
import java.util.Random;

public class AddPantryIngredientHandler implements Route {
  private final JsonAdapter<Map<String, Object>> adapter;
  private final Map<String, User> users;

  public AddPantryIngredientHandler(Map<String, User> users) {
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

      // Extract userId and ingredient details
      String userId = (String) requestMap.get("userId");
      String name = (String) requestMap.get("name");

      if (userId == null || name == null) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("message", "User ID and ingredient name are required");
        response.status(400);
        return adapter.toJson(responseMap);
      }

      // Create ingredient from request body
      Ingredient ingredient = new Ingredient();
      ingredient.setId(new Random().nextInt(1000000)); // Generate a random ID for the ingredient
      ingredient.setName(name);

      if (requestMap.containsKey("aisle") && requestMap.get("aisle") != null) {
        ingredient.setAisle((String) requestMap.get("aisle"));
      } else {
        ingredient.setAisle("Other");
      }

      if (requestMap.containsKey("amount") && requestMap.get("amount") != null) {
        ingredient.setAmount((Double) requestMap.get("amount"));
      } else {
        ingredient.setAmount(1.0);
      }

      if (requestMap.containsKey("unit") && requestMap.get("unit") != null) {
        ingredient.setUnit((String) requestMap.get("unit"));
      } else {
        ingredient.setUnit("");
      }

      ingredient.setOriginalString(ingredient.getAmount() + " " +
          ingredient.getUnit() + " " +
          ingredient.getName());

      // Check if user exists
      if (!users.containsKey(userId)) {
        // Create a new user if not exists
        users.put(userId, new User(userId, "", ""));
      }

      // Add ingredient to pantry
      User user = users.get(userId);
      user.getPantry().addIngredient(ingredient);

      responseMap.put("result", "success");
      responseMap.put("message", "Ingredient added to pantry");
      responseMap.put("pantry", user.getPantry());

    } catch (Exception e) {
      responseMap.put("result", "error_processing");
      responseMap.put("message", "Failed to add ingredient to pantry: " + e.getMessage());
      response.status(500);
    }

    return adapter.toJson(responseMap);
  }
}
