package main.edu.brown.cs.student.main.server;

import main.edu.brown.cs.student.main.server.config.AppConfig;
import main.edu.brown.cs.student.main.server.handlers.AddAllergenHandler;
import main.edu.brown.cs.student.main.server.handlers.AddGroceryIngredientHandler;
import main.edu.brown.cs.student.main.server.handlers.AddPantryIngredientHandler;
import main.edu.brown.cs.student.main.server.handlers.AddRecipeToGroceryHandler;
import main.edu.brown.cs.student.main.server.handlers.AllergenHandler;
// import main.edu.brown.cs.student.main.server.handlers.CheckGroceryItemHandler;
import main.edu.brown.cs.student.main.server.handlers.ClearGroceryListHandler; 
import main.edu.brown.cs.student.main.server.handlers.GroceryListHandler;
import main.edu.brown.cs.student.main.server.handlers.PantryCompareHandler;
import main.edu.brown.cs.student.main.server.handlers.PantryHandler;
import main.edu.brown.cs.student.main.server.handlers.RecipeDetailHandler;
import main.edu.brown.cs.student.main.server.handlers.RecipeHandler;
import main.edu.brown.cs.student.main.server.handlers.RemoveAllergenHandler;
import main.edu.brown.cs.student.main.server.model.User;
import main.edu.brown.cs.student.main.server.service.SpoonacularService;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Server {
  public static void main(String[] args) {
    // Initialize configuration
    AppConfig config = AppConfig.getInstance();

    // Set up port
    port(config.getServerPort());

    // Set up CORS
    after((request, response) -> {
      response.header("Access-Control-Allow-Origin", "*");
      response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
      response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Content-Length, Accept, Origin");
    });

    // Options request handling for CORS
    options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
      }

      return "OK";
    });

    // Initialize shared user store (in-memory for demo purposes)
    Map<String, User> users = new HashMap<>();

    // Initialize services
    SpoonacularService spoonacularService = new SpoonacularService(config.getSpoonacularApiKey());

    // Set up API routes

    // Recipe routes
    get("/api/recipes/search", new RecipeHandler(spoonacularService, users));
    get("/api/recipes/:id", new RecipeDetailHandler(spoonacularService, users));

    // Grocery list routes
    get("/api/users/:userId/grocery", new GroceryListHandler(spoonacularService, users));
    post("/api/grocery/add-recipe", new AddRecipeToGroceryHandler(spoonacularService, users));
    post("/api/grocery/add-ingredient", new AddGroceryIngredientHandler(users));
    post("/api/grocery/clear", new ClearGroceryListHandler(users));
    // post("/api/grocery/check-item", new CheckGroceryItemHandler(users));


    // Pantry routes
    get("/api/users/:userId/pantry", new PantryHandler(spoonacularService, users));
    post("/api/pantry/add-ingredient", new AddPantryIngredientHandler(users));
    get("/api/pantry/compare", new PantryCompareHandler(spoonacularService, users));

    // Allergen routes
    get("/api/allergens", new AllergenHandler(spoonacularService, users));
    get("/api/users/:userId/allergens", new AllergenHandler(spoonacularService, users));
    post("/api/allergens/add", new AddAllergenHandler(users));
    delete("/api/users/:userId/allergens/:allergen", new RemoveAllergenHandler(users));

    // Health check route
    get("/api/health", (req, res) -> {
      res.type("application/json");
      return "{\"status\":\"UP\"}";
    });

    // Not found handler
    notFound((req, res) -> {
      res.type("application/json");
      return "{\"result\":\"error\",\"message\":\"Route not found\"}";
    });

    // Exception handler
    exception(Exception.class, (e, req, res) -> {
      res.status(500);
      res.type("application/json");
      res.body("{\"result\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
    });

    System.out.println("Server started on port " + port());
  }
}