import React, { useState } from "react";
import { SignOutButton } from "@clerk/clerk-react";

const cardStyle = {
  background: "#ffe9ec",
  borderRadius: "24px",
  padding: "40px 32px",
  boxShadow: "0 4px 24px #f7b2b2",
  maxWidth: "420px",
  margin: "300px auto",
  textAlign: "center",
};

const inputStyle = {
  padding: "10px",
  borderRadius: "8px",
  border: "1px solid #f7b2b2",
  marginRight: "8px",
  fontSize: "1rem",
};

const buttonStyle = {
  background: "#f7b2b2",
  color: "#a23e3e",
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
  boxShadow: "0 2px 8px #f7b2b266",
};

const MealPlan: React.FC = () => {
  const [recipes, setRecipes] = useState<string[]>([]);
  const [input, setInput] = useState<string>("");

  const handleAdd = () => {
    if (input.trim() && !recipes.includes(input.trim())) {
      setRecipes([...recipes, input.trim()]);
      setInput("");
    }
  };

  const handleRemove = (item: string) => {
    setRecipes(recipes.filter(r => r !== item));
  };

  const handleInputKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") handleAdd();
  };

  return (
    <div style={cardStyle}>
      <h1 style={{ color: "#a23e3e", marginBottom: "8px" }}>
        🍽️ plan your meals!
      </h1>
      <p style={{ color: "#a23e3e", marginBottom: "16px" }}>
        Add recipes to your weekly meal plan:
      </p>
      <div>
        <input
          type="text"
          placeholder="e.g. Spaghetti Carbonara"
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
        {recipes.length === 0 && (
          <li style={{ color: "#f7b2b2", marginTop: "16px" }}>
            (no recipes planned yet!)
          </li>
        )}
        {recipes.map(item => (
          <li key={item} style={itemStyle}>
            <span>
              <span role="img" aria-label="recipe" style={{ marginRight: 8 }}>
                🍲
              </span>
              {item}
            </span>
            <button
              onClick={() => handleRemove(item)}
              style={{
                ...buttonStyle,
                background: "#ffe9ec",
                color: "#a23e3e",
                marginLeft: "12px",
                padding: "6px 14px",
                fontSize: "0.9rem",
                border: "1px solid #f7b2b2",
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
              border: "none",
            }}
          >
            Log Out
          </button>
        </SignOutButton>
      </div>
    </div>
  );
};

export default MealPlan;
