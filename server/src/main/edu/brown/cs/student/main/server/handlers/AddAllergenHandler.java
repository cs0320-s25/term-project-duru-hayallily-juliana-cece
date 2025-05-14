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

public class AddAllergenHandler implements Route {
  private final JsonAdapter<Map<String, Object>> adapter;
  private final Map<String, User> users;

  // Supported allergens list
  private final List<String> supportedAllergens = Arrays.asList(
      "dairy", "egg", "gluten", "grain", "peanut", "seafood",
      "sesame", "shellfish", "soy", "sulfite", "tree nut", "wheat"
  );

  public AddAllergenHandler(Map<String, User> users) {
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

      // Extract userId and allergen
      String userId = (String) requestMap.get("userId");
      String allergen = (String) requestMap.get("allergen");

      if (userId == null || allergen == null) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("message", "User ID and allergen are required");
        response.status(400);
        return adapter.toJson(responseMap);
      }

      // Validate the allergen
      allergen = allergen.toLowerCase();
      if (!supportedAllergens.contains(allergen)) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("message", "Unsupported allergen: " + allergen);
        response.status(400);
        return adapter.toJson(responseMap);
      }

      // Check if user exists
      if (!users.containsKey(userId)) {
        // Create a new user if not exists
        users.put(userId, new User(userId, "", ""));
      }

      // Add the allergen
      User user = users.get(userId);
      user.addAllergy(allergen);

      responseMap.put("result", "success");
      responseMap.put("message", "Allergen added successfully");
      responseMap.put("allergens", user.getAllergies());

    } catch (Exception e) {
      responseMap.put("result", "error_processing");
      responseMap.put("message", "Failed to add allergen: " + e.getMessage());
      response.status(500);
    }

    return adapter.toJson(responseMap);
  }
}