package main.edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import main.edu.brown.cs.student.main.server.model.User;
import main.edu.brown.cs.student.main.server.model.Ingredient;
import main.edu.brown.cs.student.main.server.service.SpoonacularService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.*;

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
      // Get userId from path parameter
      String userId = request.params(":userId");

      if (userId == null) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("message", "User ID is required");
        response.status(400);
        return adapter.toJson(responseMap);
      }

      // Check if user exists
      if (!users.containsKey(userId)) {
        users.put(userId, new User(userId, "", ""));
      }

      User user = users.get(userId);

      // Get the grocery list items (using getAllIngredients method from GroceryList)
      List<Ingredient> groceryIngredients = user.getGroceryList().getAllIngredients();
      List<Ingredient> checkedItems = user.getGroceryList().getCheckedItems();

      // Convert to object format that includes checked status
      List<Map<String, Object>> groceryItemsWithStatus = new ArrayList<>();
      for (Ingredient ingredient : groceryIngredients) {
        Map<String, Object> itemMap = new HashMap<>();

        String itemString = ingredient.getOriginalString() != null
            ? ingredient.getOriginalString()
            : (ingredient.getAmount() != 0 ? ingredient.getAmount() + " " : "") +
                (ingredient.getUnit() != null ? ingredient.getUnit() + " " : "") +
                ingredient.getName();

        itemMap.put("name", itemString);

        // Check if this item is in the checked list
        boolean isChecked = checkedItems.stream()
            .anyMatch(checked -> checked.getName().equalsIgnoreCase(ingredient.getName()));
        itemMap.put("checked", isChecked);

        groceryItemsWithStatus.add(itemMap);
      }

      responseMap.put("result", "success");
      responseMap.put("groceries", groceryItemsWithStatus);
      responseMap.put("count", groceryItemsWithStatus.size());

    } catch (Exception e) {
      responseMap.put("result", "error_processing");
      responseMap.put("message", "Failed to get grocery list: " + e.getMessage());
      response.status(500);
    }

    return adapter.toJson(responseMap);
  }
}