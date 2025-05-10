package edu.brown.cs.student;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.edu.brown.cs.student.main.server.handlers.AddAllergenHandler;
import main.edu.brown.cs.student.main.server.model.User;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Response;

public class AddAllergenHandlerTest {

  private AddAllergenHandler handler;
  private Map<String, User> users;
  private Request mockRequest;
  private Response mockResponse;
  private JsonAdapter<Map<String, Object>> adapter;

  @Before
  public void setUp() {
    // Initialize users map
    users = new HashMap<>();

    // Add a test user
    User testUser = new User("test-user", "Test User", "test@example.com");
    users.put(testUser.getId(), testUser);

    // Create the handler
    handler = new AddAllergenHandler(users);

    // Mock Spark request and response
    mockRequest = mock(Request.class);
    mockResponse = mock(Response.class);

    // Initialize Moshi adapter for parsing responses
    Moshi moshi = new Moshi.Builder().build();
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    adapter = moshi.adapter(type);
  }

  @Test
  public void testHandleAddAllergen() throws Exception {
    // Create request body JSON
    String requestBody = "{\"userId\":\"test-user\",\"allergen\":\"dairy\"}";

    // Set up mock request to return the JSON body
    when(mockRequest.body()).thenReturn(requestBody);

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify successful response
    assertEquals("success", responseMap.get("result"));
    assertEquals("Allergen added successfully", responseMap.get("message"));

    // Verify allergen was added to user
    List<String> allergens = (List<String>) responseMap.get("allergens");
    assertNotNull(allergens);
    assertTrue(allergens.contains("dairy"));

    // Verify actual user object was updated
    User user = users.get("test-user");
    assertTrue(user.getAllergies().contains("dairy"));
  }

  @Test
  public void testHandleMissingUserId() throws Exception {
    // Create request body JSON with missing userId
    String requestBody = "{\"allergen\":\"dairy\"}";

    // Set up mock request to return the JSON body
    when(mockRequest.body()).thenReturn(requestBody);

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify error response
    assertEquals("error_bad_request", responseMap.get("result"));
    assertTrue(((String) responseMap.get("message")).contains("User ID and allergen are required"));
  }

  @Test
  public void testHandleMissingAllergen() throws Exception {
    // Create request body JSON with missing allergen
    String requestBody = "{\"userId\":\"test-user\"}";

    // Set up mock request to return the JSON body
    when(mockRequest.body()).thenReturn(requestBody);

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify error response
    assertEquals("error_bad_request", responseMap.get("result"));
    assertTrue(((String) responseMap.get("message")).contains("User ID and allergen are required"));
  }

  @Test
  public void testHandleInvalidAllergen() throws Exception {
    // Create request body JSON with invalid allergen
    String requestBody = "{\"userId\":\"test-user\",\"allergen\":\"invalid-allergen\"}";

    // Set up mock request to return the JSON body
    when(mockRequest.body()).thenReturn(requestBody);

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify error response
    assertEquals("error_bad_request", responseMap.get("result"));
    assertTrue(((String) responseMap.get("message")).contains("Unsupported allergen"));
  }

  @Test
  public void testHandleNewUser() throws Exception {
    // Create request body JSON with a new user ID
    String requestBody = "{\"userId\":\"new-user\",\"allergen\":\"gluten\"}";

    // Set up mock request to return the JSON body
    when(mockRequest.body()).thenReturn(requestBody);

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify successful response
    assertEquals("success", responseMap.get("result"));

    // Verify a new user was created
    User newUser = users.get("new-user");
    assertNotNull("New user should be created", newUser);
    assertTrue(newUser.getAllergies().contains("gluten"));
  }

  @Test
  public void testHandleDuplicateAllergen() throws Exception {
    // Add an allergen to the test user
    User user = users.get("test-user");
    user.addAllergy("dairy");

    // Create request body JSON with the same allergen
    String requestBody = "{\"userId\":\"test-user\",\"allergen\":\"dairy\"}";

    // Set up mock request to return the JSON body
    when(mockRequest.body()).thenReturn(requestBody);

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify successful response (should succeed even for duplicates)
    assertEquals("success", responseMap.get("result"));

    // Verify allergens list contains the allergen only once
    List<String> allergens = (List<String>) responseMap.get("allergens");
    assertEquals(1, allergens.size());
    assertEquals("dairy", allergens.get(0));
  }

  @Test
  public void testHandleInvalidJson() throws Exception {
    // Set up mock request to return invalid JSON
    when(mockRequest.body()).thenReturn("not valid json");

    // Call the handler
    String responseJson = (String) handler.handle(mockRequest, mockResponse);

    // Parse the response
    Map<String, Object> responseMap = adapter.fromJson(responseJson);

    // Verify error response
    assertEquals("error_bad_request", responseMap.get("result"));
    assertTrue(((String) responseMap.get("message")).contains("Invalid JSON body"));
  }
}
