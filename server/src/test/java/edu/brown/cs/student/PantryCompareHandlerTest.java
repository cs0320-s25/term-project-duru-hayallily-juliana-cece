package edu.brown.cs.student;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import main.edu.brown.cs.student.main.server.handlers.PantryCompareHandler;
import main.edu.brown.cs.student.main.server.model.Ingredient;
import main.edu.brown.cs.student.main.server.model.User;
import main.edu.brown.cs.student.main.server.service.MockSpoonacularService;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spark.Request;
import spark.Response;

import static org.mockito.Mockito.*;

public class PantryCompareHandlerTest {

  private PantryCompareHandler handler;
  private MockSpoonacularService mockService;
  private Map<String, User> users;
  private Request mockRequest;
  private Response mockResponse;
  private JsonAdapter<Map<String, Object>> adapter;

  @Before
  public void setUp() {
    // Initialize mock service and users map
    mockService = new MockSpoonacularService();
    users = new HashMap<>();

    // Add a test user with pantry items
    User testUser = new User("test-user", "Test User", "test@example.com");

    // Add pantry items that match some of the ingredients in recipe #1
    testUser.getPantry().addIngredient(new Ingredient(1001, "Butter", "Dairy", 1.0, "cup"));
    testUser.getPantry().addIngredient(new Ingredient(1002, "Sugar", "Baking", 1.0, "cup"));
    testUser.getPantry().addIngredient(new Ingredient(1006, "All-Purpose Flour", "Baking", 2.5, "cups"));

    users.put(testUser.getId(), testUser);

    // Create the handler
    handler = new PantryCompareHandler(mockService, users);

    // Mock Spark request and response
    mockRequest = mock(Request.class);
    mockResponse = mock(Response.class);

    // Initialize Moshi adapter for parsing responses
    Moshi moshi = new Moshi.Builder().build();
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    adapter = moshi.adapter(type);
  }

  @Test
  public void testHandleCompareWithRecipe() throws Exception {
    // Set up mock request with userId and recipeId
    when(mockRequest.queryParams("userId")).thenReturn("test-user");
    when(mockRequest.queryParams("recipeId")).thenReturn("1"); // Chocolate Chip Cookies from mock data

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify successful response
    assertEquals("success", responseMap.get("result"));

    // Verify recipe in response
    Map<String, Object> recipe = (Map<String, Object>) responseMap.get("recipe");
    assertNotNull(recipe);
    assertEquals(1.0, (double) recipe.get("id"), 0.001);
    assertEquals("Chocolate Chip Cookies", recipe.get("title"));

    // Verify pantry comparison information
    assertEquals(3.0, (double) responseMap.get("availableCount"), 0.001);
    assertTrue((double) responseMap.get("totalCount") > 3.0);

    // Verify missing ingredients list
    List<Map<String, Object>> missingIngredients = (List<Map<String, Object>>) responseMap.get("missingIngredients");
    assertNotNull(missingIngredients);
    assertFalse(missingIngredients.isEmpty());
  }

  @Test
  public void testHandleMissingUserId() throws Exception {
    // Set up mock request with missing userId
    when(mockRequest.queryParams("userId")).thenReturn(null);
    when(mockRequest.queryParams("recipeId")).thenReturn("1");

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify error response
    assertEquals("error_bad_request", responseMap.get("result"));
    assertTrue(((String) responseMap.get("message")).contains("User ID"));
  }

  @Test
  public void testHandleMissingRecipeId() throws Exception {
    // Set up mock request with missing recipeId
    when(mockRequest.queryParams("userId")).thenReturn("test-user");
    when(mockRequest.queryParams("recipeId")).thenReturn(null);

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify error response
    assertEquals("error_bad_request", responseMap.get("result"));
    assertTrue(((String) responseMap.get("message")).contains("recipe ID"));
  }

  @Test
  public void testHandleInvalidRecipeId() throws Exception {
    // Set up mock request with invalid recipeId
    when(mockRequest.queryParams("userId")).thenReturn("test-user");
    when(mockRequest.queryParams("recipeId")).thenReturn("invalid");

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify error response
    assertEquals("error_bad_request", responseMap.get("result"));
    assertTrue(((String) responseMap.get("message")).contains("Invalid recipe ID format"));
  }

  @Test
  public void testHandleUserNotFound() throws Exception {
    // Set up mock request with non-existent userId
    when(mockRequest.queryParams("userId")).thenReturn("non-existent-user");
    when(mockRequest.queryParams("recipeId")).thenReturn("1");

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify error response
    assertEquals("error_not_found", responseMap.get("result"));
    assertTrue(((String) responseMap.get("message")).contains("User not found"));
  }

  @Test
  public void testHandleRecipeNotFound() throws Exception {
    // Set up mock request with non-existent recipeId
    when(mockRequest.queryParams("userId")).thenReturn("test-user");
    when(mockRequest.queryParams("recipeId")).thenReturn("999");

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify error response
    assertEquals("error_processing", responseMap.get("result"));
    assertTrue(((String) responseMap.get("message")).contains("Failed to compare pantry with recipe"));
  }
}
