import React, { useState } from "react";
import { SignOutButton } from "@clerk/clerk-react";

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
  const [groceries, setGroceries] = useState<string[]>([]);
  const [input, setInput] = useState<string>("");

  const handleAdd = () => {
    if (input.trim() && !groceries.includes(input.trim())) {
      setGroceries([...groceries, input.trim()]);
      setInput("");
    }
  };

  const handleRemove = (item: string) => {
    setGroceries(groceries.filter(g => g !== item));
  };

  const handleInputKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "enter") handleAdd();
  };

  return (
    <div style={cardStyle}>
      <h1 style={{ color: "#3a5a40", marginBottom: "8px" }}>
        🛒 your grocery list
      </h1>
      <div>
        <input
          type="text"
          placeholder="add a grocery item..."
          value={input}
          onChange={e => setInput(e.target.value)}
          onKeyDown={handleInputKeyDown}
          style={inputStyle}
        />
        <button onClick={handleAdd} style={buttonStyle}>
          Add
        </button>
      </div>
      <ul style={listStyle}>
        {groceries.length === 0 && (
          <li style={{ color: "#8fb996", marginTop: "16px" }}>
            (nothing to buy!)
          </li>
        )}
        {groceries.map(item => (
          <li key={item} style={itemStyle}>
            <span>
              <span role="img" aria-label="grocery" style={{ marginRight: 8 }}>
                🥕
              </span>
              {item}
            </span>
            <button
              onClick={() => handleRemove(item)}
              style={{
                ...buttonStyle,
                background: "#ffb4b4",
                color: "#a23e3e",
                marginLeft: "12px",
                padding: "6px 14px",
                fontSize: "0.9rem",
              }}
            >
              Remove
            </button>
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
              marginTop: "16px",
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
