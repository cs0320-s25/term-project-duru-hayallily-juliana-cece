package main.edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import main.edu.brown.cs.student.main.server.model.GroceryList;
import main.edu.brown.cs.student.main.server.model.User;
import main.edu.brown.cs.student.main.server.service.SpoonacularService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroceryListHandler implements Route {
  private final SpoonacularService spoonacularService;
  private final JsonAdapter<Map<String, Object>> adapter;
  private final Map<String, User> users;

  public GroceryListHandler(SpoonacularService spoonacularService, Map<String, User> users) {
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
      // Extract user ID from request path
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

      // Get the user's grocery list
      User user = users.get(userId);
      GroceryList groceryList = user.getGroceryList();

      responseMap.put("result", "success");
      responseMap.put("groceryList", groceryList);

    } catch (IllegalArgumentException e) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("message", e.getMessage());
      response.status(400);
    } catch (Exception e) {
      responseMap.put("result", "error_datasource");
      responseMap.put("message", "Failed to fetch grocery list: " + e.getMessage());
      response.status(500);
    }

    return adapter.toJson(responseMap);
  }
}