

import React, { useState, useEffect } from "react";
import { SignOutButton } from "@clerk/clerk-react";
import {
  addMealPlanItem,
  getMealPlanItems,
  clearMealPlanItems,
} from "../utils/firebaseUtils";
import { useUser } from "@clerk/clerk-react";

// Add interfaces for the data types
interface Recipe {
  id: number;
  title: string;
  image?: string;
  readyInMinutes?: number;
  servings?: number;
}

interface MealPlanItem {
  recipeId: number;
  recipeName: string;
  image?: string;
}

const cardStyle = {
  background: "#eafff0",
  borderRadius: "24px",
  padding: "40px 32px",
  boxShadow: "0 4px 24px #b4e2c1",
  maxWidth: "420px",
  margin: "300px auto",
  textAlign: "center",
};

const inputStyle = {
  padding: "10px",
  borderRadius: "8px",
  border: "1px solid #b4e2c1",
  marginRight: "8px",
  fontSize: "1rem",
};

const buttonStyle = {
  background: "#b4e2c1",
  color: "#3a5a40",
  border: "none",
  padding: "10px 20px",
  borderRadius: "8px",
  cursor: "pointer",
  fontWeight: "bold",
  fontSize: "1rem",
  marginLeft: "4px",
  marginTop: "8px",
};

const listStyle = {
  listStyle: "none",
  padding: 0,
  marginTop: "24px",
  textAlign: "left",
};

const itemStyle = {
  background: "#fff",
  borderRadius: "8px",
  padding: "10px 16px",
  marginBottom: "10px",
  display: "flex",
  alignItems: "center",
  justifyContent: "space-between",
  boxShadow: "0 2px 8px #b4e2c133",
};

const recipeResultStyle = {
  background: "#f8f8f8",
  borderRadius: "8px",
  padding: "10px",
  marginBottom: "8px",
  cursor: "pointer",
  border: "1px solid #e0e0e0",
  transition: "background-color 0.2s",
};

