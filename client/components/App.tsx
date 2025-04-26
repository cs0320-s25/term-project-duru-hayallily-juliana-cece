import React from "react";
import { Routes, Route, Link, useLocation, Navigate } from "react-router-dom";
import { SignedIn, SignedOut, SignInButton, SignOutButton, UserButton } from "@clerk/clerk-react";
import Dashboard from "./Dashboard";
import MealPlan from "./MealPlan";
import GroceryList from "./GroceryList";
import Profile from "./Profile";

function App() {
  const location = useLocation();

  return (
    <div className="App">
      <nav>
        <div className="nav_content">
          <div className="logo">
            <h1><Link to="/">groceryly</Link></h1>
          </div>
          <ul className="nav_links">
            <li><Link to="/meal-plan" className="btn btn-fill-bottom btn-fill-bottom--green">meal plan</Link></li>
            <li><Link to="/grocery-list" className="btn btn-fill-bottom btn-fill-bottom--green">grocery list</Link></li>
            <li><Link to="/profile" className="btn btn-fill-bottom btn-fill-bottom--green">profile</Link></li>
          </ul>
        </div>
      </nav>

      {/* Only show splash on home page */}
      {location.pathname === "/" && (
        <div className="splash">
          <div className="splash-header">
            <ul>
              <li><h1 aria-label="splash-header">groceryly</h1></li>
              <li><h2>your meal planning solution!</h2></li>
              <SignedOut>
                <SignInButton mode="redirect" redirectUrl="/dashboard">
                  <button className="button">log in</button>
                </SignInButton>
              </SignedOut>
              <SignedIn>
                <UserButton />
                <SignOutButton redirectUrl="/" />
              </SignedIn>
            </ul>
          </div>
        </div>
      )}

      <Routes>
        <Route
          path="/"
          element={
            <SignedIn>
              <Navigate to="/dashboard" replace />
            </SignedIn>
            ||
            <div>Welcome! Please log in.</div>
          }
        />
        <Route path="/dashboard" element={<SignedIn><Dashboard /></SignedIn>} />
        <Route path="/meal-plan" element={<SignedIn><MealPlan /></SignedIn>} />
        <Route path="/grocery-list" element={<SignedIn><GroceryList /></SignedIn>} />
        <Route path="/profile" element={<SignedIn><Profile /></SignedIn>} />
      </Routes>
    </div>
  );
}

export default App;
