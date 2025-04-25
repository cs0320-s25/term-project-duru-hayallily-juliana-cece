import React from 'react';
import "../styles/groceryly.css"

function App({}: { children: React.ReactNode; modal: React.ReactNode }) {
    /**
     * A state tracker for if the user is logged in and
     *  a function to update the logged-in state
     */
  
    return (
      <div className="App">
        <nav>
            <div className="nav_content">
                <div className="logo">
                    <h1><a href="#">groceryly</a></h1>
                </div>
                <ul className="nav_links">
                    <li><a className="btn btn-fill-bottom btn-fill-bottom--green"> meal plan </a></li>
                    <li><a className="btn btn-fill-bottom btn-fill-bottom--green"> grocery list </a></li>
                    <li><a className="btn btn-fill-bottom btn-fill-bottom--green"> profile </a></li>
                </ul>
            </div>
        </nav>
        <div className="splash">
            <div className="splash-header">
                <ul>
                    <li><h1 aria-label="splash-header">groceryly</h1></li>
                    <li><h2>your meal planning solution!</h2></li>
                    <li><button class="button">log in</button></li>
                </ul>
                
                
            </div>
        </div>
      </div>
    );
  }
  export default App;