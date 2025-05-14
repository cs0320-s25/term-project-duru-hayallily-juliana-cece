package main.edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import main.edu.brown.cs.student.main.server.model.User;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ClearPantryHandler implements Route {
  private final JsonAdapter<Map<String, Object>> adapter;
  private final Map<String, User> users;

  public ClearPantryHandler(Map<String, User> users) {
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

      // Extract userId
      String userId = (String) requestMap.get("userId");

      if (userId == null) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("message", "User ID is required");
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

      // Clear the pantry
      user.getPantry().getIngredientsByCategory().clear();

      responseMap.put("result", "success");
      responseMap.put("message", "Pantry cleared successfully");

    } catch (Exception e) {
      responseMap.put("result", "error_processing");
      responseMap.put("message", "Failed to clear pantry: " + e.getMessage());
      response.status(500);
    }

    return adapter.toJson(responseMap);
  }
}
