import React, { useState, useEffect } from "react";
import { SignOutButton } from "@clerk/clerk-react";

import { addGroceryItem, getGroceryItems, clearGroceryItems } from "../utils/firebaseUtils";
import { useUser } from "@clerk/clerk-react";


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

const GroceryList: React.FC = () => {
  const { user } = useUser();
  const [groceries, setGroceries] = useState<string[]>([]);
  const [input, setInput] = useState<string>("");

  const userId = user?.id;

  useEffect(() => {
    if (userId) {
      getGroceryItems(userId).then(setGroceries);
    }
  }, [userId]);

  const handleAdd = async () => {
    const trimmed = input.trim();
    if (!trimmed || groceries.includes(trimmed)) return;
    const updated = [...groceries, trimmed];
    setGroceries(updated);
    setInput("");
    if (userId) await addGroceryItem(userId, trimmed);
  };

  const handleClear = async () => {
    if (userId) {
      await clearGroceryItems(userId);
      setGroceries([]);
    }
  };

  const handleInputKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") handleAdd();
  };

  return (
    <div style={cardStyle}>
      <h1 style={{ color: "#3a5a40" }}>🛒 your grocery list</h1>
      <div>
        <input
          type="text"
          placeholder="add a grocery item..."
          value={input}
          onChange={e => setInput(e.target.value)}
          onKeyDown={handleInputKeyDown}
          style={inputStyle}
        />
        <button onClick={handleAdd} style={buttonStyle}>Add</button>
        <button onClick={handleClear} style={{ ...buttonStyle, marginLeft: "12px", background: "#ffb4b4", color: "#a23e3e" }}>
          Clear All
        </button>
      </div>

      <ul style={listStyle}>
        {groceries.length === 0 ? (
          <li style={{ color: "#8fb996", marginTop: "16px" }}>(nothing to buy!)</li>
        ) : (
          groceries.map(item => (
            <li key={item} style={itemStyle}>
              <span>
                <span role="img" aria-label="grocery" style={{ marginRight: 8 }}>🥕</span>
                {item}
              </span>
            </li>
          ))
        )}
      </ul>

      <div style={{ marginTop: "32px" }}>
        <SignOutButton>
          <button style={{ ...buttonStyle, background: "#f9d29d", color: "#b48a54", marginLeft: 0 }}>
            Log Out
          </button>
        </SignOutButton>
      </div>
    </div>
  );
};

export default GroceryList;