const MealPlan: React.FC = () => {
  const { user } = useUser();
  const [mealPlans, setMealPlans] = useState<MealPlanItem[]>([]);
  const [input, setInput] = useState<string>("");
  const [recipeResults, setRecipeResults] = useState<Recipe[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const [searchError, setSearchError] = useState<string>("");

  const userId = user?.id;

  useEffect(() => {
    if (userId) {
      // You'll need to update getMealPlanItems to return MealPlanItem objects
      getMealPlanItems(userId).then((items) => {
        // This assumes you store JSON objects in Firebase
        const parsedItems = items.map((item) =>
          typeof item === "string" ? JSON.parse(item) : item
        );
        setMealPlans(parsedItems);
      });
    }
  }, [userId]);

  // Function to search for recipes
  const searchRecipes = async (query: string) => {
    if (!query.trim()) {
      setRecipeResults([]);
      return;
    }

    setIsSearching(true);
    setSearchError("");

    try {
      console.log(`Searching for: ${query}`); // Debug log

      // Updated to match your Java backend endpoint structure
      const url = `http://localhost:8080/api/recipes/search?query=${encodeURIComponent(
        query
      )}&number=10`;
      console.log(`Fetching from: ${url}`); // Debug log

      const response = await fetch(url);

      if (!response.ok) {
        throw new Error(`API error: ${response.status} ${response.statusText}`);
      }

      const data = await response.json();
      console.log("API Response:", data); // Debug log

      // Your backend returns a JSON object with "result" and "recipes" fields
      if (data.result === "success" && Array.isArray(data.recipes)) {
        setRecipeResults(data.recipes);
      } else if (
        data.result === "error_bad_request" ||
        data.result === "error_datasource"
      ) {
        console.error("API Error:", data.message);
        setSearchError(data.message || "Error fetching recipes");
        setRecipeResults([]);
      } else {
        console.error("Unexpected API response format:", data);
        setSearchError("Unexpected response format from API");
        setRecipeResults([]);
      }
    } catch (error) {
      console.error("Error searching recipes:", error);
      setSearchError(`Error: ${error.message}`);
      setRecipeResults([]);
    } finally {
      setIsSearching(false);
    }
  };

  // Function to add recipe ingredients to grocery list
const addRecipeToGroceryList = async (recipeId: number) => {
  if (!userId) return;

  try {
    console.log(
      `Adding recipe ${recipeId} to grocery list for user ${userId}`
    ); // Debug log

    const response = await fetch(
      `http://localhost:8080/api/grocery/add-recipe`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          userId: userId,
          recipeId: recipeId,
        }),
      }
    );

    if (!response.ok) {
      throw new Error(
        `Failed to add recipe to grocery list: ${response.status}`
      );
    }

    // 🚨 Fix here — get raw text and parse it manually
    const rawText = await response.text();
    const result = JSON.parse(rawText);
    console.log("Add to grocery list result:", result); // Debug log

    if (result.result === "success") {
      // Trigger grocery list refresh
      localStorage.setItem("grocery-update", Date.now().toString());

      // Optional UI message reset
      setTimeout(() => {
        // setSuccessMessage("");
      }, 3000);
    }
  } catch (error: any) {
    console.error("Error adding recipe to grocery list:", error);
    setSearchError(`Error adding to grocery list: ${error.message}`);
  }
};


  // Function to handle adding a recipe to meal plan
  const handleAddRecipe = async (recipe: Recipe) => {
    const mealPlanItem: MealPlanItem = {
      recipeId: recipe.id,
      recipeName: recipe.title,
      image: recipe.image,
    };

    const updated = [...mealPlans, mealPlanItem];
    setMealPlans(updated);
    setInput("");
    setRecipeResults([]);

    if (userId) {
      // Store the meal plan item
      await addMealPlanItem(userId, JSON.stringify(mealPlanItem));

      // Add recipe ingredients to grocery list
      await addRecipeToGroceryList(recipe.id);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setInput(value);

    // Search recipes as user types (debounced)
    clearTimeout((window as any).searchTimeout);
    (window as any).searchTimeout = setTimeout(() => {
      searchRecipes(value);
    }, 500); // Increased debounce time
  };

  const handleClear = async () => {
  if (userId) {
    // Clear meal plan items
    await clearMealPlanItems(userId);
    setMealPlans([]);

    // Clear grocery list as well
    await clearGroceryList(userId);  // New function for clearing grocery list
  }
};

// Function to clear grocery list
const clearGroceryList = async (userId: string) => {
  try {
    const response = await fetch(
      `http://localhost:8080/api/grocery/clear`,  // Adjust the endpoint as needed
      
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          userId: userId,
        }),
      }
    );

    if (!response.ok) {
      throw new Error("Failed to clear grocery list");
    }

    // Optionally update the UI or trigger any additional effects
    console.log("Grocery list cleared successfully");
    localStorage.setItem("grocery-update", Date.now().toString());  // Optional refresh trigger
  } catch (error) {
    console.error("Error clearing grocery list:", error);
  }
};

  return (
    <div style={cardStyle}>
      <h1 style={{ color: "#3a5a40" }}>🍽️ Your Meal Plan</h1>
      <div>
        <input
          type="text"
          placeholder="Search for recipes..."
          value={input}
          onChange={handleInputChange}
          style={inputStyle}
        />
        <button
          onClick={handleClear}
          style={{
            ...buttonStyle,
            marginLeft: "12px",
            background: "#ffb4b4",
            color: "#a23e3e",
          }}
        >
          clear all
        </button>
      </div>

      {/* Search error message */}
      {searchError && (
        <div style={{ color: "red", fontSize: "0.8rem", marginTop: "8px" }}>
          {searchError}
        </div>
      )}

      {/* Recipe search results */}
      {recipeResults.length > 0 && (
        <div
          style={{ marginTop: "16px", maxHeight: "200px", overflowY: "auto" }}
        >
          <h3 style={{ color: "#3a5a40", fontSize: "0.9rem" }}>
            Recipe Results:
          </h3>
          {recipeResults.map((recipe) => (
            <div
              key={recipe.id}
              style={recipeResultStyle}
              onClick={() => handleAddRecipe(recipe)}
              onMouseEnter={(e) => {
                (e.target as HTMLElement).style.backgroundColor = "#e8e8e8";
              }}
              onMouseLeave={(e) => {
                (e.target as HTMLElement).style.backgroundColor = "#f8f8f8";
              }}
            >
              <div style={{ display: "flex", alignItems: "center" }}>
                {recipe.image && (
                  <img
                    src={recipe.image}
                    alt={recipe.title}
                    style={{
                      width: "40px",
                      height: "40px",
                      borderRadius: "4px",
                      marginRight: "10px",
                    }}
                  />
                )}
                <div>
                  <div style={{ fontWeight: "bold", fontSize: "0.9rem" }}>
                    {recipe.title}
                  </div>
                  {(recipe.readyInMinutes || recipe.servings) && (
                    <div style={{ fontSize: "0.8rem", color: "#666" }}>
                      {recipe.readyInMinutes && `${recipe.readyInMinutes} mins`}
                      {recipe.readyInMinutes && recipe.servings && " • "}
                      {recipe.servings && `${recipe.servings} servings`}
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {isSearching && (
        <div style={{ color: "#8fb996", marginTop: "16px" }}>
          Searching recipes...
        </div>
      )}

      {/* Show "No results" message if search completed but no results */}
      {!isSearching &&
        input.trim() &&
        recipeResults.length === 0 &&
        !searchError && (
          <div style={{ color: "#8fb996", marginTop: "16px" }}>
            No recipes found for "{input}"
          </div>
        )}

      <ul style={listStyle}>
        {mealPlans.length === 0 ? (
          <li style={{ color: "#8fb996", marginTop: "16px" }}>
            (No meals planned yet!)
          </li>
        ) : (
          mealPlans.map((item, index) => (
            <li key={`${item.recipeId}-${index}`} style={itemStyle}>
              <div style={{ display: "flex", alignItems: "center" }}>
                {item.image && (
                  <img
                    src={item.image}
                    alt={item.recipeName}
                    style={{
                      width: "40px",
                      height: "40px",
                      borderRadius: "4px",
                      marginRight: "10px",
                    }}
                  />
                )}
                <span>
                  <span role="img" aria-label="meal" style={{ marginRight: 8 }}>
                    🍲
                  </span>
                  {item.recipeName}
                </span>
              </div>
            </li>
          ))
        )}
      </ul>

      <div style={{ marginTop: "32px" }}>
        <SignOutButton>
          <button
            style={{
              ...buttonStyle,
              background: "#f9d29d",
              color: "#b48a54",
              marginLeft: 0,
            }}
          >
            log out
          </button>
        </SignOutButton>
      </div>
    </div>
  );
};

export default MealPlan;

