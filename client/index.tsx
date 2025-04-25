import React from "react";
import ReactDOM from "react-dom/client";
import "./styles/groceryly.css";
import App from "./components/App.tsx";

/**
 * Render the App (mock) element to front-end using React
 */
const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement
);
root.render(
  <React.StrictMode>
    <App children={undefined} modal={undefined} />
  </React.StrictMode>
);