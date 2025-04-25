package main.edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import main.edu.brown.cs.student.main.server.model.User;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.*;

public class RemoveAllergenHandler implements Route {
  private final JsonAdapter<Map<String, Object>> adapter;
  private final Map<String, User> users;

  public RemoveAllergenHandler(Map<String, User> users) {
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
      // Extract user ID and allergen from request path
      String userId = request.params(":userId");
      String allergen = request.params(":allergen");

      if (userId == null || allergen == null) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("message", "User ID and allergen are required");
        response.status(400);
        return adapter.toJson(responseMap);
      }

      // Check if user exists
      if (!users.containsKey(userId)) {
        responseMap.put("result", "error_not_found");
        responseMap.put("message", "User not found");
        response.status(404);
        return adapter.toJson(responseMap);
      }

      // Remove the allergen
      User user = users.get(userId);
      user.removeAllergy(allergen.toLowerCase());

      responseMap.put("result", "success");
      responseMap.put("message", "Allergen removed successfully");
      responseMap.put("allergens", user.getAllergies());

    } catch (Exception e) {
      responseMap.put("result", "error_processing");
      responseMap.put("message", "Failed to remove allergen: " + e.getMessage());
      response.status(500);
    }

    return adapter.toJson(responseMap);
  }
}