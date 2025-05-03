package edu.brown.cs.student;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import main.edu.brown.cs.student.main.server.handlers.RecipeHandler;
import main.edu.brown.cs.student.main.server.model.Ingredient;
import main.edu.brown.cs.student.main.server.model.User;
import main.edu.brown.cs.student.main.server.service.MockSpoonacularService;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import spark.Request;
import spark.Response;

import static org.mockito.Mockito.*;

public class RecipeHandlerTest {

  private RecipeHandler handler;
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

    // Add a test user
    User testUser = new User("test-user", "Test User", "test@example.com");
    users.put(testUser.getId(), testUser);

    // Create the handler
    handler = new RecipeHandler(mockService, users);

    // Mock Spark request and response
    mockRequest = mock(Request.class);
    mockResponse = mock(Response.class);

    // Initialize Moshi adapter for parsing responses
    Moshi moshi = new Moshi.Builder().build();
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    adapter = moshi.adapter(type);
  }

  @Test
  public void testHandleSearchNoParams() throws Exception {
    // Set up mock request to return empty query params
    when(mockRequest.queryParams("query")).thenReturn(null);
    when(mockRequest.queryParams("cuisine")).thenReturn(null);
    when(mockRequest.queryParams("diet")).thenReturn(null);
    when(mockRequest.queryParams("intolerances")).thenReturn(null);
    when(mockRequest.queryParams("equipment")).thenReturn(null);
    when(mockRequest.queryParams("includeIngredients")).thenReturn(null);
    when(mockRequest.queryParams("excludeIngredients")).thenReturn(null);
    when(mockRequest.queryParams("type")).thenReturn(null);
    when(mockRequest.queryParams("userId")).thenReturn(null);

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify response structure
    assertEquals("success", responseMap.get("result"));
    assertNotNull(responseMap.get("recipes"));
  }

  @Test
  public void testHandleSearchWithQuery() throws Exception {
    // Set up mock request with a search query
    when(mockRequest.queryParams("query")).thenReturn("cookies");
    when(mockRequest.queryParams("cuisine")).thenReturn(null);
    when(mockRequest.queryParams("diet")).thenReturn(null);
    when(mockRequest.queryParams("intolerances")).thenReturn(null);
    when(mockRequest.queryParams("equipment")).thenReturn(null);
    when(mockRequest.queryParams("includeIngredients")).thenReturn(null);
    when(mockRequest.queryParams("excludeIngredients")).thenReturn(null);
    when(mockRequest.queryParams("type")).thenReturn(null);
    when(mockRequest.queryParams("userId")).thenReturn(null);

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify response
    assertEquals("success", responseMap.get("result"));
    assertNotNull(responseMap.get("recipes"));
  }

  @Test
  public void testHandleSearchWithUser() throws Exception {
    // Set up mock request with a user ID
    when(mockRequest.queryParams("query")).thenReturn("cookies");
    when(mockRequest.queryParams("cuisine")).thenReturn(null);
    when(mockRequest.queryParams("diet")).thenReturn(null);
    when(mockRequest.queryParams("intolerances")).thenReturn(null);
    when(mockRequest.queryParams("equipment")).thenReturn(null);
    when(mockRequest.queryParams("includeIngredients")).thenReturn(null);
    when(mockRequest.queryParams("excludeIngredients")).thenReturn(null);
    when(mockRequest.queryParams("type")).thenReturn(null);
    when(mockRequest.queryParams("userId")).thenReturn("test-user");

    // Add some items to the user's pantry
    User user = users.get("test-user");
    user.getPantry().addIngredient(new Ingredient(1001, "Butter", "Dairy", 0.5, "cup"));
    user.getPantry().addIngredient(new Ingredient(1006, "All-Purpose Flour", "Baking", 2.0, "cups"));

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify response
    assertEquals("success", responseMap.get("result"));
    assertNotNull(responseMap.get("recipes"));
  }

  @Test
  public void testHandleSearchWithAllergen() throws Exception {
    // Set up mock request with intolerances
    when(mockRequest.queryParams("query")).thenReturn("cookies");
    when(mockRequest.queryParams("cuisine")).thenReturn(null);
    when(mockRequest.queryParams("diet")).thenReturn(null);
    when(mockRequest.queryParams("intolerances")).thenReturn("dairy");
    when(mockRequest.queryParams("equipment")).thenReturn(null);
    when(mockRequest.queryParams("includeIngredients")).thenReturn(null);
    when(mockRequest.queryParams("excludeIngredients")).thenReturn(null);
    when(mockRequest.queryParams("type")).thenReturn(null);
    when(mockRequest.queryParams("userId")).thenReturn(null);

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify response
    assertEquals("success", responseMap.get("result"));
    assertNotNull(responseMap.get("recipes"));
  }

  @Test
  public void testHandleSearchWithAllParams() throws Exception {
    // Set up mock request with all possible parameters
    when(mockRequest.queryParams("query")).thenReturn("cookies");
    when(mockRequest.queryParams("cuisine")).thenReturn("american");
    when(mockRequest.queryParams("diet")).thenReturn("vegetarian");
    when(mockRequest.queryParams("intolerances")).thenReturn("dairy,gluten");
    when(mockRequest.queryParams("equipment")).thenReturn("oven");
    when(mockRequest.queryParams("includeIngredients")).thenReturn("chocolate,sugar");
    when(mockRequest.queryParams("excludeIngredients")).thenReturn("nuts");
    when(mockRequest.queryParams("type")).thenReturn("dessert");
    when(mockRequest.queryParams("userId")).thenReturn("test-user");

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify response
    assertEquals("success", responseMap.get("result"));
    assertNotNull(responseMap.get("recipes"));
  }

  @Test
  public void testHandleError() throws Exception {
    // Set up mock request to trigger an error
    when(mockRequest.queryParams("query")).thenReturn("cookies");
    when(mockRequest.queryParams("cuisine")).thenReturn(null);
    when(mockRequest.queryParams("diet")).thenReturn(null);
    when(mockRequest.queryParams("intolerances")).thenReturn(null);
    when(mockRequest.queryParams("equipment")).thenReturn(null);
    when(mockRequest.queryParams("includeIngredients")).thenReturn(null);
    when(mockRequest.queryParams("excludeIngredients")).thenReturn(null);
    when(mockRequest.queryParams("type")).thenReturn(null);
    when(mockRequest.queryParams("userId")).thenReturn("non-existent");

    // Modify the mock service to throw an exception
    MockSpoonacularService errorService = spy(mockService);
    doThrow(new RuntimeException("Test error")).when(errorService).searchRecipes(any());

    // Create handler with error-throwing service
    RecipeHandler errorHandler = new RecipeHandler(errorService, users);

    // Call the handler
    String responseJson = (String) errorHandler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify error response
    assertEquals("error_datasource", responseMap.get("result"));
    assertNotNull(responseMap.get("message"));
  }
}