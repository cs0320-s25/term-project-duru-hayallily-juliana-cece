import React, { useState, useEffect } from "react";
import { SignOutButton } from "@clerk/clerk-react";
import { addMealPlanItem, getMealPlanItems, clearMealPlanItems } from "../utils/firebaseUtils"; 
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

const MealPlan: React.FC = () => {
  const { user } = useUser();
  const [mealPlans, setMealPlans] = useState<string[]>([]);
  const [input, setInput] = useState<string>("");

  const userId = user?.id;


  useEffect(() => {
    if (userId) {
      getMealPlanItems(userId).then(setMealPlans);
    }
  }, [userId]);

  const handleAdd = async () => {
    const trimmed = input.trim();
    if (!trimmed || mealPlans.includes(trimmed)) return;
    const updated = [...mealPlans, trimmed];
    setMealPlans(updated);
    setInput("");
    if (userId) await addMealPlanItem(userId, trimmed);
  };

  const handleClear = async () => {
    if (userId) {
      await clearMealPlanItems(userId);
      setMealPlans([]);
    }
  };

  const handleInputKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") handleAdd();
  };

  return (
    <div style={cardStyle}>
      <h1 style={{ color: "#3a5a40" }}>🍽️ Your Meal Plan</h1>
      <div>
        <input
          type="text"
          placeholder="Add a meal..."
          value={input}
          onChange={e => setInput(e.target.value)}
          onKeyDown={handleInputKeyDown}
          style={inputStyle}
        />
        <button onClick={handleAdd} style={buttonStyle}>Add</button>
        <button onClick={handleClear} style={{ ...buttonStyle, marginLeft: "12px", background: "#ffb4b4", color: "#a23e3e" }}>
          clear all
        </button>
      </div>

      <ul style={listStyle}>
        {mealPlans.length === 0 ? (
          <li style={{ color: "#8fb996", marginTop: "16px" }}>(No meals planned yet!)</li>
        ) : (
          mealPlans.map(item => (
            <li key={item} style={itemStyle}>
              <span>
                <span role="img" aria-label="meal" style={{ marginRight: 8 }}>🍲</span>
                {item}
              </span>
            </li>
          ))
        )}
      </ul>

      <div style={{ marginTop: "32px" }}>
        <SignOutButton>
          <button style={{ ...buttonStyle, background: "#f9d29d", color: "#b48a54", marginLeft: 0 }}>
            log out
          </button>
        </SignOutButton>
      </div>
    </div>
  );
};

export default MealPlan;
