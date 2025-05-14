import React, { useState, useEffect } from "react";
import { SignOutButton, useUser } from "@clerk/clerk-react";

// Define interface for GroceryItem
interface GroceryItem {
  name: string;
  checked: boolean;
}

// Styles
const cardStyle = {
  background: "#eafff0",
  borderRadius: "24px",
  padding: "40px 32px",
  boxShadow: "0 4px 24px #b4e2c1",
  maxWidth: "420px",
  margin: "300px auto",
  textAlign: "center" as const,
};

const inputStyle = {
  padding: "10px",
  borderRadius: "8px",
  border: "1px solid #b4e2c1",
  marginRight: "8px",
  fontSize: "1rem",
  flexGrow: 1,
};

const buttonStyle = {
  background: "#b4e2c1",
  color: "#3a5a40",
  border: "none",
  padding: "10px 20px",
  borderRadius: "8px",
  cursor: "pointer",
  fontWeight: "bold" as const,
  fontSize: "1rem",
  marginLeft: "4px",
  marginTop: "8px",
};

const listStyle = {
  listStyle: "none",
  padding: 0,
  marginTop: "24px",
  textAlign: "left" as const,
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

const checkboxStyle = {
  width: "20px",
  height: "20px",
  accentColor: "#8fb996",
  marginRight: "12px",
};

const GroceryList: React.FC = () => {
  const { user } = useUser();
  const userId = user?.id;
  const [groceries, setGroceries] = useState<GroceryItem[]>([]);
  const [input, setInput] = useState<string>("");
  const [error, setError] = useState<string>("");
  const [isLoading, setIsLoading] = useState(false);

  // General fetch function
  const fetchFromAPI = async (
    url: string,
    method: string = "GET",
    body?: any
  ) => {
    try {
      const response = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: body ? JSON.stringify(body) : undefined,
      });
      const data = await response.json();
      if (!response.ok || data.result !== "success") {
        throw new Error(data.message || "Request failed");
      }
      return data;
    } catch (error) {
      setError(error.message);
      console.error(error);
      throw error;
    }
  };

  // Fetch grocery items for the current user
  const fetchGroceryItems = async () => {
    if (!userId) return;

    setIsLoading(true);
    try {
      setError("");
      const data = await fetchFromAPI(
        `http://localhost:8080/api/users/${userId}/grocery`
      );
      const formatted = (data.groceries || []).map((item: any) => ({
        name: item.name,
        checked: item.checked ?? false,
      }));
      setGroceries(formatted);
    } catch (error) {
      setError("Failed to load grocery list");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (userId) {
      fetchGroceryItems();
      const handleStorageChange = (event: StorageEvent) => {
        if (event.key === "grocery-update") {
          fetchGroceryItems();
        }
      };
      window.addEventListener("storage", handleStorageChange);
      return () => window.removeEventListener("storage", handleStorageChange);
    }
  }, [userId]);

  // Add new grocery item to the list
  const addGroceryItem = async (name: string) => {
    try {
      await fetchFromAPI(
        "http://localhost:8080/api/grocery/add-ingredient",
        "POST",
        {
          userId,
          ingredientName: name, // Use correct parameter name
        }
      );
      await fetchGroceryItems(); // Refresh list after adding
    } catch (error) {
      setError("Failed to add item");
    }
  };

  // Handle adding a new item from the input field
  const handleAdd = async () => {
    const trimmed = input.trim();
    if (!trimmed || groceries.some((item) => item.name === trimmed)) return;

    setInput("");
    await addGroceryItem(trimmed);
  };

  // Handle Enter key press for adding a new item
  const handleInputKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") handleAdd();
  };

  // Toggle checked status for grocery items
  const toggleGroceryItem = async (item: GroceryItem) => {
    if (!userId) return;

    const newCheckedStatus = !item.checked;

    // Optimistically update UI
    setGroceries((prev) =>
      prev.map((grocery) =>
        grocery.name === item.name
          ? { ...grocery, checked: newCheckedStatus }
          : grocery
      )
    );

    try {
      if (newCheckedStatus) {
        // When checking an item:
        // 1. First check it in the grocery list
        await fetchFromAPI(
          "http://localhost:8080/api/grocery/check-item",
          "POST",
          {
            userId,
            ingredientName: item.name,
            checked: true,
          }
        );

        // 2. Add to pantry
        await fetchFromAPI(
          "http://localhost:8080/api/pantry/add-ingredient",
          "POST",
          {
            userId,
            ingredientName: item.name,
          }
        );

        // 3. Remove from grocery list
        await fetchFromAPI(
          "http://localhost:8080/api/grocery/delete-ingredient",
          "POST",
          {
            userId,
            ingredientName: item.name,
          }
        );

        // Remove item from UI since it's now in pantry
        setGroceries((prev) => prev.filter((g) => g.name !== item.name));

        console.log(
          `${item.name} moved to pantry and removed from grocery list`
        );
      } else {
        // When unchecking an item (just uncheck it)
        await fetchFromAPI(
          "http://localhost:8080/api/grocery/check-item",
          "POST",
          {
            userId,
            ingredientName: item.name,
            checked: false,
          }
        );
      }
    } catch (error) {
      setError("Failed to update item status");
      console.error("Error updating item:", error);

      // Rollback the optimistic update
      setGroceries((prev) =>
        prev.map((grocery) =>
          grocery.name === item.name
            ? { ...grocery, checked: item.checked }
            : grocery
        )
      );
    }
  };

  // Clear all grocery items
  const handleClearAll = async () => {
    if (!userId) return;

    try {
      await fetchFromAPI("http://localhost:8080/api/grocery/clear", "POST", {
        userId,
      });
      setGroceries([]);
    } catch (error) {
      setError("Failed to clear grocery list");
    }
  };

  // Sort groceries: unchecked first, then checked
  const sortedGroceries = [...groceries].sort((a, b) => {
    if (a.checked === b.checked) return 0;
    return a.checked ? 1 : -1;
  });

  const checkedCount = groceries.filter((item) => item.checked).length;

  return (
    <div style={cardStyle}>
      <h2
        style={{
          color: "#3a5a40",
          fontWeight: "bold",
          fontSize: "2rem",
          marginBottom: "12px",
        }}
      >
        🛒 Grocery List
      </h2>

      {error && (
        <p
          style={{
            color: "#a23e3e",
            marginBottom: "12px",
            fontSize: "0.95rem",
          }}
        >
          {error}
        </p>
      )}

      <div style={{ display: "flex", gap: "8px", marginBottom: "18px" }}>
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={handleInputKeyDown}
          placeholder="Add an item..."
          style={inputStyle}
          disabled={isLoading}
        />
        <button onClick={handleAdd} style={buttonStyle} disabled={isLoading}>
          {isLoading ? "Adding..." : "Add"}
        </button>
        <button
          onClick={handleClearAll}
          style={{
            ...buttonStyle,
            background: "#ffb4b4",
            color: "#a23e3e",
          }}
          disabled={isLoading}
        >
          Clear All
        </button>
      </div>

      {/* Show progress if there are any items */}
      {groceries.length > 0 && (
        <div
          style={{
            marginBottom: "16px",
            fontSize: "0.9rem",
            color: "#8fb996",
            textAlign: "center",
          }}
        >
          {checkedCount} of {groceries.length} items completed
        </div>
      )}

      <ul style={listStyle}>
        {isLoading ? (
          <li
            style={{ color: "#8fb996", marginTop: "16px", textAlign: "center" }}
          >
            Loading grocery items...
          </li>
        ) : groceries.length === 0 ? (
          <li
            style={{ color: "#8fb996", marginTop: "16px", textAlign: "center" }}
          >
            (nothing to buy!)
          </li>
        ) : (
          sortedGroceries.map((item, index) => (
            <li key={`${item.name}-${index}`} style={itemStyle}>
              <label
                style={{
                  display: "flex",
                  alignItems: "center",
                  flexGrow: 1,
                  cursor: "pointer",
                }}
              >
                <input
                  aria-label="grocery add"
                  type="checkbox"
                  checked={item.checked}
                  onChange={() => toggleGroceryItem(item)}
                  style={checkboxStyle}
                />
                <span
                  style={{
                    textDecoration: item.checked ? "line-through" : "none",
                    color: item.checked ? "#8fb996" : "#3a5a40",
                    fontSize: "1rem",
                    marginLeft: "6px",
                  }}
                >
                  {item.name}
                </span>
              </label>
              {item.checked && (
                <span style={{ color: "#4CAF50", fontSize: "1.2rem" }}>✓</span>
              )}
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
            Sign Out
          </button>
        </SignOutButton>
      </div>
    </div>
  );
};

export default GroceryList;
