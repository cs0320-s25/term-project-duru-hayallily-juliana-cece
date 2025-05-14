import React, { useEffect } from "react";
import { Routes, Route, Link, useLocation, useNavigate  } from "react-router-dom";
import { SignedIn, SignedOut, SignInButton, SignOutButton, UserButton, useUser } from "@clerk/clerk-react";
import Dashboard from "./Dashboard";
import MealPlan from "./MealPlan";
import GroceryList from "./GroceryList";
import Profile from "./Profile";
import { initializeApp } from "firebase/app";

function App() {
  const location = useLocation();
  const navigate = useNavigate();
  const { isSignedIn } = useUser();

  useEffect(() => {
    if (isSignedIn && location.pathname === "/") {
      navigate("/dashboard");
    }
  }, [isSignedIn, location.pathname, navigate]);

  
  

  const firebaseConfig = {
    apiKey:'AIzaSyCqimlNe5AwhTG7o-wNyZLCW0mchaR-5WE',
    authDomain: 'cs320finalproject.firebaseapp.com',
    projectId: 'cs320finalproject',
    storageBucket: 'cs320finalproject.appspot.com',
    messagingSenderId: '23782981764',
    appId: '1:23782981764:web:73094e0015f59e76f802d1',
    databaseURL: `https://cs320finalproject-default-rtdb.firebaseio.com/`,
  };

  initializeApp(firebaseConfig);

  


  return (
    <div className="App">
      <nav>
        <div className="nav_content">
          <div className="logo">
            <h1>
              <Link to="/">groceryly</Link>
            </h1>
          </div>
          <SignedIn>
            <ul className="nav_links">
              <li>
                <Link
                  to="/"
                  className="btn btn-fill-bottom btn-fill-bottom--green"
                >
                  pantry
                </Link>
              </li>

              <li>
                <Link
                  to="/meal-plan"
                  className="btn btn-fill-bottom btn-fill-bottom--green"
                >
                  recipes
                </Link>
              </li>
              <li>
                <Link
                  to="/grocery-list"
                  className="btn btn-fill-bottom btn-fill-bottom--green"
                >
                  grocery list
                </Link>
              </li>
              <li>
                <Link
                  to="/profile"
                  className="btn btn-fill-bottom btn-fill-bottom--green"
                >
                  profile
                </Link>
              </li>
            </ul>
          </SignedIn>
          <SignedOut>
            <ul className="nav_links">
              <li>
                <SignInButton mode="modal" redirectUrl="/dashboard">
                  <p className="btn btn-fill-bottom btn-fill-bottom--green">
                    <Link>log in</Link>
                  </p>
                </SignInButton>
              </li>
            </ul>
          </SignedOut>
        </div>
      </nav>

      {/* Only show splash on home page */}
      {location.pathname === "/" && (
        <div className="splash">
          <div className="splash-header">
            <ul>
              <li>
                <h1 aria-label="splash-header">groceryly</h1>
              </li>
              <li>
                <h2>your meal planning solution!</h2>
              </li>
              <SignedOut>
                <SignInButton mode="modal" redirectUrl="/dashboard">
                  <button className="button" aria-label="sign in">sign up</button>
                </SignInButton>
              </SignedOut>
              <SignedIn>
                <UserButton />
                <SignOutButton />
              </SignedIn>
            </ul>
          </div>
        </div>
      )}

      <Routes>
        <Route path="/" element={<div>Welcome! Please log in.</div>} />
        <Route
          path="/dashboard"
          element={
            <SignedIn>
              <Dashboard />
            </SignedIn>
          }
        />
        <Route
          path="/meal-plan"
          element={
            <SignedIn>
              <MealPlan />
            </SignedIn>
          }
        />
        <Route
          path="/grocery-list"
          element={
            <SignedIn>
              <GroceryList />
            </SignedIn>
          }
        />
        <Route
          path="/profile"
          element={
            <SignedIn>
              <Profile />
            </SignedIn>
          }
        />
      </Routes>
    </div>
  );
}

export default App;
