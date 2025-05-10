package main.edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import main.edu.brown.cs.student.main.server.model.Ingredient;
import main.edu.brown.cs.student.main.server.model.Recipe;
import main.edu.brown.cs.student.main.server.model.User;
import main.edu.brown.cs.student.main.server.service.SpoonacularService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeDetailHandler implements Route {
  private final SpoonacularService spoonacularService;
  private final JsonAdapter<Map<String, Object>> adapter;
  private final Map<String, User> users;

  public RecipeDetailHandler(SpoonacularService spoonacularService, Map<String, User> users) {
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
      // Extract recipe ID from request path
      String idParam = request.params(":id");
      if (idParam == null) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("message", "Recipe ID is required");
        response.status(400);
        return adapter.toJson(responseMap);
      }

      int recipeId = Integer.parseInt(idParam);

      // Get detailed recipe information
      Recipe recipe = spoonacularService.getRecipeById(recipeId);

      // Check for user allergens
      String userId = request.queryParams("userId");
      if (userId != null && users.containsKey(userId)) {
        User user = users.get(userId);
        List<String> allergies = user.getAllergies();

        // Check recipe ingredients for allergens
        if (!allergies.isEmpty()) {
          spoonacularService.checkRecipeForAllergens(recipe, allergies);
        }

        // Check pantry for available ingredients
        Map<String, Object> pantryComparison = user.getPantry().compareWithRecipe(recipe);
        recipe.setAvailableIngredients((int) pantryComparison.get("availableCount"));
        recipe.setTotalIngredients((int) pantryComparison.get("totalCount"));
        recipe.setMissingIngredients((List<Ingredient>) pantryComparison.get("missingIngredients"));
      }

      responseMap.put("result", "success");
      responseMap.put("recipe", recipe);

    } catch (NumberFormatException e) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("message", "Invalid recipe ID format");
      response.status(400);
    } catch (IllegalArgumentException e) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("message", e.getMessage());
      response.status(400);
    } catch (Exception e) {
      responseMap.put("result", "error_datasource");
      responseMap.put("message", "Failed to fetch recipe: " + e.getMessage());
      response.status(500);
    }

    return adapter.toJson(responseMap);
  }
}
