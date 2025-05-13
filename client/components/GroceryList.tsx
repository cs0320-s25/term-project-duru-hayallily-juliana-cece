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
  margin: "100px auto",
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

  // Fetch grocery items for the current user
  const fetchGroceryItems = async () => {
    try {
      if (!userId) return;
  
      const response = await fetch(`http://localhost:8080/api/users/${userId}/grocery`);
      const data = await response.json();
  
      if (data.result === "success") {
        const items = data.groceries || [];
        const formatted = items.map((item: any) => ({
          name: item.name,
          checked: item.checked ?? false,
        }));
        setGroceries(formatted);
      } else {
        throw new Error("Failed to load grocery items");
      }
    } catch (error) {
      console.error("Error fetching grocery items:", error);
      setError("Failed to load grocery list");
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
      await fetch("http://localhost:8080/api/grocery/add-ingredient", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userId, name }),
      });
    } catch (error) {
      console.error("Error adding grocery item:", error);
      setError("Failed to add item");
    }
  };

  // Handle adding a new item from the input field
  const handleAdd = async () => {
    const trimmed = input.trim();
    if (!trimmed || groceries.some((item) => item.name === trimmed)) return;

    const newGrocery = { name: trimmed, checked: false };
    setGroceries([...groceries, newGrocery]);
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

    setGroceries((prev) =>
      prev.map((grocery) =>
        grocery.name === item.name ? { ...grocery, checked: newCheckedStatus } : grocery
      )
    );

    try {
      const checkResponse = await fetch("http://localhost:8080/api/grocery/add-ingredient", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          userId,
          ingredientName: item.name,
          checked: newCheckedStatus,
        }),
      });

      const checkData = await checkResponse.json();
      if (!checkResponse.ok || checkData.result !== "success") {
        throw new Error("Failed to update grocery item");
      }

      if (newCheckedStatus) {
        const addToPantryResponse = await fetch("http://localhost:8080/api/pantry/add-ingredient", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ userId, name: item.name }),
        });

        const pantryData = await addToPantryResponse.json();
        if (!addToPantryResponse.ok || pantryData.result !== "success") {
          throw new Error("Failed to add item to pantry");
        }

        setGroceries((prev) => prev.filter((g) => g.name !== item.name));
      }
    } catch (error) {
      setError("Failed to move item to pantry");
      console.error("Error moving item to pantry:", error);
      setGroceries((prev) =>
        prev.map((grocery) =>
          grocery.name === item.name ? { ...grocery, checked: !newCheckedStatus } : grocery
        )
      );
    }
  };

  return (
    <div style={cardStyle}>
      <h2 style={{ color: "#3a5a40", fontWeight: "bold", fontSize: "2rem", marginBottom: "12px" }}>
        🛒 Grocery List
      </h2>
      {error && <p style={{ color: "#a23e3e", marginBottom: "12px", fontSize: "0.95rem" }}>{error}</p>}
      <div style={{ display: "flex", gap: "8px", marginBottom: "18px" }}>
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={handleInputKeyDown}
          placeholder="Add an item..."
          style={inputStyle}
        />
        <button onClick={handleAdd} style={buttonStyle}>
          Add
        </button>
      </div>
      <ul style={listStyle}>
        {groceries.map((item, index) => (
            <li key={`${item.name}-${index}`} style={itemStyle}>
              <label style={{ display: "flex", alignItems: "center", flexGrow: 1 }}>
                <input
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
            </li>
          ))}

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
