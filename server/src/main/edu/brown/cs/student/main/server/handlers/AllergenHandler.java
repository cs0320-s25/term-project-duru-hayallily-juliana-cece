package main.edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import main.edu.brown.cs.student.main.server.model.User;
import main.edu.brown.cs.student.main.server.service.SpoonacularService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.*;

public class AllergenHandler implements Route {
  private final SpoonacularService spoonacularService;
  private final JsonAdapter<Map<String, Object>> adapter;
  private final Map<String, User> users;

  // Supported allergens list
  private final List<String> supportedAllergens = Arrays.asList(
      "dairy", "egg", "gluten", "grain", "peanut", "seafood",
      "sesame", "shellfish", "soy", "sulfite", "tree nut", "wheat"
  );

  public AllergenHandler(SpoonacularService spoonacularService, Map<String, User> users) {
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
      // Determine the endpoint based on request path
      String path = request.pathInfo();

      if (path.endsWith("/allergens")) {
        // Get all supported allergens
        responseMap.put("result", "success");
        responseMap.put("allergens", supportedAllergens);
      } else {
        // Extract user ID
        String userId = request.params(":userId");
        if (userId == null) {
          responseMap.put("result", "error_bad_request");
          responseMap.put("message", "User ID is required");
          response.status(400);
          return adapter.toJson(responseMap);
        }

        // Check if user exists
        if (!users.containsKey(userId)) {
          // Create a new user if not exists
          users.put(userId, new User(userId, "", ""));
        }

        // Get user
        User user = users.get(userId);

        // Get user's allergens
        List<String> allergens = user.getAllergies();

        responseMap.put("result", "success");
        responseMap.put("allergens", allergens);
      }
    } catch (IllegalArgumentException e) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("message", e.getMessage());
      response.status(400);
    } catch (Exception e) {
      responseMap.put("result", "error_datasource");
      responseMap.put("message", "Failed to process allergen request: " + e.getMessage());
      response.status(500);
    }

    return adapter.toJson(responseMap);
  }
}