import React, { useState } from "react";
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

const pantryEmojiSuggestions = ["🍝", "🍞", "🥛", "🍯", "🍏", "🍅", "🥚", "🧀", "🥦", "🍗"];

const Dashboard: React.FC = () => {
  const { user } = useUser();
  const [pantryItems, setPantryItems] = useState([
    { name: "Pasta", emoji: "🍝" },
    { name: "Bread", emoji: "🍞" },
    { name: "Milk", emoji: "🥛" },
    { name: "Jam", emoji: "🍯" },
    { name: "Apple", emoji: "🍏" },
  ]);
  const [input, setInput] = useState("");
  const [emoji, setEmoji] = useState(pantryEmojiSuggestions[0]);

  const handleAdd = () => {
    const trimmed = input.trim();
    if (!trimmed) return;
    if (pantryItems.some(item => item.name.toLowerCase() === trimmed.toLowerCase())) return;
    setPantryItems([...pantryItems, { name: trimmed, emoji }]);
    setInput("");
    setEmoji(pantryEmojiSuggestions[0]);
  };

  const handleRemove = (name: string) => {
    setPantryItems(pantryItems.filter(item => item.name !== name));
  };

  const handleInputKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") handleAdd();
  };

  return (
    <div style={pantryStyle}>
      <h1 style={{ color: "#b48a54" }}>
        🧺 welcome to your pantry, {user?.firstName || "!"}!
      </h1>
      <p style={{ color: "#b48a54" }}>
        here are your favorite pantry items:
      </p>
      <div style={shelfStyle}>
        {pantryItems.length === 0 && (
          <span style={{ color: "#e0c3a5" }}>(Your pantry is empty!)</span>
        )}
        {pantryItems.map(item => (
          <span key={item.name} style={jarStyle} title={item.name}>
            <span style={{ marginRight: 4 }}>{item.emoji}</span>
            <span style={{ fontSize: "1rem", color: "#b48a54" }}>{item.name}</span>
            <button
              onClick={() => handleRemove(item.name)}
              style={removeButtonStyle}
              title={`Remove ${item.name}`}
            >
              ×
            </button>
          </span>
        ))}
      </div>
      <div style={{ margin: "24px 0 0 0" }}>
        <input
          type="text"
          placeholder="Add pantry item..."
          value={input}
          onChange={e => setInput(e.target.value)}
          onKeyDown={handleInputKeyDown}
          style={inputStyle}
        />
        <select
          value={emoji}
          onChange={e => setEmoji(e.target.value)}
          style={{
            ...inputStyle,
            width: "52px",
            padding: "10px 12px",
            fontSize: "1.3rem",
            marginRight: "10px",
          }}
        >
          {pantryEmojiSuggestions.map(e => (
            <option key={e} value={e}>{e}</option>
          ))}
        </select>
        <button onClick={handleAdd} style={addButtonStyle}>
          Add
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
              transition: "background 0.2s",
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
