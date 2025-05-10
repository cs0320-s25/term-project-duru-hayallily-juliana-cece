
import { initializeApp, getApps, getApp } from "firebase/app";
import { getDatabase, ref, set, get, child } from "firebase/database";

const firebaseConfig = {
  apiKey:'AIzaSyCqimlNe5AwhTG7o-wNyZLCW0mchaR-5WE',
  authDomain: 'cs320finalproject.firebaseapp.com',
  projectId: 'cs320finalproject',
  storageBucket: 'cs320finalproject.appspot.com',
  messagingSenderId: '23782981764',
  appId: '1:23782981764:web:73094e0015f59e76f802d1',
  databaseURL: `https://cs320finalproject-default-rtdb.firebaseio.com/`,
};

const app = getApps().length === 0 ? initializeApp(firebaseConfig) : getApp();


const db = getDatabase(app); 

export { db, ref, set, get, child };

