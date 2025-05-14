import React, { useState, useEffect } from "react";
import { SignOutButton, useUser } from "@clerk/clerk-react";

const shelfStyle = {
  display: "flex",
  justifyContent: "center",
  flexWrap: "wrap",
  margin: "16px 0",
  gap: "12px",
};

const jarStyle = {
  fontSize: "2.5rem",
  margin: "0 4px",
  filter: "drop-shadow(0 2px 4px #e0c3a5)",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
};

const pantryStyle = {
  background: "#fffbe9",
  borderRadius: "24px",
  padding: "32px",
  boxShadow: "0 4px 24px #e0c3a5",
  maxWidth: "420px",
  margin: "300px auto",
  textAlign: "center",
};

const inputStyle = {
  padding: "10px",
  borderRadius: "8px",
  border: "1px solid #e0c3a5",
  fontSize: "1rem",
  marginRight: "8px",
};

const addButtonStyle = {
  background: "#b4e2c1",
  color: "#3a5a40",
  border: "none",
  padding: "10px 20px",
  borderRadius: "8px",
  cursor: "pointer",
  fontWeight: "bold",
  fontSize: "1rem",
  marginTop: "8px",
};

const removeButtonStyle = {
  background: "#ffb4b4",
  color: "#a23e3e",
  border: "none",
  padding: "6px 14px",
  borderRadius: "8px",
  cursor: "pointer",
  fontWeight: "bold",
  fontSize: "0.9rem",
  marginLeft: "10px",
};

interface PantryItem {
  name: string;
}

const Dashboard: React.FC = () => {
  const { user } = useUser();
  const userId = user?.id;

  const [pantryItems, setPantryItems] = useState<PantryItem[]>([]);
  const [input, setInput] = useState("");
  const [error, setError] = useState<string>("");
  const [isLoading, setIsLoading] = useState(false);

  // General fetch function for API calls
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

  // Fetch pantry items from Java backend
  const fetchPantryItems = async () => {
    if (!userId) return;

    setIsLoading(true);
    try {
      setError("");
      const data = await fetchFromAPI(
        `http://localhost:8080/api/users/${userId}/pantry`
      );

      if (data.pantry) {
        // Extract all ingredients from all categories
        const allItems: PantryItem[] = [];

        if (data.pantry.ingredientsByCategory) {
          Object.values(data.pantry.ingredientsByCategory).forEach(
            (categoryItems: any) => {
              if (Array.isArray(categoryItems)) {
                categoryItems.forEach((item: any) => {
                  allItems.push({ name: item.name });
                });
              }
            }
          );
        }

        setPantryItems(allItems);
      }
    } catch (error) {
      setError("Failed to load pantry items");
      console.error("Error fetching pantry items:", error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (userId) {
      fetchPantryItems();

      // Listen for storage events (for cross-tab updates)
      const handleStorageChange = (event: StorageEvent) => {
        if (event.key === "pantry-update") {
          fetchPantryItems();
        }
      };
      window.addEventListener("storage", handleStorageChange);
      return () => window.removeEventListener("storage", handleStorageChange);
    }
  }, [userId]);

  // Add item to pantry
  const handleAdd = async () => {
    const trimmed = input.trim();
    if (!trimmed || !userId) return;
    if (
      pantryItems.some(
        (item) => item.name.toLowerCase() === trimmed.toLowerCase()
      )
    )
      return;

    // Optimistically update UI
    const newItem = { name: trimmed };
    setPantryItems([...pantryItems, newItem]);
    setInput("");

    try {
      await fetchFromAPI(
        "http://localhost:8080/api/pantry/add-ingredient",
        "POST",
        {
          userId,
          ingredientName: trimmed,
        }
      );
    } catch (error) {
      // Revert optimistic update on error
      setPantryItems(pantryItems.filter((item) => item.name !== trimmed));
      setError("Failed to add item to pantry");
    }
  };

  // Remove item from pantry
  const handleRemove = async (name: string) => {
    if (!userId) return;

    // Optimistically update UI
    setPantryItems(pantryItems.filter((item) => item.name !== name));

    try {
      await fetchFromAPI(
        "http://localhost:8080/api/pantry/remove-ingredient",
        "DELETE",
        {
          userId,
          ingredientName: name,
        }
      );
    } catch (error) {
      // Revert optimistic update on error
      setPantryItems([...pantryItems, { name }]);
      setError("Failed to remove item from pantry");
    }
  };

  // Clear all pantry items
  const handleClear = async () => {
    if (!userId) return;

    // Optimistically update UI
    const currentItems = [...pantryItems];
    setPantryItems([]);

    try {
      await fetchFromAPI("http://localhost:8080/api/pantry/clear", "DELETE", {
        userId,
      });
    } catch (error) {
      // Revert optimistic update on error
      setPantryItems(currentItems);
      setError("Failed to clear pantry");
    }
  };

  const handleInputKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") handleAdd();
  };

  return (
    <div style={pantryStyle}>
      <h1 style={{ color: "#b48a54" }}>
        🧺 Welcome to your pantry, {user?.firstName || "!"}
      </h1>

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

      <p style={{ color: "#b48a54" }}>Here are your pantry items:</p>

      <div style={shelfStyle}>
        {isLoading ? (
          <span style={{ color: "#e0c3a5" }}>Loading pantry items...</span>
        ) : pantryItems.length === 0 ? (
          <span style={{ color: "#e0c3a5" }}>(Your pantry is empty!)</span>
        ) : (
          pantryItems.map((item, index) => (
            <span
              key={`${item.name}-${index}`}
              style={jarStyle}
              title={item.name}
            >
              <span style={{ fontSize: "1rem", color: "#b48a54" }}>
                {item.name}
              </span>
              <button
                onClick={() => handleRemove(item.name)}
                style={removeButtonStyle}
                title={`Remove ${item.name}`}
                disabled={isLoading}
              >
                ×
              </button>
            </span>
          ))
        )}
      </div>

      <div style={{ margin: "24px 0 0 0" }}>
        <input
          type="text"
          placeholder="Add pantry item..."
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={handleInputKeyDown}
          style={inputStyle}
          disabled={isLoading}
        />
        <button onClick={handleAdd} style={addButtonStyle} disabled={isLoading}>
          {isLoading ? "Adding..." : "Add"}
        </button>
      </div>

      <div style={{ marginTop: "32px" }}>
        <button
          onClick={handleClear}
          style={{
            background: "#ffb4b4",
            color: "#a23e3e",
            border: "none",
            padding: "12px 32px",
            borderRadius: "8px",
            cursor: "pointer",
            fontWeight: "bold",
            fontSize: "1rem",
            boxShadow: "0 2px 8px #e0c3a5",
          }}
          disabled={isLoading}
        >
          clear all
        </button>
      </div>

      <div style={{ marginTop: "32px" }}>
        <SignOutButton>
          <button
            style={{
              background: "#f9d29d",
              color: "#b48a54",
              border: "none",
              padding: "12px 32px",
              borderRadius: "8px",
              cursor: "pointer",
              fontWeight: "bold",
              fontSize: "1rem",
              boxShadow: "0 2px 8px #e0c3a5",
            }}
          >
            log out
          </button>
        </SignOutButton>
      </div>
    </div>
  );
};

export default Dashboard;
