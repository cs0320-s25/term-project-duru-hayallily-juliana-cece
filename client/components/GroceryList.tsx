import React, { useState, useEffect } from "react";
import { SignOutButton } from "@clerk/clerk-react";
import { useUser } from "@clerk/clerk-react";

interface GroceryItem {
  name: string;
  checked: boolean;
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
  cursor: "pointer",
  transition: "all 0.2s ease",
};

const checkedItemStyle = {
  ...itemStyle,
  background: "#f0f8f0",
  opacity: 0.7,
};

const checkboxStyle = {
  width: "20px",
  height: "20px",
  marginRight: "10px",
  cursor: "pointer",
};

const GroceryList: React.FC = () => {
  const { user } = useUser();
  const [groceries, setGroceries] = useState<GroceryItem[]>([]);
  const [input, setInput] = useState<string>("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string>("");

  const userId = user?.id;

  // Fetch grocery items from Java backend
  const fetchGroceryItems = async () => {
    if (!userId) return;

    setIsLoading(true);
    setError("");

    try {
      const response = await fetch(
        `http://localhost:8080/api/users/${userId}/grocery`
      );

      if (!response.ok) {
        throw new Error(`Failed to fetch grocery items: ${response.status}`);
      }

      const data = await response.json();

      if (data.result === "success") {
        setGroceries(data.groceries || []);
      } else {
        throw new Error(data.message || "Failed to fetch grocery items");
      }
    } catch (error) {
      console.error("Error fetching grocery items:", error);
      setError(error.message);
    } finally {
      setIsLoading(false);
    }
  };

  // Add new grocery item via backend
  const addGroceryItem = async (item: string) => {
    if (!userId) return;

    try {
      const response = await fetch(
        `http://localhost:8080/api/grocery/add-ingredient`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            userId: userId,
            ingredientName: item,
          }),
        }
      );

      if (!response.ok) {
        throw new Error(`Failed to add grocery item: ${response.status}`);
      }

      const data = await response.json();

      if (data.result === "success") {
        // Refresh the grocery list
        fetchGroceryItems();
      } else {
        throw new Error(data.message || "Failed to add grocery item");
      }
    } catch (error) {
      console.error("Error adding grocery item:", error);
      setError(error.message);
    }
  };

  // Check/uncheck a grocery item
  const toggleGroceryItem = async (item: GroceryItem) => {
    if (!userId) return;

    // Optimistically update UI
    const newCheckedStatus = !item.checked;
    setGroceries(
      groceries.map((grocery) =>
        grocery.name === item.name
          ? { ...grocery, checked: newCheckedStatus }
          : grocery
      )
    );

    try {
      const response = await fetch(
        `http://localhost:8080/api/grocery/check-item`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            userId: userId,
            ingredientName: item.name,
            checked: newCheckedStatus,
          }),
        }
      );

      if (!response.ok) {
        throw new Error(`Failed to update grocery item: ${response.status}`);
      }

      const data = await response.json();

      if (data.result !== "success") {
        // Revert optimistic update if backend fails
        setGroceries(
          groceries.map((grocery) =>
            grocery.name === item.name
              ? { ...grocery, checked: item.checked }
              : grocery
          )
        );
        throw new Error(data.message || "Failed to update grocery item");
      }
    } catch (error) {
      console.error("Error updating grocery item:", error);
      setError(error.message);
      // Revert optimistic update
      setGroceries(
        groceries.map((grocery) =>
          grocery.name === item.name
            ? { ...grocery, checked: item.checked }
            : grocery
        )
      );
    }
  };

  // Clear all grocery items
  const handleClear = async () => {
    if (!userId) return;

    try {
      // Note: You might need to create a clear endpoint in your backend
      // For now, let's just refresh the list
      // await fetch(`http://localhost:8080/api/users/${userId}/grocery/clear`, { method: 'DELETE' });
      setGroceries([]);
    } catch (error) {
      console.error("Error clearing grocery items:", error);
      setError(error.message);
    }
  };

  useEffect(() => {
    if (userId) {
      fetchGroceryItems();
    }
  }, [userId]);

  const handleAdd = async () => {
    const trimmed = input.trim();
    if (!trimmed || groceries.some((item) => item.name === trimmed)) return;

    // Optimistically update UI
    setGroceries([...groceries, { name: trimmed, checked: false }]);
    setInput("");

    // Add to backend
    await addGroceryItem(trimmed);
  };

  const handleInputKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") handleAdd();
  };

  // Sort groceries: unchecked items first, then checked items
  const sortedGroceries = [...groceries].sort((a, b) => {
    if (a.checked === b.checked) return 0;
    return a.checked ? 1 : -1;
  });

  const checkedCount = groceries.filter((item) => item.checked).length;

  return (
    <div style={cardStyle}>
      <h1 style={{ color: "#3a5a40" }}>🛒 your grocery list</h1>

      {error && (
        <div style={{ color: "red", fontSize: "0.8rem", marginBottom: "16px" }}>
          {error}
        </div>
      )}

      <div>
        <input
          type="text"
          placeholder="add a grocery item..."
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={handleInputKeyDown}
          style={inputStyle}
          disabled={isLoading}
        />
        <button onClick={handleAdd} style={buttonStyle} disabled={isLoading}>
          {isLoading ? "Adding..." : "Add"}
        </button>
        <button
          onClick={handleClear}
          style={{
            ...buttonStyle,
            marginLeft: "12px",
            background: "#ffb4b4",
            color: "#a23e3e",
          }}
        >
          Clear All
        </button>
      </div>

      {/* Show progress */}
      {groceries.length > 0 && (
        <div
          style={{
            marginTop: "16px",
            fontSize: "0.9rem",
            color: "#8fb996",
            textAlign: "center",
          }}
        >
          {checkedCount} of {groceries.length} items checked
        </div>
      )}

      <ul style={listStyle}>
        {isLoading ? (
          <li style={{ color: "#8fb996", marginTop: "16px" }}>
            Loading grocery items...
          </li>
        ) : groceries.length === 0 ? (
          <li style={{ color: "#8fb996", marginTop: "16px" }}>
            (nothing to buy!)
          </li>
        ) : (
          sortedGroceries.map((item, index) => (
            <li
              key={`${item.name}-${index}`}
              style={item.checked ? checkedItemStyle : itemStyle}
              onClick={() => toggleGroceryItem(item)}
            >
              <div style={{ display: "flex", alignItems: "center" }}>
                <input
                  type="checkbox"
                  checked={item.checked}
                  onChange={() => toggleGroceryItem(item)}
                  style={checkboxStyle}
                  onClick={(e) => e.stopPropagation()} // Prevent double toggle
                />
                <span
                  style={{
                    textDecoration: item.checked ? "line-through" : "none",
                    color: item.checked ? "#888" : "inherit",
                  }}
                >
                  <span
                    role="img"
                    aria-label="grocery"
                    style={{ marginRight: 8 }}
                  >
                    🥕
                  </span>
                  {item.name}
                </span>
              </div>
              {item.checked && (
                <span
                  role="img"
                  aria-label="checked"
                  style={{ color: "#4CAF50" }}
                >
                  ✓
                </span>
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
            Log Out
          </button>
        </SignOutButton>
      </div>
    </div>
  );
};

export default GroceryList;
